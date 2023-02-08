package org.egov.bpa.calculator.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.egov.bpa.calculator.config.BPACalculatorConfig;
import org.egov.bpa.calculator.kafka.broker.BPACalculatorProducer;
import org.egov.bpa.calculator.utils.BPACalculatorConstants;
import org.egov.bpa.calculator.utils.CalculationUtils;
import org.egov.bpa.calculator.web.models.BillingSlabSearchCriteria;
import org.egov.bpa.calculator.web.models.Calculation;
import org.egov.bpa.calculator.web.models.CalculationReq;
import org.egov.bpa.calculator.web.models.CalculationRes;
import org.egov.bpa.calculator.web.models.CalulationCriteria;
import org.egov.bpa.calculator.web.models.EDCMaster;
import org.egov.bpa.calculator.web.models.LabourCessRatesMaster;
import org.egov.bpa.calculator.web.models.PurchasableFarMaster;
import org.egov.bpa.calculator.web.models.bpa.BPA;
import org.egov.bpa.calculator.web.models.bpa.EstimatesAndSlabs;
import org.egov.bpa.calculator.web.models.demand.Category;
import org.egov.bpa.calculator.web.models.demand.TaxHeadEstimate;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Service
@Slf4j
public class CalculationService {

	

	@Autowired
	private MDMSService mdmsService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private EDCRService edcrService;
	
	@Autowired
	private BPACalculatorConfig config;

	@Autowired
	private CalculationUtils utils;

	@Autowired
	private BPACalculatorProducer producer;


	@Autowired
	private BPAService bpaService;
	
	@Autowired
	private ObjectMapper mapper;

	/**
	 * Calculates tax estimates and creates demand
	 * 
	 * @param calculationReq
	 *            The calculationCriteria request
	 * @return List of calculations for all applicationNumbers or tradeLicenses
	 *         in calculationReq
	 */
	public List<Calculation> calculate(CalculationReq calculationReq) {
		String tenantId = calculationReq.getCalulationCriteria().get(0)
				.getTenantId();
		Object mdmsData = mdmsService.mDMSCall(calculationReq, tenantId);
		List<Calculation> calculations = getCalculation(calculationReq.getRequestInfo(),calculationReq.getCalulationCriteria(), mdmsData);
		demandService.generateDemand(calculationReq.getRequestInfo(),calculations, mdmsData);
		CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();
		producer.push(config.getSaveTopic(), calculationRes);
		return calculations;
	}

	/***
	 * Calculates tax estimates
	 * 
	 * @param requestInfo
	 *            The requestInfo of the calculation request
	 * @param criterias
	 *            list of CalculationCriteria containing the tradeLicense or
	 *            applicationNumber
	 * @return List of calculations for all applicationNumbers or tradeLicenses
	 *         in criterias
	 */
	public List<Calculation> getCalculation(RequestInfo requestInfo,
			List<CalulationCriteria> criterias, Object mdmsData) {
		List<Calculation> calculations = new LinkedList<>();
		for (CalulationCriteria criteria : criterias) {
			BPA bpa;
			if (criteria.getBpa() == null
					&& criteria.getApplicationNo() != null) {
				bpa = bpaService.getBuildingPlan(requestInfo, criteria.getTenantId(),
						criteria.getApplicationNo(), null);
				criteria.setBpa(bpa);
			}

			EstimatesAndSlabs estimatesAndSlabs = getTaxHeadEstimates(criteria,
					requestInfo, mdmsData);
			List<TaxHeadEstimate> taxHeadEstimates = estimatesAndSlabs
					.getEstimates();

			Calculation calculation = new Calculation();
			calculation.setBpa(criteria.getBpa());
			calculation.setTenantId(criteria.getTenantId());
			calculation.setTaxHeadEstimates(taxHeadEstimates);
			calculation.setFeeType( criteria.getFeeType());
			calculations.add(calculation);

		}
		return calculations;
	}

