package org.egov.tlcalculator.service;

import static org.egov.tlcalculator.utils.TLCalculatorConstants.businessService_TL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tlcalculator.config.TLCalculatorConfigs;
import org.egov.tlcalculator.kafka.broker.TLCalculatorProducer;
import org.egov.tlcalculator.repository.BillingslabRepository;
import org.egov.tlcalculator.repository.builder.BillingslabQueryBuilder;
import org.egov.tlcalculator.utils.CalculationUtils;
import org.egov.tlcalculator.utils.LandUtil;
import org.egov.tlcalculator.utils.TLCalculatorConstants;
import org.egov.tlcalculator.web.models.Accessory;
import org.egov.tlcalculator.web.models.BillingSlab;
import org.egov.tlcalculator.web.models.BillingSlabSearchCriteria;
import org.egov.tlcalculator.web.models.Calculation;
import org.egov.tlcalculator.web.models.CalculationReq;
import org.egov.tlcalculator.web.models.CalculationRes;
import org.egov.tlcalculator.web.models.CalculatorRequest;
import org.egov.tlcalculator.web.models.CalulationCriteria;
import org.egov.tlcalculator.web.models.FeeAndBillingSlabIds;
import org.egov.tlcalculator.web.models.bankguarantee.BankGuaranteeCalculationCriteria;
import org.egov.tlcalculator.web.models.bankguarantee.BankGuaranteeCalculationResponse;
import org.egov.tlcalculator.web.models.demand.Category;
import org.egov.tlcalculator.web.models.demand.DemandRequiredParamater;
import org.egov.tlcalculator.web.models.demand.TaxHeadEstimate;
import org.egov.tlcalculator.web.models.enums.CalculationType;
import org.egov.tlcalculator.web.models.tradelicense.EstimatesAndSlabs;
import org.egov.tlcalculator.web.models.tradelicense.TradeLicense;
import org.egov.tlcalculator.web.models.tradelicense.TradeUnit;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import io.swagger.annotations.License;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculationService {

	@Autowired
	private BillingslabRepository repository;

	@Autowired
	private BillingslabQueryBuilder queryBuilder;

	@Autowired
	private TLCalculatorConfigs config;

	// @Autowired
//	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private CalculationUtils utils;

	@Autowired
	private DemandService demandService;

	@Autowired
	private TLCalculatorProducer producer;

	@Autowired
	private MDMSService mdmsService;

	@Autowired
	private TLRenewalCalculation tlRenewal;

	@Autowired
	LandUtil util;

	@Autowired
	CalculatorImpl calculatorImpl;

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	FeesCalculation feesCalculation;

	@Autowired
	AccessDemandService accessDemandService;
	
	/**
	 * Calculates tax estimates and creates demand
	 * 
	 * @param calculationReq The calculationCriteria request
	 * @return List of calculations for all applicationNumbers or tradeLicenses in
	 *         calculationReq
	 */
	public List<Calculation> calculate(CalculationReq calculationReq, Boolean isEstimate) {
		String tenantId = calculationReq.getCalulationCriteria().get(0).getTenantId();
		Object mdmsData = mdmsService.mDMSCall(calculationReq.getRequestInfo(), tenantId);
		List<Calculation> calculations = null;
		TradeLicense license = utils.getTradeLicense(calculationReq.getRequestInfo(),
				calculationReq.getCalculatorRequest().getApplicationNumber(),
				calculationReq.getRequestInfo().getUserInfo().getTenantId());
		List<CalulationCriteria> calulationCriteria = new ArrayList<>();
		CalulationCriteria objectCalulationCriteria = new CalulationCriteria(license,
				calculationReq.getCalculatorRequest().getApplicationNumber(),
				calculationReq.getRequestInfo().getUserInfo().getTenantId());
		calulationCriteria.add(objectCalulationCriteria);
		calculationReq.setCalulationCriteria(calulationCriteria);
		if (calculationReq.getCalulationCriteria().get(0).getTradelicense().getBusinessService()
				.equalsIgnoreCase(businessService_TL)) {
			calculations = calculator(calculationReq);

		} else {
			calculations = getCalculation(calculationReq.getRequestInfo(), calculationReq.getCalulationCriteria(),
					mdmsData);
		}
		CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();

		if (!isEstimate) {
			demandService.generateDemand(calculationReq.getRequestInfo(), calculations, mdmsData, businessService_TL);
			producer.push(config.getSaveTopic(), calculationRes);
		}
		return calculations;
	}

	public List<Calculation> calculator(CalculationReq calculationReq) {
		String tenantId = calculationReq.getRequestInfo().getUserInfo().getTenantId();
		Object mdmsData = mdmsService.mDMSCall(calculationReq.getRequestInfo(), tenantId);
//		Object mdmsData = util.mDMSCallPurposeCode(calculationReq.getRequestInfo(), tenantId,
//				calculationReq.getCalculatorRequest().getPurposeCode());
		List<FeesTypeCalculationDto> results = feesCalculation.payment(calculationReq.getRequestInfo(),
				calculationReq.getCalculatorRequest().getApplicationNumber());
//     
//       List<Calculation> calculations = getCalculation(calculationReq.getRequestInfo(),
//               calculationReq.getCalulationCriteria(),mdmsData);
//     
		List<Calculation> calculations = new ArrayList<>();
		Calculation calculation = null;

		List<String> bilingSlabId = new ArrayList<String>();
		BigDecimal scrutinyFee = new BigDecimal(0);
		BigDecimal licenseFeeCharges = new BigDecimal(0);
		BigDecimal conversionCharges = new BigDecimal(0);
		BigDecimal stateInfrastructure = new BigDecimal(0);
		BigDecimal externalDevelopment = new BigDecimal(0);
		
		for (FeesTypeCalculationDto result : results) {
			scrutinyFee = scrutinyFee.add(result.getScrutinyFeeChargesCal());
			licenseFeeCharges = licenseFeeCharges.add(result.getLicenseFeeChargesCal());
			externalDevelopment = externalDevelopment.add(result.getExternalDevelopmentChargesCal());
			stateInfrastructure = stateInfrastructure.add(result.getStateInfrastructureDevelopmentChargesCal());
			conversionCharges = conversionCharges.add(result.getConversionChargesCal());

		}

		for (CalulationCriteria criteria : calculationReq.getCalulationCriteria()) {
			TradeLicense license;
			if (criteria.getTradelicense() == null && criteria.getApplicationNumber() != null) {
				license = utils.getTradeLicense(calculationReq.getRequestInfo(), criteria.getApplicationNumber(),
						criteria.getTenantId());
				criteria.setTradelicense(license);
			}
			calculation = new Calculation();
			calculation.setApplicationNumber(calculationReq.getCalculatorRequest().getApplicationNumber());
			calculation.setTenantId(tenantId);
			calculation.setTradeTypeBillingIds(
					new FeeAndBillingSlabIds("", scrutinyFee.add(licenseFeeCharges), scrutinyFee, licenseFeeCharges,
							conversionCharges, externalDevelopment, stateInfrastructure, bilingSlabId));
			calculation.setTradeLicense(criteria.getTradelicense());

			EstimatesAndSlabs estimatesAndSlabs = getTaxHeadEstimates(criteria, calculationReq.getRequestInfo(),
					mdmsData);
			List<TaxHeadEstimate> taxHeadEstimates = estimatesAndSlabs.getEstimates();
		//	taxHeadEstimates.get(0).setEstimateAmount(scrutinyFee.add(licenseFeeCharges.multiply(new BigDecimal(0.25))));
			taxHeadEstimates.get(0).setEstimateAmount(results.get(0).getTotalFee());
			calculation.setTaxHeadEstimates(taxHeadEstimates);
			// taxHeadEstimates.get(0).setEstimateAmount(result.getTotalFee());
			calculations.add(calculation);
		}

		return calculations;
	}

	/***
	 * Calculates tax estimates
	 * 
	 * @param requestInfo The requestInfo of the calculation request
	 * @param criterias   list of CalculationCriteria containing the tradeLicense or
	 *                    applicationNumber
	 * @return List of calculations for all applicationNumbers or tradeLicenses in
	 *         criterias
	 */
	public List<Calculation> getCalculation(RequestInfo requestInfo, List<CalulationCriteria> criterias,
			Object mdmsData) {
		List<Calculation> calculations = new LinkedList<>();
		for (CalulationCriteria criteria : criterias) {
			TradeLicense license;
			if (criteria.getTradelicense() == null && criteria.getApplicationNumber() != null) {
				license = utils.getTradeLicense(requestInfo, criteria.getApplicationNumber(), criteria.getTenantId());
				criteria.setTradelicense(license);
			}
			EstimatesAndSlabs estimatesAndSlabs = getTaxHeadEstimates(criteria, requestInfo, mdmsData);
			List<TaxHeadEstimate> taxHeadEstimates = estimatesAndSlabs.getEstimates();
			FeeAndBillingSlabIds tradeTypeFeeAndBillingSlabIds = estimatesAndSlabs.getTradeTypeFeeAndBillingSlabIds();
			FeeAndBillingSlabIds accessoryFeeAndBillingSlabIds = null;
			if (estimatesAndSlabs.getAccessoryFeeAndBillingSlabIds() != null)
				accessoryFeeAndBillingSlabIds = estimatesAndSlabs.getAccessoryFeeAndBillingSlabIds();
			Calculation calculation = new Calculation();
			calculation.setTradeLicense(criteria.getTradelicense());
			calculation.setTenantId(criteria.getTenantId());
			calculation.setTaxHeadEstimates(taxHeadEstimates);
			calculation.setTradeTypeBillingIds(tradeTypeFeeAndBillingSlabIds);
			if (accessoryFeeAndBillingSlabIds != null)
				calculation.setAccessoryBillingIds(accessoryFeeAndBillingSlabIds);

			calculations.add(calculation);

		}
		return calculations;
	}

	public BankGuaranteeCalculationResponse getEstimateForBankGuarantee(
			BankGuaranteeCalculationCriteria bankGuaranteeCalculationCriteria) {
		try {
			log.info("inside method getEstimateForBankGuarantee with payload:"
					+ objectMapper.writeValueAsString(bankGuaranteeCalculationCriteria));
		} catch (JsonProcessingException e) {
		}
		if ((StringUtils.isEmpty(bankGuaranteeCalculationCriteria.getPotentialZone())
				|| StringUtils.isEmpty(bankGuaranteeCalculationCriteria.getPurposeCode())
				|| StringUtils.isEmpty(bankGuaranteeCalculationCriteria.getTotalLandSize())
				|| StringUtils.isEmpty(bankGuaranteeCalculationCriteria.getApplicationNumber()))
				&& StringUtils.isEmpty(bankGuaranteeCalculationCriteria.getLoiNumber())) {
			throw new CustomException("either send loiNumber or send all parameters mandatorily",
					"either send loiNumber or send all parameters mandatorily");
		}
		if (!(StringUtils.isEmpty(bankGuaranteeCalculationCriteria.getPotentialZone())
				&& StringUtils.isEmpty(bankGuaranteeCalculationCriteria.getPurposeCode())
				&& StringUtils.isEmpty(bankGuaranteeCalculationCriteria.getTotalLandSize()))) {
			log.info("calculating with the parameters");
			CalulationCriteria calulationCriteria = CalulationCriteria.builder()
					.tenantId(bankGuaranteeCalculationCriteria.getTenantId()).build();
			CalculatorRequest calculatorRequest = CalculatorRequest.builder()
					.applicationNumber(bankGuaranteeCalculationCriteria.getApplicationNumber())
					.totalLandSize(bankGuaranteeCalculationCriteria.getTotalLandSize().toString())
					.potenialZone(bankGuaranteeCalculationCriteria.getPotentialZone())
					.purposeCode(bankGuaranteeCalculationCriteria.getPurposeCode()).build();
			List<CalulationCriteria> calculationCriteriaList = new ArrayList<>();
			calculationCriteriaList.add(calulationCriteria);
			CalculationReq calculationReq = CalculationReq.builder().calculatorRequest(calculatorRequest)
					.requestInfo(bankGuaranteeCalculationCriteria.getRequestInfo())
					.calulationCriteria(calculationCriteriaList).build();
			List<Calculation> calculations = calculate(calculationReq, true);
			if (CollectionUtils.isEmpty(calculations)) {
				throw new CustomException("no calculations found for given criteria",
						"no calculations found for given criteria");
			}

			BigDecimal edcCharges = calculations.get(0).getTradeTypeBillingIds().getExternalDevelopmentCharges();
			BigDecimal constant = new BigDecimal(0.25);
			BigDecimal bankGuaranteeForEdc = constant.multiply(edcCharges);
			Object mdmsData = fetchMasterDataForBankGuaranteeAmountCalculations(bankGuaranteeCalculationCriteria);

			// IDW calculation--
			String purposeMdmsPath = TLCalculatorConstants.MDMS_PURPOSE_PATH;
			purposeMdmsPath = purposeMdmsPath.replace("{$purposeCode}",
					bankGuaranteeCalculationCriteria.getPurposeCode());

			List purposeData = JsonPath.read(mdmsData, purposeMdmsPath);
			BigDecimal iDWPerAcreInLakhsForPlottedComponent = BigDecimal.ZERO;
			BigDecimal iDWPerAcreInLakhsForCommercialComponent = BigDecimal.ZERO;
			if (purposeData.size() == 0) {
				log.error("purpose wise IDW not found for this purpose in master data");
				BankGuaranteeCalculationResponse bankGuaranteeCalculationResponse = BankGuaranteeCalculationResponse
						.builder().bankGuaranteeForEDC(bankGuaranteeForEdc).bankGuaranteeForIDW(BigDecimal.ZERO)
						.build();
				// TODO:
			}
			// Assumption: only one purpose detail found for purpose code in master data-
			HashMap<String, Object> map = (HashMap<String, Object>) purposeData.get(0);
			try {
				iDWPerAcreInLakhsForPlottedComponent = new BigDecimal(
						String.valueOf(map.get("IDWPerAcreInLakhsForPlottedComponent")));
				iDWPerAcreInLakhsForCommercialComponent = new BigDecimal(
						String.valueOf(map.get("IDWPerAcreInLakhsForCommercialComponent")));
			} catch (Exception ex) {
				log.error("Exception while parsing IDW per acre rates from master data");
			}
			BigDecimal iDWAmountForCommercialComponent = iDWPerAcreInLakhsForCommercialComponent
					.multiply(TLCalculatorConstants.IDW_COMMERCIAL_COMPONENT_AREA_FACTOR)
					.multiply(bankGuaranteeCalculationCriteria.getTotalLandSize())
					.multiply(TLCalculatorConstants.LAKH_TO_RUPEES_CONVERSION_FACTOR);
			BigDecimal iDWAmountForPlottedComponent = iDWPerAcreInLakhsForPlottedComponent
					.multiply(TLCalculatorConstants.IDW_PLOTTED_COMPONENT_AREA_FACTOR)
					.multiply(bankGuaranteeCalculationCriteria.getTotalLandSize())
					.multiply(TLCalculatorConstants.LAKH_TO_RUPEES_CONVERSION_FACTOR);
			BigDecimal totalIDWAmount = iDWAmountForCommercialComponent.add(iDWAmountForPlottedComponent);
			BankGuaranteeCalculationResponse bankGuaranteeCalculationResponse = BankGuaranteeCalculationResponse
					.builder().bankGuaranteeForEDC(bankGuaranteeForEdc).bankGuaranteeForIDW(totalIDWAmount).build();
			return bankGuaranteeCalculationResponse;

		} else {
			log.info("calculating with the loiNumber");
			// TODO: implement loiNumber based calculation once loiNumber becomes mandatory
			return null;
		}
	}

	private void calculateEdcBankGuarantee(BigDecimal totalLandSizeInSqMtrs, String potentialZone, String purposeCode) {

	}

	private Object fetchMasterDataForBankGuaranteeAmountCalculations(
			BankGuaranteeCalculationCriteria bankGuaranteeCalculationCriteria) {
		Object mdmsData = mdmsService.mDMSCallForBankGuarantee(bankGuaranteeCalculationCriteria.getRequestInfo(),
				bankGuaranteeCalculationCriteria.getTenantId());
		return mdmsData;
	}

	/**
	 * Creates TacHeadEstimates
	 * 
	 * @param calulationCriteria CalculationCriteria containing the tradeLicense or
	 *                           applicationNumber
	 * @param requestInfo        The requestInfo of the calculation request
	 * @return TaxHeadEstimates and the billingSlabs used to calculate it
	 */
	private EstimatesAndSlabs getTaxHeadEstimates(CalulationCriteria calulationCriteria, RequestInfo requestInfo,
			Object mdmsData) {
		List<TaxHeadEstimate> estimates = new LinkedList<>();
		EstimatesAndSlabs estimatesAndSlabs = getBaseTax(calulationCriteria, requestInfo, mdmsData);

		estimates.addAll(estimatesAndSlabs.getEstimates());

//		if (calulationCriteria.getTradelicense().getTradeLicenseDetail().getAdhocPenalty() != null)
//			estimates.add(getAdhocPenalty(calulationCriteria));
//
//		if (calulationCriteria.getTradelicense().getTradeLicenseDetail().getAdhocExemption() != null)
//			estimates.add(getAdhocExemption(calulationCriteria));

		estimatesAndSlabs.setEstimates(estimates);

		return estimatesAndSlabs;
	}

	/**
	 * Calculates base tax and cretaes its taxHeadEstimate
	 * 
	 * @param calulationCriteria CalculationCriteria containing the tradeLicense or
	 *                           applicationNumber
	 * @param requestInfo        The requestInfo of the calculation request
	 * @return BaseTax taxHeadEstimate and billingSlabs used to calculate it
	 */
	private EstimatesAndSlabs getBaseTax(CalulationCriteria calulationCriteria, RequestInfo requestInfo,
			Object mdmsData) {
		TradeLicense license = calulationCriteria.getTradelicense();
		EstimatesAndSlabs estimatesAndSlabs = new EstimatesAndSlabs();
		BillingSlabSearchCriteria searchCriteria = new BillingSlabSearchCriteria();
		searchCriteria.setTenantId(license.getTenantId());
		// searchCriteria.setStructureType(license.getTradeLicenseDetail().getStructureType());
		searchCriteria.setLicenseType(license.getLicenseType().toString());

		Map calculationTypeMap = mdmsService.getCalculationType(requestInfo, license, mdmsData);
		String tradeUnitCalculationType = (String) calculationTypeMap
				.get(TLCalculatorConstants.MDMS_CALCULATIONTYPE_TRADETYPE);
		String accessoryCalculationType = (String) calculationTypeMap
				.get(TLCalculatorConstants.MDMS_CALCULATIONTYPE_ACCESSORY);

		FeeAndBillingSlabIds tradeTypeFeeAndBillingSlabIds = getTradeUnitFeeAndBillingSlabIds(license,
				CalculationType.fromValue(tradeUnitCalculationType));
		BigDecimal tradeUnitFee = tradeTypeFeeAndBillingSlabIds.getFee();

		estimatesAndSlabs.setTradeTypeFeeAndBillingSlabIds(tradeTypeFeeAndBillingSlabIds);
		BigDecimal accessoryFee = new BigDecimal(0);

//		if (!CollectionUtils.isEmpty(license.getTradeLicenseDetail().getAccessories())) {
//			FeeAndBillingSlabIds accessoryFeeAndBillingSlabIds = getAccessoryFeeAndBillingSlabIds(license,
//					CalculationType.fromValue(accessoryCalculationType));
//			accessoryFee = accessoryFeeAndBillingSlabIds.getFee();
//			estimatesAndSlabs.setAccessoryFeeAndBillingSlabIds(accessoryFeeAndBillingSlabIds);
//		}

		TaxHeadEstimate estimate = new TaxHeadEstimate();
		List<TaxHeadEstimate> estimateList = new ArrayList<>();
		BigDecimal totalTax = tradeUnitFee.add(accessoryFee);

		if (totalTax.compareTo(BigDecimal.ZERO) == -1)
			throw new CustomException("INVALID AMOUNT", "Tax amount is negative");

		estimate.setEstimateAmount(totalTax);
		estimate.setCategory(Category.TAX);
		if (license.getApplicationType() != null
				&& license.getApplicationType().toString().equals(TLCalculatorConstants.APPLICATION_TYPE_RENEWAL)) {
			estimate.setTaxHeadCode(config.getRenewTaxHead());
			estimateList.add(estimate);
			estimateList.addAll(tlRenewal.tlRenewalCalculation(requestInfo, calulationCriteria, mdmsData, totalTax));
		} else {
			estimate.setTaxHeadCode(config.getBaseTaxHead());
			estimateList.add(estimate);
		}

		estimatesAndSlabs.setEstimates(estimateList);

		return estimatesAndSlabs;
	}

	/**
	 * Creates taxHeadEstimates for AdhocPenalty
	 * 
	 * @param calulationCriteria CalculationCriteria containing the tradeLicense or
	 *                           applicationNumber
	 * @return AdhocPenalty taxHeadEstimates
	 */
	private TaxHeadEstimate getAdhocPenalty(CalulationCriteria calulationCriteria) {
		TradeLicense license = calulationCriteria.getTradelicense();
		TaxHeadEstimate estimate = new TaxHeadEstimate();
		// estimate.setEstimateAmount(license.getTradeLicenseDetail().getAdhocPenalty());
		estimate.setTaxHeadCode(config.getAdhocPenaltyTaxHead());
		estimate.setCategory(Category.PENALTY);
		return estimate;
	}

	/**
	 * Creates taxHeadEstimates for AdhocRebate
	 * 
	 * @param calulationCriteria CalculationCriteria containing the tradeLicense or
	 *                           applicationNumber
	 * @return AdhocRebate taxHeadEstimates
	 */
	private TaxHeadEstimate getAdhocExemption(CalulationCriteria calulationCriteria) {
		TradeLicense license = calulationCriteria.getTradelicense();
		TaxHeadEstimate estimate = new TaxHeadEstimate();
		// estimate.setEstimateAmount(license.getTradeLicenseDetail().getAdhocExemption());
		estimate.setTaxHeadCode(config.getAdhocExemptionTaxHead());
		estimate.setCategory(Category.EXEMPTION);
		return estimate;
	}

	/**
	 * @param license         TradeLicense for which fee has to be calculated
	 * @param calculationType Calculation logic to be used
	 * @return TradeUnit Fee and billingSlab used to calculate it
	 */
	private FeeAndBillingSlabIds getTradeUnitFeeAndBillingSlabIds(TradeLicense license,
			CalculationType calculationType) {

		List<BigDecimal> tradeUnitFees = new LinkedList<>();
		// List<TradeUnit> tradeUnits = license.getTradeLicenseDetail().getTradeUnits();
		List<String> billingSlabIds = new LinkedList<>();
		int i = 0;
		/*
		 * for (TradeUnit tradeUnit : tradeUnits) { if (tradeUnit.getActive()) {
		 * List<Object> preparedStmtList = new ArrayList<>(); BillingSlabSearchCriteria
		 * searchCriteria = new BillingSlabSearchCriteria();
		 * searchCriteria.setTenantId(license.getTenantId());
		 * searchCriteria.setStructureType(license.getTradeLicenseDetail().
		 * getStructureType());
		 * searchCriteria.setApplicationType(license.getApplicationType().toString());
		 * searchCriteria.setLicenseType(license.getLicenseType().toString());
		 * searchCriteria.setTradeType(tradeUnit.getTradeType()); if
		 * (tradeUnit.getUomValue() != null) {
		 * searchCriteria.setUomValue(Double.parseDouble(tradeUnit.getUomValue()));
		 * searchCriteria.setUom(tradeUnit.getUom()); } // Call the Search String query
		 * = queryBuilder.getSearchQuery(searchCriteria, preparedStmtList);
		 * log.info("query " + query); log.info("preparedStmtList " +
		 * preparedStmtList.toString()); List<BillingSlab> billingSlabs =
		 * repository.getDataFromDB(query, preparedStmtList);
		 * 
		 * if (billingSlabs.size() > 1) throw new CustomException("BILLINGSLAB ERROR",
		 * "Found multiple BillingSlabs for the given TradeType"); if
		 * (CollectionUtils.isEmpty(billingSlabs)) throw new
		 * CustomException("BILLINGSLAB ERROR",
		 * "No BillingSlab Found for the given tradeType"); System.out
		 * .println("TradeUnit: " + tradeUnit.getTradeType() + " rate: " +
		 * billingSlabs.get(0).getRate());
		 * 
		 * billingSlabIds.add(billingSlabs.get(0).getId() + "|" + i + "|" +
		 * tradeUnit.getId());
		 * 
		 * if (billingSlabs.get(0).getType().equals(BillingSlab.TypeEnum.FLAT))
		 * tradeUnitFees.add(billingSlabs.get(0).getRate()); // tradeUnitTotalFee =
		 * tradeUnitTotalFee.add(billingSlabs.get(0).getRate());
		 * 
		 * if (billingSlabs.get(0).getType().equals(BillingSlab.TypeEnum.RATE)) {
		 * BigDecimal uomVal = new BigDecimal(tradeUnit.getUomValue());
		 * tradeUnitFees.add(billingSlabs.get(0).getRate().multiply(uomVal)); //
		 * tradeUnitTotalFee = //
		 * tradeUnitTotalFee.add(billingSlabs.get(0).getRate().multiply(uomVal)); } i++;
		 * } }
		 */

		BigDecimal tradeUnitTotalFee = getTotalFee(tradeUnitFees, calculationType);

		FeeAndBillingSlabIds feeAndBillingSlabIds = new FeeAndBillingSlabIds();
		feeAndBillingSlabIds.setFee(tradeUnitTotalFee);
		feeAndBillingSlabIds.setBillingSlabIds(billingSlabIds);
		feeAndBillingSlabIds.setId(UUID.randomUUID().toString());

		return feeAndBillingSlabIds;
	}

	/**
	 * @param license         TradeLicense for which fee has to be calculated
	 * @param calculationType Calculation logic to be used
	 * @return Accessory Fee and billingSlab used to calculate it
	 */
	private FeeAndBillingSlabIds getAccessoryFeeAndBillingSlabIds(TradeLicense license,
			CalculationType calculationType) {

		List<BigDecimal> accessoryFees = new LinkedList<>();
		List<String> billingSlabIds = new LinkedList<>();

		/*
		 * List<Accessory> accessories =
		 * license.getTradeLicenseDetail().getAccessories(); int i = 0; for (Accessory
		 * accessory : accessories) { if (accessory.getActive()) { List<Object>
		 * preparedStmtList = new ArrayList<>(); BillingSlabSearchCriteria
		 * searchCriteria = new BillingSlabSearchCriteria();
		 * searchCriteria.setTenantId(license.getTenantId());
		 * searchCriteria.setAccessoryCategory(accessory.getAccessoryCategory());
		 * searchCriteria.setApplicationType(license.getApplicationType().toString());
		 * if (accessory.getUomValue() != null) {
		 * searchCriteria.setUomValue(Double.parseDouble(accessory.getUomValue()));
		 * searchCriteria.setUom(accessory.getUom()); } // Call the Search String query
		 * = queryBuilder.getSearchQuery(searchCriteria, preparedStmtList);
		 * List<BillingSlab> billingSlabs = repository.getDataFromDB(query,
		 * preparedStmtList);
		 * 
		 * if (billingSlabs.size() > 1) throw new CustomException("BILLINGSLAB ERROR",
		 * "Found multiple BillingSlabs for the given accessories "); if
		 * (CollectionUtils.isEmpty(billingSlabs)) throw new
		 * CustomException("BILLINGSLAB ERROR",
		 * "No BillingSlab Found for the given accessory"); System.out.println(
		 * "Accessory: " + accessory.getAccessoryCategory() + " rate: " +
		 * billingSlabs.get(0).getRate());
		 * billingSlabIds.add(billingSlabs.get(0).getId() + "|" + i + "|" +
		 * accessory.getId()); if
		 * (billingSlabs.get(0).getType().equals(BillingSlab.TypeEnum.FLAT)) {
		 * BigDecimal count = accessory.getCount() == null ? BigDecimal.ONE : new
		 * BigDecimal(accessory.getCount());
		 * accessoryFees.add(billingSlabs.get(0).getRate().multiply(count)); } //
		 * accessoryTotalFee = accessoryTotalFee.add(billingSlabs.get(0).getRate());
		 * 
		 * if (billingSlabs.get(0).getType().equals(BillingSlab.TypeEnum.RATE)) {
		 * BigDecimal uomVal = new BigDecimal(accessory.getUomValue());
		 * accessoryFees.add(billingSlabs.get(0).getRate().multiply(uomVal)); //
		 * accessoryTotalFee = //
		 * accessoryTotalFee.add(billingSlabs.get(0).getRate().multiply(uomVal)); } i++;
		 * } }
		 */

		BigDecimal accessoryTotalFee = getTotalFee(accessoryFees, calculationType);
		FeeAndBillingSlabIds feeAndBillingSlabIds = new FeeAndBillingSlabIds();
		feeAndBillingSlabIds.setFee(accessoryTotalFee);
		feeAndBillingSlabIds.setBillingSlabIds(billingSlabIds);
		feeAndBillingSlabIds.setId(UUID.randomUUID().toString());

		return feeAndBillingSlabIds;
	}

	/**
	 * Calculates total fee of by applying logic on list based on calculationType
	 * 
	 * @param fees            List of fee for different tradeType or accessories
	 * @param calculationType Calculation logic to be used
	 * @return Total Fee
	 */
	private BigDecimal getTotalFee(List<BigDecimal> fees, CalculationType calculationType) {
		BigDecimal totalFee = BigDecimal.ZERO;
		// Summation
		if (calculationType.equals(CalculationType.SUM))
			totalFee = fees.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

		// Average
		if (calculationType.equals(CalculationType.AVERAGE))
			totalFee = (fees.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(fees.size())))
					.setScale(2, 2);

		// Max
		if (calculationType.equals(CalculationType.MAX))
			totalFee = fees.stream().reduce(BigDecimal::max).get();

		// Min
		if (calculationType.equals(CalculationType.MIN))
			totalFee = fees.stream().reduce(BigDecimal::min).get();

		return totalFee;
	}
	
