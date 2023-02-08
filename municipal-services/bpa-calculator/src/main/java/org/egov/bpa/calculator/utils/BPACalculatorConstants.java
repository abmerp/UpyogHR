package org.egov.bpa.calculator.utils;

import java.math.BigDecimal;

public class BPACalculatorConstants {

	

    public static final String MDMS_EGF_MASTER = "egf-master";

    public static final String MDMS_FINANCIALYEAR  = "FinancialYear";

    public static final String MDMS_FINACIALYEAR_PATH = "$.MdmsRes.egf-master.FinancialYear[?(@.code==\"{}\")]";

    public static final String MDMS_STARTDATE  = "startingDate";

    public static final String MDMS_ENDDATE  = "endingDate";

    public static final String MDMS_CALCULATIONTYPE = "CalculationType";

    public static final String MDMS_CALCULATIONTYPE_PATH = "$.MdmsRes.BPA.CalculationType";

    public static final String MDMS_BPA_PATH = "$.MdmsRes.BPA";

    public static final String MDMS_BPA = "BPA";

    public static final String MDMS_CALCULATIONTYPE_FINANCIALYEAR= "financialYear";

    public static final String MDMS_CALCULATIONTYPE_FINANCIALYEAR_PATH = "$.MdmsRes.BPA.CalculationType[?(@.financialYear=='{}')]";

	public static final Object MDMS_CALCULATIONTYPE_SERVICETYPE = "serviceType";

	public static final Object MDMS_CALCULATIONTYPE_RISKTYPE = "riskType";

	public static final String MDMS_ROUNDOFF_TAXHEAD = "TL_ROUNDOFF";

	public static final String MDMS_CALCULATIONTYPE_AMOUNT = "amount";
	
	public static final String MDMS_CALCULATIONTYPE_APL_FEETYPE = "ApplicationFee";
	
	public static final String MDMS_CALCULATIONTYPE_SANC_FEETYPE = "SanctionFee";

	public static final String LOW_RISK_PERMIT_FEE_TYPE = "LOW_RISK_PERMIT_FEE";

	public static final String MDMS_CALCULATIONTYPE_LOW_SANC_FEETYPE = "Low_SanctionFee";

	public static final String MDMS_CALCULATIONTYPE_LOW_APL_FEETYPE = "Low_ApplicationFee";
	
	// Error messages in BPA Calculator
	
	public static final String PARSING_ERROR = "PARSING ERROR";
	
	public static final String INVALID_AMOUNT = "INVALID AMOUNT";
	
	public static final String INVALID_UPDATE = "INVALID UPDATE";
	
	public static final String INVALID_ERROR = "INVALID ERROR";
	
	public static final String INVALID_APPLICATION_NUMBER = "INVALID APPLICATION NUMBER";
	
	public static final String EDCR_ERROR = "EDCR_ERROR";
	
	public static final String CALCULATION_ERROR = "CALCULATION ERROR";
	