	/**
	 * Creates TacHeadEstimates
	 * 
	 * @param calulationCriteria
	 *            CalculationCriteria containing the tradeLicense or
	 *            applicationNumber
	 * @param requestInfo
	 *            The requestInfo of the calculation request
	 * @return TaxHeadEstimates and the billingSlabs used to calculate it
	 */
	private EstimatesAndSlabs getTaxHeadEstimates(
			CalulationCriteria calulationCriteria, RequestInfo requestInfo,
			Object mdmsData) {
		List<TaxHeadEstimate> estimates = new LinkedList<>();
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put(BPACalculatorConstants.PARAM_MAP_BPA, calulationCriteria.getBpa());
		paramMap.put(BPACalculatorConstants.PARAM_MAP_REQUEST_INFO, requestInfo);
		List<CalulationCriteria> calculationCriterias = new ArrayList<>();
		calculationCriterias.add(calulationCriteria);
		CalculationReq calculationReq = CalculationReq.builder().calulationCriteria(calculationCriterias)
				.requestInfo(requestInfo).build();
		EstimatesAndSlabs estimatesAndSlabs;
		prepareParamMap(paramMap);
		if (calulationCriteria.getFeeType().equalsIgnoreCase(BPACalculatorConstants.LOW_RISK_PERMIT_FEE_TYPE)) {

//			 stopping Application fee for lowrisk applicaiton according to BBI-391
			calulationCriteria.setFeeType(BPACalculatorConstants.MDMS_CALCULATIONTYPE_LOW_APL_FEETYPE);
			estimatesAndSlabs = getBaseTax(calulationCriteria, requestInfo, mdmsData);

			estimates.addAll(estimatesAndSlabs.getEstimates());

			calulationCriteria.setFeeType(BPACalculatorConstants.MDMS_CALCULATIONTYPE_LOW_SANC_FEETYPE);
			estimatesAndSlabs = getBaseTax(calulationCriteria, requestInfo, mdmsData);

			estimates.addAll(estimatesAndSlabs.getEstimates());

			calulationCriteria.setFeeType(BPACalculatorConstants.LOW_RISK_PERMIT_FEE_TYPE);

		} else if (calulationCriteria.getFeeType()
				.equalsIgnoreCase(BPACalculatorConstants.MDMS_CALCULATIONTYPE_APL_FEETYPE)) {
			//my code for application fees calculation-
			calculateScrutinyFee(paramMap, estimates);
			estimatesAndSlabs = new EstimatesAndSlabs();
		} else if (calulationCriteria.getFeeType().equalsIgnoreCase(BPACalculatorConstants.FEE_TYPE_LABOUR_CESS)) {
			calculateLabourCess(calculationReq, calulationCriteria.getTenantId(), paramMap, estimates);
			estimatesAndSlabs = new EstimatesAndSlabs();
		} else if (calulationCriteria.getFeeType().equalsIgnoreCase(BPACalculatorConstants.FEE_TYPE_PURCHASABLE_FAR)) {
			calculatePurchasableFar(calculationReq, calulationCriteria.getBpa().getTenantId(), paramMap, estimates);
			estimatesAndSlabs = new EstimatesAndSlabs();
		} else if (calulationCriteria.getFeeType().equalsIgnoreCase(BPACalculatorConstants.FEE_TYPE_EDC)) {
			calculateEDCFee(paramMap, estimates, calculationReq, calulationCriteria.getBpa().getTenantId());
			estimatesAndSlabs = new EstimatesAndSlabs();
		} else {
			estimatesAndSlabs = getBaseTax(calulationCriteria, requestInfo, mdmsData);
			estimates.addAll(estimatesAndSlabs.getEstimates());
		}

		estimatesAndSlabs.setEstimates(estimates);

		return estimatesAndSlabs;
	}
	
	private void prepareParamMap(Map<String,Object> paramMap) {
		LinkedHashMap edcrDetails = edcrService.getEDCRDetails(
				(RequestInfo) paramMap.get(BPACalculatorConstants.PARAM_MAP_REQUEST_INFO),
				(BPA) paramMap.get(BPACalculatorConstants.PARAM_MAP_BPA));
		String edcrDetailsString = new JSONObject(edcrDetails).toString();
		DocumentContext edcrContext = JsonPath.using(Configuration.defaultConfiguration()).parse(edcrDetailsString);
		//JSONArray permitNumber = edcrContext.read("edcrDetail.*.permitNumber");
		JSONArray totalBuiltupAreaJson = edcrContext.read(BPACalculatorConstants.TOTAL_BUILTUP_AREA_PATH);
		if (!CollectionUtils.isEmpty(totalBuiltupAreaJson) && Objects.nonNull(totalBuiltupAreaJson.get(0))) {
			String totalBuiltupAreaString = totalBuiltupAreaJson.get(0).toString();
			BigDecimal totalBuiltUpArea = new BigDecimal(totalBuiltupAreaString);
			paramMap.put(BPACalculatorConstants.TOTAL_BUILTUP_AREA, totalBuiltUpArea);
		}
		JSONArray occupancyTypeJson = edcrContext.read(BPACalculatorConstants.OCCUPANCY_TYPE_PATH);
		if (!CollectionUtils.isEmpty(occupancyTypeJson) && Objects.nonNull(occupancyTypeJson.get(0))) {
			String occupancyType = occupancyTypeJson.get(0).toString();
			paramMap.put(BPACalculatorConstants.OCCUPANCY_TYPE, occupancyType);
		}
		JSONArray subOccupancyTypeJson = edcrContext.read(BPACalculatorConstants.SUB_OCCUPANCY_TYPE_PATH);
		if (!CollectionUtils.isEmpty(subOccupancyTypeJson) && Objects.nonNull(subOccupancyTypeJson.get(0))) {
			String subOccupancyType = subOccupancyTypeJson.get(0).toString();
			paramMap.put(BPACalculatorConstants.SUB_OCCUPANCY_TYPE, subOccupancyType);
		}
		JSONArray usageJson = edcrContext.read(BPACalculatorConstants.USAGE_CATEGORY_PATH);
		if (!CollectionUtils.isEmpty(usageJson) && Objects.nonNull(usageJson.get(0))) {
			String usageCategory = usageJson.get(0).toString();
			paramMap.put(BPACalculatorConstants.USAGE_CATEGORY, usageCategory);
		}
		JSONArray subUsageJson = edcrContext.read(BPACalculatorConstants.SUB_USAGE_CATEGORY_PATH);
		if (!CollectionUtils.isEmpty(subUsageJson) && Objects.nonNull(subUsageJson.get(0))) {
			String subUsageCategory = subUsageJson.get(0).toString();
			paramMap.put(BPACalculatorConstants.SUB_USAGE_CATEGORY, subUsageCategory);
		}
		JSONArray plotAreaJson = edcrContext.read(BPACalculatorConstants.PLOT_AREA_PATH);
		if (!CollectionUtils.isEmpty(plotAreaJson) && Objects.nonNull(plotAreaJson.get(0))) {
			String plotArea = plotAreaJson.get(0).toString();
			paramMap.put(BPACalculatorConstants.PLOT_AREA, new BigDecimal(plotArea));
		}
		JSONArray providedFarJson = edcrContext.read(BPACalculatorConstants.PROVIDED_FAR_PATH);
		if (!CollectionUtils.isEmpty(providedFarJson) && Objects.nonNull(providedFarJson.get(0))) {
			String providedFar = providedFarJson.get(0).toString();
			paramMap.put(BPACalculatorConstants.EXISTING_FAR, new BigDecimal(providedFar));
		}
		JSONArray districtNameJson = edcrContext.read(BPACalculatorConstants.DISTRICT_NAME_PATH);
		if (!CollectionUtils.isEmpty(districtNameJson) && Objects.nonNull(districtNameJson.get(0))) {
			String districtName = districtNameJson.get(0).toString();
			paramMap.put(BPACalculatorConstants.DISTRICT_NAME, districtName);
		}
		JSONArray developmentPlanJson = edcrContext.read(BPACalculatorConstants.DEVELOPMENT_PLAN_PATH);
		if (!CollectionUtils.isEmpty(developmentPlanJson) && Objects.nonNull(developmentPlanJson.get(0))) {
			String developmentPlan = developmentPlanJson.get(0).toString();
			paramMap.put(BPACalculatorConstants.DEVELOPMENT_PLAN, developmentPlan);
		}
	}
	
