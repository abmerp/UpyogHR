package org.egov.tlcalculator.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;

import org.egov.tlcalculator.config.TLCalculatorConfigs;
import org.egov.tlcalculator.repository.CalculationRepository;
import org.egov.tlcalculator.repository.DemandRepository;
import org.egov.tlcalculator.repository.ServiceRequestRepository;
import org.egov.tlcalculator.repository.builder.CalculationQueryBuilder;
import org.egov.tlcalculator.utils.CalculationUtils;
import org.egov.tlcalculator.web.models.*;
import org.egov.tlcalculator.web.models.demand.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.awt.Desktop;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import static org.egov.tlcalculator.utils.TLCalculatorConstants.BILLINGSLAB_KEY;
import static org.egov.tlcalculator.utils.TLCalculatorConstants.MDMS_ROUNDOFF_TAXHEAD;
import static org.egov.tlcalculator.utils.TLCalculatorConstants.businessService_BPA;
import static org.egov.tlcalculator.utils.TLCalculatorConstants.businessService_TL;

@Service
@Log4j2
public class AccessDemandService {

	@Autowired
	private CalculationService calculationService;

	@Autowired
	private CalculationUtils utils;

	@Autowired
	private TLCalculatorConfigs config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private MDMSService mdmsService;
	
	@Autowired
	private RestTemplate restTemplate;


	@Autowired
	private CalculationRepository calculationRepository;

	@Autowired
	private CalculationQueryBuilder calculationQueryBuilder;
	
	/**
	 * Creates or updates Demand
	 * 
	 * @param requestInfo  The RequestInfo of the calculation request
	 * @param calculations The Calculation Objects for which demand has to be
	 *                     generated or updated
	 */
	public List<Demand> generateDemand(RequestInfo requestInfo, List<Calculation> calculations,DemandRequiredParamater demandRequiredParamater) {

		String businessService=demandRequiredParamater.getBusinessService();
		
		// List that will contain Calculation for new demands
		List<Calculation> createCalculations = new LinkedList<>();

		// List that will contain Calculation for old demands
		List<Calculation> updateCalculations = new LinkedList<>();
		String tenantId = calculations.get(0).getTenantId();
		Set<String> applicationNumbers = calculations.stream().map(calculation -> calculation.getApplicationNumber())
				.collect(Collectors.toSet());
		List<Demand> demands = searchDemand(tenantId, applicationNumbers, requestInfo, businessService);
		if (!CollectionUtils.isEmpty(calculations)) {

			// Collect required parameters for demand search

			Set<String> applicationNumbersFromDemands = new HashSet<>();
			if (!CollectionUtils.isEmpty(demands))
				applicationNumbersFromDemands = demands.stream().map(Demand::getConsumerCode)
						.collect(Collectors.toSet());

			// If demand already exists add it updateCalculations else createCalculations
			for (Calculation calculation : calculations) {
				if (!applicationNumbersFromDemands.contains(calculation.getApplicationNumber()))
					createCalculations.add(calculation);
				else
					updateCalculations.add(calculation);
			}
		}

		if (!CollectionUtils.isEmpty(createCalculations))
			demands=createDemand(requestInfo, createCalculations,demandRequiredParamater);

		if (!CollectionUtils.isEmpty(updateCalculations))
			demands=updateDemand(requestInfo, updateCalculations, demandRequiredParamater);
		
		if(demands!=null&&!demands.isEmpty()) {
			System.out.println(demands.size());
			createBillV2(requestInfo,calculations,demandRequiredParamater,demands);
		}
		
		return demands;
	}

	/**
	 * Generates bill
	 * 
	 * @param requestInfo  The RequestInfo of the calculation request
	 * @param billCriteria The criteria for bill generation
	 * @return The generate bill response along with ids of slab used for
	 *         calculation
	 */
	public BillAndCalculations getBill(RequestInfo requestInfo, GenerateBillCriteria billCriteria,
			String serviceFromPath) {
		BillResponse billResponse = generateBill(requestInfo, billCriteria, serviceFromPath);
		BillingSlabIds billingSlabIds = getBillingSlabIds(billCriteria);
		BillAndCalculations getBillResponse = new BillAndCalculations();
		getBillResponse.setBillingSlabIds(billingSlabIds);
		getBillResponse.setBillResponse(billResponse);
		return getBillResponse;
	}