/*********************************************** access payment service start ***********************************************/
	
	public List<Calculation> accessCalculation(CalculationReq calculationReq,String apiServiceName,String calculationServiceName,String calculationType,int isIntialCalculation){
		String tenantId = calculationReq.getCalulationCriteria().get(0).getTenantId();
		DemandRequiredParamater demandRequiredParamater=null;
		Long taxPeriodFrom = System.currentTimeMillis();
		Long taxPeriodTo = System.currentTimeMillis();
		
	
		
		String consumerCode = calculationReq.getCalculatorRequest().getApplicationNumber();
		
		
		Calculation.builder().build();
		List<Calculation> calculations = new ArrayList<>();
		
		TradeLicense tradeLicense = utils.getTradeLicense(calculationReq.getRequestInfo(),
				calculationReq.getCalculatorRequest().getApplicationNumber(),
				calculationReq.getRequestInfo().getUserInfo().getTenantId());
		
		List<CalulationCriteria> calulationCriteria = new ArrayList<>();
		CalulationCriteria objectCalulationCriteria = new CalulationCriteria(tradeLicense,
				calculationReq.getCalculatorRequest().getApplicationNumber(),
				calculationReq.getRequestInfo().getUserInfo().getTenantId());
		calulationCriteria.add(objectCalulationCriteria);
		calculationReq.setCalulationCriteria(calulationCriteria);
		
		calculations=Arrays.asList(Calculation.builder().tradeLicense(tradeLicense).applicationNumber(consumerCode).tenantId(tenantId).build());
		
	    try {
		  if(calculationType.equals("1")){
			  calculations=calculationAllType(calculationReq.getRequestInfo(), calculations,isIntialCalculation,calculationServiceName, calculationType);
			  BigDecimal amount=calculations.get(0).getTaxHeadEstimates().get(0).getEstimateAmount();
			  demandRequiredParamater=DemandRequiredParamater.builder()
						.businessService(apiServiceName)
						.consumerCode(consumerCode)
						.taxPeriodFrom(taxPeriodFrom)
						.taxPeriodTo(taxPeriodTo)
						.consumerType("eg_tl_change_beneficial")
						.taxAmount(amount)
						.tenantId(tenantId)
						.build();
		  }
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(validateDemandParameter(demandRequiredParamater)) {
			CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();
			accessDemandService.generateDemand(calculationReq.getRequestInfo(), calculations, demandRequiredParamater);
			producer.push(config.getSaveTopic(), calculationRes);
		}else {
			throw new CustomException("400",
					"Some required parameter are null for demand and  bill creation");
	    }
		return calculations;
	}
	
	private boolean validateDemandParameter(DemandRequiredParamater demandRequiredParamater) {
		boolean isValid=false;
		if(demandRequiredParamater!=null
				&&demandRequiredParamater.getBusinessService()!=null
				&&demandRequiredParamater.getConsumerCode()!=null
				&&demandRequiredParamater.getConsumerType()!=null
				&&demandRequiredParamater.getTenantId()!=null
				&&demandRequiredParamater.getTaxAmount()!=null) {
			isValid=true;
		}
		
		return isValid;
	}
	
	private List<Calculation> calculationAllType(RequestInfo requestInfo,List<Calculation> calculations,int isIntial,String developerServiceCode,String calculationType) {
		BigDecimal licenseFee=new BigDecimal(0);	
		  
		if(calculationType.equals("1")){
			    BigDecimal administrativeCharge=new BigDecimal(0);
			    licenseFee=calculations.get(0).getTradeLicense().getTradeLicenseDetail().getLicenseFeeCharges();
			    Boolean isIntials=(isIntial==1?true:false);   
			    try { //COD , JDAMR, CISP, TOL
			    	  	switch (developerServiceCode) {
						
							case "COD":
								administrativeCharge=licenseFee.multiply(new BigDecimal(0.25)).multiply(new BigDecimal(isIntials?(0.4):(0.6)));
								break;
							case "JDAMR":
									// adminestrive charge has levied	
							    break;
							case "CISP":
								administrativeCharge=licenseFee.multiply(new BigDecimal(0.25)).multiply(new BigDecimal(isIntials?(0.4):(0.6)));
								break;
							case "TOL":
								administrativeCharge=licenseFee.multiply(new BigDecimal(0.25)).multiply(new BigDecimal(isIntials?(0.4):(0.6))).add(isIntials?(licenseFee.multiply(new BigDecimal(0.1))):(new BigDecimal(0)));
					        	break;
							default:
								break;
						}
			  			
			    	  	log.info("licenseFee Charge for change beneficial : "+licenseFee);
					    log.info("Administrative Charge for change beneficial : "+administrativeCharge);
					}catch (Exception e) {
						e.printStackTrace();
					  log.error("Exception : "+e.getMessage());
					}
				TaxHeadEstimate taxHeadEstimate=TaxHeadEstimate.builder().estimateAmount(administrativeCharge).category(Category.CHARGES).taxHeadCode("TL_TAX").build(); //CB_Charges :--change beneficial Charges
				calculations=calculations.stream().map(calculation ->{
					calculation.setTaxHeadEstimates(Arrays.asList(taxHeadEstimate));
					 return calculation;
				}).collect(Collectors.toList());
					
			}
		
			return calculations;
		
		}
	
	/*********************************************** access payment service end ***********************************************/
	

}