	private BigDecimal calculateScrutinyFee(Map<String, Object> paramMap, List<TaxHeadEstimate> estimates) {
		String occupancyTypeForResidentialOrNonResidential = null;
		BigDecimal scrutinyFee = BigDecimal.ZERO;
		BPA bpa = (BPA) paramMap.get(BPACalculatorConstants.PARAM_MAP_BPA);
		if (Objects.nonNull(paramMap.get(BPACalculatorConstants.OCCUPANCY_TYPE))) {
			occupancyTypeForResidentialOrNonResidential = paramMap.get(BPACalculatorConstants.OCCUPANCY_TYPE).toString();
		}
		if (BPACalculatorConstants.OCCUPANCY_TYPE_RESIDENTIAL_CODE.equals(occupancyTypeForResidentialOrNonResidential)) {
			scrutinyFee = calculateScrutinyFeeForResidential(paramMap);
		} else {
			scrutinyFee = calculateScrutinyFeeForNonResidential(paramMap);
		}
		generateTaxHeadEstimate(estimates, scrutinyFee, BPACalculatorConstants.TAXHEADCODE_APPLICATION_FEE, Category.FEE);
		log.info("ApplicationFees:::::::::::" + scrutinyFee);
		return scrutinyFee;
	}
	
	private BigDecimal calculateScrutinyFeeForResidential(Map<String, Object> paramMap) {
		BigDecimal totalBuiltUpArea = BigDecimal.ZERO;
		if (Objects.nonNull(paramMap)
				&& !StringUtils.isEmpty(paramMap.get(BPACalculatorConstants.TOTAL_BUILTUP_AREA))) {
			totalBuiltUpArea = new BigDecimal(paramMap.get(BPACalculatorConstants.TOTAL_BUILTUP_AREA).toString());
		}
		//assumption:totalBuiltupArea coming from edcr details will be in square metre
		BigDecimal scrutinyFee = totalBuiltUpArea.multiply(BPACalculatorConstants.RESIDENTIAL_APPL_FEE_RATE_PER_SQ_MT);
		return scrutinyFee;
	}
	