	/**
	 * Gets the billingSlabs from the db
	 * 
	 * @param billCriteria The criteria on which bill has to be generated
	 * @return The billingSlabIds used for calculation
	 */
	private BillingSlabIds getBillingSlabIds(GenerateBillCriteria billCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		CalculationSearchCriteria criteria = new CalculationSearchCriteria();
		criteria.setTenantId(billCriteria.getTenantId());
		criteria.setAplicationNumber(billCriteria.getConsumerCode());

		String query = calculationQueryBuilder.getSearchQuery(criteria, preparedStmtList);
		return calculationRepository.getDataFromDB(query, preparedStmtList);
	}

	/**
	 * Creates demand for the given list of calculations
	 * 
	 * @param requestInfo  The RequestInfo of the calculation request
	 * @param calculations List of calculation object
	 * @return Demands that are created
	 */
	private List<Demand> createDemand(RequestInfo requestInfo, List<Calculation> calculations,DemandRequiredParamater demandRequiredParamater) {
		
		Long taxPeriodFrom = demandRequiredParamater.getTaxPeriodFrom();// System.currentTimeMillis();
		Long taxPeriodTo = demandRequiredParamater.getTaxPeriodTo();// System.currentTimeMillis();
		String tenantId = demandRequiredParamater.getTenantId();
		String consumerCode = demandRequiredParamater.getConsumerCode();
		String businessService=demandRequiredParamater.getBusinessService();
		String consumerType=demandRequiredParamater.getConsumerType();
		BigDecimal minAmount=demandRequiredParamater.getTaxAmount();
		
		List<Demand> demands = new LinkedList<>();
		for (Calculation calculation : calculations) {

	        List<DemandDetail> demandDetails = new LinkedList<>();
	
		    calculation.getTaxHeadEstimates().forEach(taxHeadEstimate -> {
		    demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.
		    getEstimateAmount())
		    .taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).collectionAmount(
		    BigDecimal.ZERO) .tenantId(tenantId).build()); });
		 

			addRoundOffTaxHead(calculation.getTenantId(), demandDetails);
			List<String> combinedBillingSlabs = new LinkedList<>();
			if (calculation.getTradeTypeBillingIds() != null
					&& !CollectionUtils.isEmpty(calculation.getTradeTypeBillingIds().getBillingSlabIds()))
				combinedBillingSlabs.addAll(calculation.getTradeTypeBillingIds().getBillingSlabIds());
			if (calculation.getAccessoryBillingIds() != null
					&& !CollectionUtils.isEmpty(calculation.getAccessoryBillingIds().getBillingSlabIds()))
				combinedBillingSlabs.addAll(calculation.getAccessoryBillingIds().getBillingSlabIds());
			Demand singleDemand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails)//.payer(owner)
					.minimumAmountPayable(minAmount).tenantId(tenantId)
					.taxPeriodFrom(taxPeriodFrom).taxPeriodTo(taxPeriodTo)
					.businessService(tenantId)
					.additionalDetails(Collections.singletonMap(BILLINGSLAB_KEY, combinedBillingSlabs)).build();
			
			switch (businessService) {
	            case businessService_TL:{
	            	singleDemand.setConsumerType(consumerType);
				 break;
	            }
				case businessService_BPA: {
					singleDemand.setConsumerType("bpaStakeHolderReg");
					singleDemand.setBusinessService(config.getBusinessServiceBPA());
				break;
				}
			}
			demands.add(singleDemand);
		}
		return demandRepository.saveDemand(requestInfo, demands);
	}

	/**
	 * Updates demand for the given list of calculations
	 * 
	 * @param requestInfo  The RequestInfo of the calculation request
	 * @param calculations List of calculation object
	 * @return Demands that are updated
	 */
	private List<Demand> updateDemand(RequestInfo requestInfo, List<Calculation> calculations,DemandRequiredParamater demandRequiredParamater) {
		String consumerCode = demandRequiredParamater.getConsumerCode();
		String businessService=demandRequiredParamater.getBusinessService();
		
		List<Demand> demands = new LinkedList<>();
		for (Calculation calculation : calculations) {

			List<Demand> searchResult = searchDemand(calculation.getTenantId(),
					Collections.singleton(consumerCode), requestInfo,
					businessService);

			if (CollectionUtils.isEmpty(searchResult))
				throw new CustomException("INVALID UPDATE", "No demand exists for applicationNumber: "
						+ consumerCode);

			Demand demand = searchResult.get(0);
			List<DemandDetail> demandDetails = demand.getDemandDetails();
			List<DemandDetail> updatedDemandDetails = getUpdatedDemandDetails(calculation, demandDetails);
			demand.setDemandDetails(updatedDemandDetails);
			demands.add(demand);
		}
		return demandRepository.updateDemand(requestInfo, demands);
	}

	/**
	 * Searches demand for the given consumerCode and tenantIDd
	 * 
	 * @param tenantId      The tenantId of the tradeLicense
	 * @param consumerCodes The set of consumerCode of the demands
	 * @param requestInfo   The RequestInfo of the incoming request
	 * @return Lis to demands for the given consumerCode
	 */
	private List<Demand> searchDemand(String tenantId, Set<String> consumerCodes, RequestInfo requestInfo,
			String businessService) {
		String uri = utils.getDemandSearchURL();
		uri = uri.replace("{1}", tenantId);
		uri = uri.replace("{2}", businessService);
		uri = uri.replace("{3}", StringUtils.join(consumerCodes, ','));

		Object result = serviceRequestRepository.fetchResult(new StringBuilder(uri),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());

		DemandResponse response;
		try {
			response = mapper.convertValue(result, DemandResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Failed to parse response from Demand Search");
		}

		if (CollectionUtils.isEmpty(response.getDemands()))
			return null;

		else
			return response.getDemands();

	}

	
	
	
	private void createBillV2(RequestInfo requestInfo,List<Calculation> calculations,DemandRequiredParamater demandRequiredParamater,List<Demand> demands) {
		Demand demand=demands.get(0);
		String demandId=demand.getId();
	
		String tenantId = demandRequiredParamater.getTenantId();
		String consumerCode = demandRequiredParamater.getConsumerCode();
		String businessService=demandRequiredParamater.getBusinessService();
				
		Long taxPeriodFrom = demandRequiredParamater.getTaxPeriodFrom();
		Long taxPeriodTo = demandRequiredParamater.getTaxPeriodTo();
		BigDecimal amount = demandRequiredParamater.getTaxAmount();
		Map<String,Object> BillAccountDetailV2=new HashMap<>();
		BillAccountDetailV2.put("tenantId", tenantId);
		BillAccountDetailV2.put("amount", amount);
		
		Map<String,Object> billDetailV2=new HashMap<>();
		billDetailV2.put("tenantId", tenantId);
		billDetailV2.put("demandId", demandId);
		billDetailV2.put("expiryDate", taxPeriodTo);
		billDetailV2.put("amount", amount);
		billDetailV2.put("amountPaid", amount);
		billDetailV2.put("fromPeriod", taxPeriodFrom);
		billDetailV2.put("toPeriod", taxPeriodTo);
		billDetailV2.put("billAccountDetails", Arrays.asList(BillAccountDetailV2));
		
		Map<String,Object> billV2=new HashMap<>();
		billV2.put("mobileNumber","9812345678");
		billV2.put("payerName","Access InfoTech");
		billV2.put("payerAddress","Mohali");
		billV2.put("payerEmail","access@yopmail.com");
		billV2.put("status",BillStatus.PARTIALLY_PAID);
		billV2.put("totalAmount",amount);
		billV2.put("businessService",businessService);
		billV2.put("billDate",new Date().getTime());
		billV2.put("consumerCode",consumerCode);
		billV2.put("billDetails",Arrays.asList(billDetailV2));
		billV2.put("tenantId",tenantId);
//		billV2.put("auditDetails","log");
//		User u= requestInfo.getUserInfo();
//		List<Role> roleList=new ArrayList<>();
//		u.getRoles().forEach(role ->{
//			Role ro=new Role();
//			ro.setCode(role.getCode());	
//			ro.setId(role.getId());	
//			ro.setName(role.getName());	
//			ro.setTenantId(role.getTenantId());	
//			roleList.add(ro);
//		});
//		u.setRoles(roleList);
//		requestInfo.setUserInfo(u);
		
		Map<String,Object> billV2CreationRequest=new HashMap<>();
		billV2CreationRequest.put("RequestInfo", requestInfo);
		billV2CreationRequest.put("Bills",Arrays.asList(billV2));
		
	    StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getBillCreateEndpoint());
    
        HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		String billId=null;
		try {
		HttpEntity<Map<String,Object>> entity = new HttpEntity<>(billV2CreationRequest, headers);
//		System.out.println("entity.getBody():---"+entity.getBody());
		HashMap billDeatails = restTemplate.exchange(url.toString(), HttpMethod.POST, entity, HashMap.class).getBody();
		List<HashMap> bill=(List<HashMap>) billDeatails.get("Bill");
	    billId=bill.get(0).get("id").toString();
		log.info("Bill Created : "+billId);
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception: "+e.getMessage());
		}
		
		List<String> billIds=Arrays.asList(billId);
		calculations=calculations.stream().map(cal->{
			FeeAndBillingSlabIds feeAndBillingSlabIds=new FeeAndBillingSlabIds();
			feeAndBillingSlabIds.setBillingSlabIds(billIds);
			cal.setTradeTypeBillingIds(feeAndBillingSlabIds);
			return cal;
			}).collect(Collectors.toList());
		
	}
	
   public enum BillStatus {
		
		ACTIVE("ACTIVE"),

		CANCELLED("CANCELLED"),

		PAID("PAID"),
		
		PARTIALLY_PAID ("PARTIALLY_PAID"),
		
		PAYMENT_CANCELLED ("PAYMENT_CANCELLED"),

		EXPIRED("EXPIRED");

		private String value;

		BillStatus(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static BillStatus fromValue(String text) {
			for (BillStatus b : BillStatus.values()) {
				if (String.valueOf(b.value).equalsIgnoreCase(text)) {
					return b;
				}
			}
			return null;
		}
	}
	/**
	 * Generates bill by calling BillingService
	 * 
	 * @param requestInfo  The RequestInfo of the getBill request
	 * @param billCriteria The criteria for bill generation
	 * @return The response of the bill generate
	 */
	private BillResponse generateBill(RequestInfo requestInfo, GenerateBillCriteria billCriteria,
			String businessServiceFromPath) {

		String consumerCode = billCriteria.getConsumerCode();
		String tenantId = billCriteria.getTenantId();

		List<Demand> demands = searchDemand(tenantId, Collections.singleton(consumerCode), requestInfo,
				billCriteria.getBusinessService());

		if (!StringUtils.equals(businessServiceFromPath, billCriteria.getBusinessService()))
			throw new CustomException("BUSINESSSERVICE_MISMATCH",
					"Business Service in Path variable and bill criteria are different");

		if (CollectionUtils.isEmpty(demands))
			throw new CustomException("INVALID CONSUMERCODE",
					"Bill cannot be generated.No demand exists for the given consumerCode");

		String uri = utils.getBillGenerateURI();
		uri = uri.replace("{1}", billCriteria.getTenantId());
		uri = uri.replace("{2}", billCriteria.getConsumerCode());
		uri = uri.replace("{3}", billCriteria.getBusinessService());

		Object result = serviceRequestRepository.fetchResult(new StringBuilder(uri),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());
		BillResponse response;
		try {
			response = mapper.convertValue(result, BillResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Unable to parse response of generate bill");
		}
		return response;
	}

	/**
	 * Returns the list of new DemandDetail to be added for updating the demand
	 * 
	 * @param calculation   The calculation object for the update tequest
	 * @param demandDetails The list of demandDetails from the existing demand
	 * @return The list of new DemandDetails
	 */
	private List<DemandDetail> getUpdatedDemandDetails(Calculation calculation, List<DemandDetail> demandDetails) {

		List<DemandDetail> newDemandDetails = new ArrayList<>();
		Map<String, List<DemandDetail>> taxHeadToDemandDetail = new HashMap<>();

		demandDetails.forEach(demandDetail -> {
			if (!taxHeadToDemandDetail.containsKey(demandDetail.getTaxHeadMasterCode())) {
				List<DemandDetail> demandDetailList = new LinkedList<>();
				demandDetailList.add(demandDetail);
				taxHeadToDemandDetail.put(demandDetail.getTaxHeadMasterCode(), demandDetailList);
			} else
				taxHeadToDemandDetail.get(demandDetail.getTaxHeadMasterCode()).add(demandDetail);
		});

		BigDecimal diffInTaxAmount;
		List<DemandDetail> demandDetailList;
		BigDecimal total;

		for (TaxHeadEstimate taxHeadEstimate : calculation.getTaxHeadEstimates()) {
			if (!taxHeadToDemandDetail.containsKey(taxHeadEstimate.getTaxHeadCode()))
				newDemandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
						.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).tenantId(calculation.getTenantId())
						.collectionAmount(BigDecimal.ZERO).build());
			else {
				demandDetailList = taxHeadToDemandDetail.get(taxHeadEstimate.getTaxHeadCode());
				total = demandDetailList.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO,
						BigDecimal::add);
				diffInTaxAmount = taxHeadEstimate.getEstimateAmount().subtract(total);
				if (diffInTaxAmount.compareTo(BigDecimal.ZERO) != 0) {
					newDemandDetails.add(DemandDetail.builder().taxAmount(diffInTaxAmount)
							.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).tenantId(calculation.getTenantId())
							.collectionAmount(BigDecimal.ZERO).build());
				}
			}
		}
		List<DemandDetail> combinedBillDetials = new LinkedList<>(demandDetails);
		combinedBillDetials.addAll(newDemandDetails);
		addRoundOffTaxHead(calculation.getTenantId(), combinedBillDetials);
		return combinedBillDetials;
	}

	/**
	 * Adds roundOff taxHead if decimal values exists
	 * 
	 * @param tenantId      The tenantId of the demand
	 * @param demandDetails The list of demandDetail
	 */
	private void addRoundOffTaxHead(String tenantId, List<DemandDetail> demandDetails) {
		BigDecimal totalTax = BigDecimal.ZERO;

		DemandDetail prevRoundOffDemandDetail = null;

		/*
		 * Sum all taxHeads except RoundOff as new roundOff will be calculated
		 */
		for (DemandDetail demandDetail : demandDetails) {
			if (!demandDetail.getTaxHeadMasterCode().equalsIgnoreCase(MDMS_ROUNDOFF_TAXHEAD))
				totalTax = totalTax.add(demandDetail.getTaxAmount());
			else
				prevRoundOffDemandDetail = demandDetail;
		}

		BigDecimal decimalValue = totalTax.remainder(BigDecimal.ONE);
		BigDecimal midVal = new BigDecimal(0.5);
		BigDecimal roundOff = BigDecimal.ZERO;

		/*
		 * If the decimal amount is greater than 0.5 we subtract it from 1 and put it as
		 * roundOff taxHead so as to nullify the decimal eg: If the tax is 12.64 we will
		 * add extra tax roundOff taxHead of 0.36 so that the total becomes 13
		 */
		if (decimalValue.compareTo(midVal) > 0)
			roundOff = BigDecimal.ONE.subtract(decimalValue);

		/*
		 * If the decimal amount is less than 0.5 we put negative of it as roundOff
		 * taxHead so as to nullify the decimal eg: If the tax is 12.36 we will add
		 * extra tax roundOff taxHead of -0.36 so that the total becomes 12
		 */
		if (decimalValue.compareTo(midVal) < 0)
			roundOff = decimalValue.negate();

		/*
		 * If roundOff already exists in previous demand create a new roundOff taxHead
		 * with roundOff amount equal to difference between them so that it will be
		 * balanced when bill is generated. eg: If the previous roundOff amount was of
		 * -0.36 and the new roundOff excluding the previous roundOff is 0.2 then the
		 * new roundOff will be created with 0.2 so that the net roundOff will be 0.2
		 * -(-0.36)
		 */
		if (prevRoundOffDemandDetail != null) {
			roundOff = roundOff.subtract(prevRoundOffDemandDetail.getTaxAmount());
		}

		if (roundOff.compareTo(BigDecimal.ZERO) != 0) {
			DemandDetail roundOffDemandDetail = DemandDetail.builder().taxAmount(roundOff)
					.taxHeadMasterCode(MDMS_ROUNDOFF_TAXHEAD).tenantId(tenantId).collectionAmount(BigDecimal.ZERO)
					.build();

			demandDetails.add(roundOffDemandDetail);
		}
	}
	

}