	public static final String TOTAL_BUILTUP_AREA = "TOTAL_BUILTUP_AREA";
	public static final String TOTAL_BUILTUP_AREA_PATH = "edcrDetail.*.planDetail.virtualBuilding.totalBuitUpArea";
	public static final String OCCUPANCY_TYPE = "occupancyType";
	public static final String OCCUPANCY_TYPE_PATH = "edcrDetail.*.planDetail.virtualBuilding.mostRestrictiveFarHelper.type.code";
	public static final String OCCUPANCY_TYPE_RESIDENTIAL_CODE = "A";
	public static final String SUB_OCCUPANCY_TYPE = "subOccupancyType";
	public static final String SUB_OCCUPANCY_TYPE_PATH = "edcrDetail.*.planDetail.virtualBuilding.mostRestrictiveFarHelper.subtype.code";
	public static final BigDecimal RESIDENTIAL_APPL_FEE_RATE_PER_SQ_MT = new BigDecimal("10");
	public static final String USAGE_CATEGORY_PATH = "edcrDetail.*.TODO";//TODO
	public static final String USAGE_CATEGORY = "USAGE_CATEGORY";
	public static final String SUB_USAGE_CATEGORY_PATH = "edcrDetail.*.TODO";//TODO
	public static final String SUB_USAGE_CATEGORY = "SUB_USAGE_CATEGORY";
	public static final BigDecimal APPL_FEE_DEFAULT_RATE_PER_SQ_MT = new BigDecimal("10");
	public static final BigDecimal NON_RESIDENTIAL_GH_UPTO_21_APPL_FEE_RATE_PER_SQ_MT = new BigDecimal("10");
	public static final BigDecimal NON_RESIDENTIAL_GH_ABOVE_21_APPL_FEE_RATE_PER_SQ_MT = new BigDecimal("10");
	public static final BigDecimal NON_RESIDENTIAL_COMMERICAL_APPL_FEE_RATE_PER_SQ_MT = new BigDecimal("10");
	public static final BigDecimal NON_RESIDENTIAL_INDUSTRIAL_APPL_FEE_RATE_PER_SQ_MT = new BigDecimal("10");
	public static final BigDecimal NON_RESIDENTIAL_INSTITUTIONAL_APPL_FEE_RATE_PER_SQ_MT = new BigDecimal("10");
	public static final String USAGE_CATEGORY_GH = "Group Housing";//TODO
	public static final String USAGE_CATEGORY_COMMERCIAL = "Commercial";//TODO
	public static final String USAGE_CATEGORY_INDUSTRIAL = "Industrial";//TODO
	public static final String USAGE_CATEGORY_INSTITUTIONAL = "Institutional";//TODO
	public static final String SUB_USAGE_CATEGORY_GH_UPTO_21 = "Group Housing Upto 21-mtr. height";//TODO
	public static final String SUB_USAGE_CATEGORY_GH_ABOVE_21 = "Group Housing more than 21-mtr. height";//TODO
	public static final String PARAM_MAP_BPA = "bpa";
	public static final String PARAM_MAP_REQUEST_INFO = "requestInfo";
	public static final String FEE_TYPE_LABOUR_CESS = "labourCess";
	public static final String TAXHEADCODE_LABOUR_CESS = "BPA_LABOUR_CESS";
	public static final BigDecimal SQ_MTR_TO_SQ_FEET_CONVERTER = new BigDecimal("10.76");
	public static final BigDecimal RESIDENTIAL_CONSTRUCTION_COST_RATE_PER_SQ_FEET = new BigDecimal("1447");
	public static final BigDecimal RESIDENTIAL_LABOUR_CESS_FACTOR_OF_CONSTRUCTION_COST = new BigDecimal("0.01");
	public static final BigDecimal LABOUR_CESS_MINIMUM_AREA = new BigDecimal("64.50");
	public static final BigDecimal LABOUR_CESS__MIN_CONSTRUCTION_COST = new BigDecimal("1000000");
	public static final String PLOT_SIZE = "plotSize";
	public static final String EXISTING_FAR = "existingFar";
	public static final String PROVIDED_FAR_PATH = "edcrDetail.*.planDetail.farDetails.providedFar";
	public static final String ZONE = "zone";
	public static final String FEE_TYPE_PURCHASABLE_FAR = "purchasableFar";
	public static final String TAXHEADCODE_PURCHASABLE_FAR = "BPA_PURCHASABLE_FAR";
	public static final String DISTRICT_NAME_PATH = "edcrDetail.*.TODO";//TODO
	public static final String DISTRICT_NAME = "districtName";
	public static final String DEVELOPMENT_PLAN_PATH = "edcrDetail.*.TODO";//TODO
	public static final String DEVELOPMENT_PLAN = "developmentPlan";
	public static final String EDC_INDEX = "index";
	public static final String PLOT_AREA = "plotArea";
	public static final String PLOT_AREA_PATH = "edcrDetail.*.planDetail.plot.area";
	public static final BigDecimal BASE_EDC_AMOUNT_RUPEES_PER_ACRE = new BigDecimal("10409600");
	public static final BigDecimal PER_ACRE_TO_PER_SQM_DIVIDE_BY = new BigDecimal("4046.86");
	public static final BigDecimal EDC_APPLICABLE_AREA_FACTOR_DIVIDE = new BigDecimal("0.55");
	public static final BigDecimal EDC_APPLICABLE_ONE_THIRD_FACTOR = new BigDecimal("0.33");
	public static final String MDMS_PURCHASABLE_FAR = "PurchasableFar";
	public static final String MDMS_EDC_RATES = "EDCRates";
	public static final String FEE_TYPE_EDC = "edcFee";
	public static final String TAXHEADCODE_EDC = "BPA_EDC_FEES";
	public static final String TAXHEADCODE_APPLICATION_FEE = "BPA_APPL_FEES";
	public static final String MDMS_LABOUR_CESS_RATES = "LabourCessRates";
	
}