	private BigDecimal calculateScrutinyFeeForNonResidential(Map<String, Object> paramMap) {
		BigDecimal totalBuiltupArea = BigDecimal.ZERO;
		String usageCategory = null;
		String subUsageCategory = null;
		if (!StringUtils.isEmpty(paramMap.get(BPACalculatorConstants.USAGE_CATEGORY))) {
			usageCategory = paramMap.get(BPACalculatorConstants.USAGE_CATEGORY).toString();
		}
		if (!StringUtils.isEmpty(paramMap.get(BPACalculatorConstants.SUB_USAGE_CATEGORY))) {
			subUsageCategory = paramMap.get(BPACalculatorConstants.SUB_USAGE_CATEGORY).toString();
		}
		if (Objects.nonNull(paramMap.get(BPACalculatorConstants.TOTAL_BUILTUP_AREA))) {
			totalBuiltupArea = (BigDecimal) paramMap.get(BPACalculatorConstants.TOTAL_BUILTUP_AREA);
		}
		BigDecimal rate = BigDecimal.ZERO;
		switch (usageCategory) {
		case BPACalculatorConstants.USAGE_CATEGORY_GH:
			switch (subUsageCategory) {
			case BPACalculatorConstants.SUB_USAGE_CATEGORY_GH_UPTO_21:
				rate = BPACalculatorConstants.NON_RESIDENTIAL_GH_UPTO_21_APPL_FEE_RATE_PER_SQ_MT;
				break;
			case BPACalculatorConstants.SUB_USAGE_CATEGORY_GH_ABOVE_21:
				rate = BPACalculatorConstants.NON_RESIDENTIAL_GH_ABOVE_21_APPL_FEE_RATE_PER_SQ_MT;
				break;
			}
			break;
		case BPACalculatorConstants.USAGE_CATEGORY_COMMERCIAL:
			rate = BPACalculatorConstants.NON_RESIDENTIAL_COMMERICAL_APPL_FEE_RATE_PER_SQ_MT;
			break;
		case BPACalculatorConstants.USAGE_CATEGORY_INDUSTRIAL:
			rate = BPACalculatorConstants.NON_RESIDENTIAL_INDUSTRIAL_APPL_FEE_RATE_PER_SQ_MT;
			break;
		case BPACalculatorConstants.USAGE_CATEGORY_INSTITUTIONAL:
			rate = BPACalculatorConstants.NON_RESIDENTIAL_INSTITUTIONAL_APPL_FEE_RATE_PER_SQ_MT;
			break;
		default:
			rate = BPACalculatorConstants.APPL_FEE_DEFAULT_RATE_PER_SQ_MT;
			break;
		}
		BigDecimal scrutinyFee = rate.multiply(totalBuiltupArea);
		return scrutinyFee;
	}
	
	private BigDecimal calculateLabourCess(CalculationReq calculationReq, String tenantId, Map<String, Object> paramMap,
			List<TaxHeadEstimate> estimates) {
		String residentialOrOtherThanResidential = null;
		BigDecimal labourCess = BigDecimal.ZERO;
		BPA bpa = (BPA) paramMap.get(BPACalculatorConstants.PARAM_MAP_BPA);
		if (Objects.nonNull(paramMap.get(BPACalculatorConstants.OCCUPANCY_TYPE))) {
			residentialOrOtherThanResidential = paramMap.get(BPACalculatorConstants.OCCUPANCY_TYPE).toString();
		}
		if (BPACalculatorConstants.OCCUPANCY_TYPE_RESIDENTIAL_CODE.equals(residentialOrOtherThanResidential)) {
			labourCess = calculateLabourCessForResidential(paramMap);
		} else {
			labourCess = calculateLabourCessForNonResidential(calculationReq, tenantId, paramMap, estimates);
		}
		generateTaxHeadEstimate(estimates, labourCess, BPACalculatorConstants.TAXHEADCODE_LABOUR_CESS, Category.FEE);
		log.info("LabourCess:::::::::::" + labourCess);
		return labourCess;
	}
	
	private BigDecimal calculateLabourCessForResidential(Map<String, Object> paramMap) {
		BigDecimal totalBuiltupArea = BigDecimal.ZERO;
		BigDecimal labourCess = BigDecimal.ZERO;
		if (Objects.nonNull(paramMap.get(BPACalculatorConstants.TOTAL_BUILTUP_AREA))) {
			totalBuiltupArea = (BigDecimal) paramMap.get(BPACalculatorConstants.TOTAL_BUILTUP_AREA);
		}
		if(totalBuiltupArea.compareTo(BPACalculatorConstants.LABOUR_CESS_MINIMUM_AREA)<=0) {
			return labourCess;
		}
		// assumption:totalBuiltupArea coming from edcr details will be in square metre
		BigDecimal constructionCost = totalBuiltupArea.multiply(BPACalculatorConstants.SQ_MTR_TO_SQ_FEET_CONVERTER)
				.multiply(BPACalculatorConstants.RESIDENTIAL_CONSTRUCTION_COST_RATE_PER_SQ_FEET);
		if (constructionCost.compareTo(BPACalculatorConstants.LABOUR_CESS__MIN_CONSTRUCTION_COST) > 0) {
			labourCess = constructionCost
					.multiply(BPACalculatorConstants.RESIDENTIAL_LABOUR_CESS_FACTOR_OF_CONSTRUCTION_COST);
		}
		return labourCess;
	}
	
	private BigDecimal calculateLabourCessForNonResidential(CalculationReq calculationReq, String tenantId,
			Map<String, Object> paramMap, List<TaxHeadEstimate> estimates) {
		String use = null;
		String subUse = null;
		BigDecimal totalBuiltupArea = BigDecimal.ZERO;
		BigDecimal labourCess = BigDecimal.ZERO;
		if (!StringUtils.isEmpty(paramMap.get(BPACalculatorConstants.USAGE_CATEGORY))) {
			use = paramMap.get(BPACalculatorConstants.USAGE_CATEGORY).toString();
		}
		if (!StringUtils.isEmpty(paramMap.get(BPACalculatorConstants.SUB_USAGE_CATEGORY))) {
			subUse = paramMap.get(BPACalculatorConstants.SUB_USAGE_CATEGORY).toString();
		}
		if (Objects.nonNull(paramMap.get(BPACalculatorConstants.TOTAL_BUILTUP_AREA))) {
			totalBuiltupArea = (BigDecimal) paramMap.get(BPACalculatorConstants.TOTAL_BUILTUP_AREA);
		}
		//hardcode as of now as use and subuse path not available from edcrDetails-
		use="Group Housing";
		subUse="The Multi-storey flats have a basement and the construction is of superfine type \\\"A\\\"";
		LabourCessRatesMaster labourCessRatesMaster = fetchLabourCessRatesMdms(calculationReq, tenantId, use, subUse);
		// assumption: builtup area is in square metres
		labourCess = totalBuiltupArea.multiply(labourCessRatesMaster.getLabourCessRate());
		
		return labourCess;
	}
	
	private LabourCessRatesMaster fetchLabourCessRatesMdms(CalculationReq calculationReq, String tenantId,
			String use, String subUse) {
		List<MasterDetail> bpaMasterDetails = new ArrayList<>();
		String filter = "[?(@.use=='{use}' && @.subUse=='{subUse}')]";
		filter = filter.replace("{use}", use);
		filter = filter.replace("{subUse}", subUse);
        bpaMasterDetails.add(MasterDetail.builder().name(BPACalculatorConstants.MDMS_LABOUR_CESS_RATES)
        		.filter(filter)
        		.build());
		MdmsCriteriaReq mdmsCriteriaReq = mdmsService.prepareMdmsCriteriaReqForBpaModule(calculationReq, tenantId,
				bpaMasterDetails);
		MdmsResponse mdmsResponse = mdmsService.mDMSCustomCall(calculationReq, tenantId, mdmsCriteriaReq);
		LabourCessRatesMaster labourCessRatesMaster = null;
		if (Objects.nonNull(mdmsResponse) && Objects.nonNull(mdmsResponse.getMdmsRes())
				&& Objects.nonNull(mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA))
				&& Objects.nonNull(mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA)
						.get(BPACalculatorConstants.MDMS_LABOUR_CESS_RATES))
				&& !mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA)
						.get(BPACalculatorConstants.MDMS_LABOUR_CESS_RATES).isEmpty()) {
			labourCessRatesMaster = mapper.convertValue(mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA)
					.get(BPACalculatorConstants.MDMS_LABOUR_CESS_RATES).get(0), LabourCessRatesMaster.class);
		} else {
			labourCessRatesMaster = LabourCessRatesMaster.builder().use(use).subUse(subUse)
					.labourCessRate(BigDecimal.ZERO).build();
		}
		return labourCessRatesMaster;
		//PurchasableFarMaster purchasableFarMaster = PurchasableFarMaster.builder()
	}
	
	private BigDecimal calculatePurchasableFar(CalculationReq calculationReq, String tenantId,
			Map<String, Object> paramMap, List<TaxHeadEstimate> estimates) {
		BigDecimal plotSize = BigDecimal.ZERO;
		BigDecimal existingFar = BigDecimal.ZERO;
		String zoneType = null;
		BigDecimal totalBuiltupArea = BigDecimal.ZERO;
		BigDecimal purchasableFarFee = BigDecimal.ZERO;
		BPA bpa = (BPA) paramMap.get(BPACalculatorConstants.PARAM_MAP_BPA);
		if (Objects.nonNull(paramMap.get(BPACalculatorConstants.PLOT_AREA))) {
			plotSize = (BigDecimal) paramMap.get(BPACalculatorConstants.PLOT_AREA);
		}
		if (Objects.nonNull(paramMap.get(BPACalculatorConstants.EXISTING_FAR))) {
			existingFar = (BigDecimal) paramMap.get(BPACalculatorConstants.EXISTING_FAR);
		}
		if (!StringUtils.isEmpty(paramMap.get(BPACalculatorConstants.ZONE))) {
			zoneType = paramMap.get(BPACalculatorConstants.ZONE).toString();
		}
		if (Objects.nonNull(paramMap.get(BPACalculatorConstants.TOTAL_BUILTUP_AREA))) {
			totalBuiltupArea = (BigDecimal) paramMap.get(BPACalculatorConstants.TOTAL_BUILTUP_AREA);
		}
		//hardcode zonetype as of now-
		zoneType = "Hyper";
		String plotSizeCategory = getPlotSizeCategory(plotSize);
		//fetch from master data the exact rate-
		PurchasableFarMaster purchasableFarMaster = fetchPurchasableFarForCriteria(calculationReq, tenantId,
				plotSizeCategory, zoneType);
		if (existingFar.compareTo(purchasableFarMaster.getExistingFar()) > 0
				&& existingFar.compareTo(purchasableFarMaster.getNewTotalFar()) < 0) {
			purchasableFarFee = totalBuiltupArea.multiply(purchasableFarMaster.getPurchasableFarRate());
		}
		generateTaxHeadEstimate(estimates, purchasableFarFee, BPACalculatorConstants.TAXHEADCODE_PURCHASABLE_FAR, Category.FEE);
		log.info("PurchasableFar:::::::::::" + purchasableFarFee);
		return purchasableFarFee;
	}
	
	private String getPlotSizeCategory(BigDecimal plotSize) {
		String plotSizeCategory = null;
		BigDecimal PLOT_SIZE_SEVENTY_FIVE = new BigDecimal("75");
		BigDecimal PLOT_SIZE_ONE_HUNDRED = new BigDecimal("100");
		BigDecimal PLOT_SIZE_ONE_HUNDRED_FIFTY = new BigDecimal("150");
		BigDecimal PLOT_SIZE_TWO_HUNDRED = new BigDecimal("200");
		BigDecimal PLOT_SIZE_TWO_HUNDRED_FIFTY = new BigDecimal("250");
		BigDecimal PLOT_SIZE_THREE_HUNDRED_FIFTY = new BigDecimal("350");
		BigDecimal PLOT_SIZE_FIVE_HUNDRED = new BigDecimal("500");
		//assumption:plotsize is in sq mtr.
		if(plotSize.compareTo(PLOT_SIZE_SEVENTY_FIVE)<=0) {
			plotSizeCategory = "Up to 75 Sq. Mtr.";
		}else if(plotSize.compareTo(PLOT_SIZE_SEVENTY_FIVE)>0 && plotSize.compareTo(PLOT_SIZE_ONE_HUNDRED)<=0) {
			plotSizeCategory = "Above 75 Sq. Mtr. & up to 100 Sq. Mtr.";
		}else if(plotSize.compareTo(PLOT_SIZE_ONE_HUNDRED)>0 && plotSize.compareTo(PLOT_SIZE_ONE_HUNDRED_FIFTY)<=0) {
			plotSizeCategory = "Above 100 Sq. Mtr. & up to 150 Sq. Mtr.";
		}else if(plotSize.compareTo(PLOT_SIZE_ONE_HUNDRED_FIFTY)>0 && plotSize.compareTo(PLOT_SIZE_TWO_HUNDRED)<=0) {
			plotSizeCategory = "Above 150 & up to 200sqm";
		}else if(plotSize.compareTo(PLOT_SIZE_TWO_HUNDRED)>0 && plotSize.compareTo(PLOT_SIZE_TWO_HUNDRED_FIFTY)<=0) {
			plotSizeCategory = "Above 200 & up to 250sqm";
		}else if(plotSize.compareTo(PLOT_SIZE_TWO_HUNDRED_FIFTY)>0 && plotSize.compareTo(PLOT_SIZE_THREE_HUNDRED_FIFTY)<=0) {
			plotSizeCategory = "Above 250 & up to 350sqm";
		}else if(plotSize.compareTo(PLOT_SIZE_THREE_HUNDRED_FIFTY)>0 && plotSize.compareTo(PLOT_SIZE_FIVE_HUNDRED)<=0) {
			plotSizeCategory = "Above 350 & up to 500sqm";
		}else if(plotSize.compareTo(PLOT_SIZE_FIVE_HUNDRED)>0) {
			plotSizeCategory = "Above 500sqm";
		}
		return plotSizeCategory;
	}
	
	private PurchasableFarMaster fetchPurchasableFarForCriteria(CalculationReq calculationReq, String tenantId,
			String plotSizeCategory, String zone) {
		List<MasterDetail> bpaMasterDetails = new ArrayList<>();
		String filter = "[?(@.plotSizeCategory=='{plotSizeCategory}' && @.zone=='{zone}')]";
		filter = filter.replace("{plotSizeCategory}", plotSizeCategory);
		filter = filter.replace("{zone}", zone);
        bpaMasterDetails.add(MasterDetail.builder().name(BPACalculatorConstants.MDMS_PURCHASABLE_FAR)
        		.filter(filter)
        		.build());
		MdmsCriteriaReq mdmsCriteriaReq = mdmsService.prepareMdmsCriteriaReqForBpaModule(calculationReq, tenantId,
				bpaMasterDetails);
		MdmsResponse mdmsResponse = mdmsService.mDMSCustomCall(calculationReq, tenantId, mdmsCriteriaReq);
		PurchasableFarMaster purchasableFarMaster = null;
		if(Objects.nonNull(mdmsResponse)&& Objects.nonNull(mdmsResponse.getMdmsRes())
				&& Objects.nonNull(mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA))
				&& Objects.nonNull(mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA).get(BPACalculatorConstants.MDMS_PURCHASABLE_FAR))
				&& !mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA).get(BPACalculatorConstants.MDMS_PURCHASABLE_FAR).isEmpty()) {
			purchasableFarMaster = mapper.convertValue(
					mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA).get(BPACalculatorConstants.MDMS_PURCHASABLE_FAR).get(0),
					PurchasableFarMaster.class);
		}else {
			purchasableFarMaster = PurchasableFarMaster.builder()
					.plotSizeCategory(plotSizeCategory)
					.zone(zone)
					.existingFar(BigDecimal.ZERO)
					.maxAdditionalPurchasableFar(BigDecimal.ZERO)
					.newTotalFar(BigDecimal.ZERO)
					.build();
		}
		return purchasableFarMaster;
	}
	
	private BigDecimal calculateEDCFee(Map<String, Object> paramMap, List<TaxHeadEstimate> estimates,
			CalculationReq calculationReq, String tenantId) {
		BigDecimal edcFee = BigDecimal.ZERO;
		String districtName = null;
		String developmentPlan = null;
		BigDecimal plotArea = null;
		if (!StringUtils.isEmpty(paramMap.get(BPACalculatorConstants.DISTRICT_NAME))) {
			districtName = paramMap.get(BPACalculatorConstants.DISTRICT_NAME).toString();
		}
		if (!StringUtils.isEmpty(paramMap.get(BPACalculatorConstants.DEVELOPMENT_PLAN))) {
			developmentPlan = paramMap.get(BPACalculatorConstants.DEVELOPMENT_PLAN).toString();
		}
		if (Objects.nonNull(paramMap.get(BPACalculatorConstants.PLOT_AREA))) {
			plotArea = (BigDecimal) paramMap.get(BPACalculatorConstants.PLOT_AREA);
		}
		//hardcode as of now as path from edcr not available for districtName and developmentPlan-
		districtName = "Gurgaon";
		developmentPlan = "Gurgaon";
		EDCMaster edcMaster = fetchEdcMaster(calculationReq, tenantId, districtName, developmentPlan);
		edcFee = plotArea.multiply(edcMaster.getIndex())
				.multiply(BPACalculatorConstants.BASE_EDC_AMOUNT_RUPEES_PER_ACRE)
				.divide(BPACalculatorConstants.PER_ACRE_TO_PER_SQM_DIVIDE_BY, 2, RoundingMode.HALF_DOWN)
				.divide(BPACalculatorConstants.EDC_APPLICABLE_AREA_FACTOR_DIVIDE, 2, RoundingMode.HALF_DOWN)
				.multiply(BPACalculatorConstants.EDC_APPLICABLE_ONE_THIRD_FACTOR);
		generateTaxHeadEstimate(estimates, edcFee, BPACalculatorConstants.TAXHEADCODE_EDC, Category.FEE);
		log.info("EDCFee:::::::::::" + edcFee);
		return edcFee;
	}
	
	private EDCMaster fetchEdcMaster(CalculationReq calculationReq, String tenantId,
			String districtName,String developmentPlan) {
		List<MasterDetail> bpaMasterDetails = new ArrayList<>();
		String filter = "[?(@.districtName=='{districtName}' && @.developmentPlan=='{developmentPlan}')]";
		filter = filter.replace("{districtName}", districtName);
		filter = filter.replace("{developmentPlan}", developmentPlan);
        bpaMasterDetails.add(MasterDetail.builder().name(BPACalculatorConstants.MDMS_EDC_RATES)
        		.filter(filter)
        		.build());
		MdmsCriteriaReq mdmsCriteriaReq = mdmsService.prepareMdmsCriteriaReqForBpaModule(calculationReq, tenantId,
				bpaMasterDetails);
		MdmsResponse mdmsResponse = mdmsService.mDMSCustomCall(calculationReq, tenantId, mdmsCriteriaReq);
		
		EDCMaster eDCMaster = null;
		if (Objects.nonNull(mdmsResponse) && Objects.nonNull(mdmsResponse.getMdmsRes())
				&& Objects.nonNull(mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA))
				&& Objects.nonNull(mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA)
						.get(BPACalculatorConstants.MDMS_EDC_RATES))
				&& !mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA)
						.get(BPACalculatorConstants.MDMS_EDC_RATES).isEmpty()) {
			eDCMaster = mapper.convertValue(mdmsResponse.getMdmsRes().get(BPACalculatorConstants.MDMS_BPA)
					.get(BPACalculatorConstants.MDMS_EDC_RATES).get(0), EDCMaster.class);
		} else {
			eDCMaster = EDCMaster.builder().districtName(districtName).developmentPlan(developmentPlan)
					.index(new BigDecimal("0")).build();
		}

		return eDCMaster;
	}
	
	private void generateTaxHeadEstimate(List<TaxHeadEstimate> estimates, BigDecimal feeAmount, String taxHeadCode,
			Category category) {
		TaxHeadEstimate estimate = new TaxHeadEstimate();
		estimate.setEstimateAmount(feeAmount.setScale(0, BigDecimal.ROUND_UP));
		estimate.setCategory(category);
		estimate.setTaxHeadCode(taxHeadCode);
		estimates.add(estimate);
	}

	/**
	 * Calculates base tax and cretaes its taxHeadEstimate
	 * 
	 * @param calulationCriteria
	 *            CalculationCriteria containing the tradeLicense or
	 *            applicationNumber
	 * @param requestInfo
	 *            The requestInfo of the calculation request
	 * @return BaseTax taxHeadEstimate and billingSlabs used to calculate it
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private EstimatesAndSlabs getBaseTax(CalulationCriteria calulationCriteria, RequestInfo requestInfo,
			Object mdmsData) {
		BPA bpa = calulationCriteria.getBpa();
		EstimatesAndSlabs estimatesAndSlabs = new EstimatesAndSlabs();
		BillingSlabSearchCriteria searchCriteria = new BillingSlabSearchCriteria();
		searchCriteria.setTenantId(bpa.getTenantId());

		Map calculationTypeMap = mdmsService.getCalculationType(requestInfo, bpa, mdmsData,
				calulationCriteria.getFeeType());
		int calculatedAmout = 0;
		ArrayList<TaxHeadEstimate> estimates = new ArrayList<TaxHeadEstimate>();
		if (calculationTypeMap.containsKey("calsiLogic")) {
			LinkedHashMap ocEdcr = edcrService.getEDCRDetails(requestInfo, bpa);
			String jsonString = new JSONObject(ocEdcr).toString();
			DocumentContext context = JsonPath.using(Configuration.defaultConfiguration()).parse(jsonString);
			JSONArray permitNumber = context.read("edcrDetail.*.permitNumber");
			String jsonData = new JSONObject(calculationTypeMap).toString();
			DocumentContext calcContext = JsonPath.using(Configuration.defaultConfiguration()).parse(jsonData);
			JSONArray parameterPaths = calcContext.read("calsiLogic.*.paramPath");
			JSONArray tLimit = calcContext.read("calsiLogic.*.tolerancelimit");
			System.out.println("tolerance limit in: " + tLimit.get(0));
			DocumentContext edcrContext = null;
			if (!CollectionUtils.isEmpty(permitNumber)) {
				BPA permitBpa = bpaService.getBuildingPlan(requestInfo, bpa.getTenantId(), null,
						permitNumber.get(0).toString());
				if (permitBpa.getEdcrNumber() != null) {
					LinkedHashMap edcr = edcrService.getEDCRDetails(requestInfo, permitBpa);
					String edcrData = new JSONObject(edcr).toString();
					edcrContext = JsonPath.using(Configuration.defaultConfiguration()).parse(edcrData);
				}
			}
			
			for (int i = 0; i < parameterPaths.size(); i++) {
				Double ocTotalBuitUpArea = context.read(parameterPaths.get(i).toString());
				Double bpaTotalBuitUpArea = edcrContext.read(parameterPaths.get(i).toString());
				Double diffInBuildArea = ocTotalBuitUpArea - bpaTotalBuitUpArea;
				System.out.println("difference in area: " + diffInBuildArea);
				Double limit = Double.valueOf(tLimit.get(i).toString());
				if (diffInBuildArea > limit) {
					JSONArray data = calcContext.read("calsiLogic.*.deviation");
					System.out.println(data.get(0));
					JSONArray data1 = (JSONArray) data.get(0);
					for (int j = 0; j < data1.size(); j++) {
						LinkedHashMap diff = (LinkedHashMap) data1.get(j);
						Integer from = (Integer) diff.get("from");
						Integer to = (Integer) diff.get("to");
						Integer uom = (Integer) diff.get("uom");
						Integer mf = (Integer) diff.get("MF");
						if (diffInBuildArea >= from && diffInBuildArea <= to) {
							calculatedAmout = (int) (diffInBuildArea * mf * uom);
							break;
						}
					}
				} else {
					calculatedAmout = 0;
				}
				TaxHeadEstimate estimate = new TaxHeadEstimate();
				BigDecimal totalTax = BigDecimal.valueOf(calculatedAmout);
				if (totalTax.compareTo(BigDecimal.ZERO) == -1)
					throw new CustomException(BPACalculatorConstants.INVALID_AMOUNT, "Tax amount is negative");

				estimate.setEstimateAmount(totalTax);
				estimate.setCategory(Category.FEE);

				String taxHeadCode = utils.getTaxHeadCode(bpa.getBusinessService(), calulationCriteria.getFeeType());
				estimate.setTaxHeadCode(taxHeadCode);
				estimates.add(estimate);
			}
		} else {
			TaxHeadEstimate estimate = new TaxHeadEstimate();
			calculatedAmout = Integer
					.parseInt(calculationTypeMap.get(BPACalculatorConstants.MDMS_CALCULATIONTYPE_AMOUNT).toString());

			BigDecimal totalTax = BigDecimal.valueOf(calculatedAmout);
			if (totalTax.compareTo(BigDecimal.ZERO) == -1)
				throw new CustomException(BPACalculatorConstants.INVALID_AMOUNT, "Tax amount is negative");

			estimate.setEstimateAmount(totalTax);
			estimate.setCategory(Category.FEE);

			String taxHeadCode = utils.getTaxHeadCode(bpa.getBusinessService(), calulationCriteria.getFeeType());
			estimate.setTaxHeadCode(taxHeadCode);
			estimates.add(estimate);
		}
		estimatesAndSlabs.setEstimates(estimates);
		return estimatesAndSlabs;
	}

}
