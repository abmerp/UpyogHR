package org.egov.lndcalculator.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;

import org.egov.lndcalculator.config.TLCalculatorConfigs;
import org.egov.lndcalculator.repository.CalculationRepository;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

@Component
public class LandUtil {

	@Autowired
	TLCalculatorConfigs config;
	
	@Autowired
	CalculationRepository calculationRepository;
	public static final String COMMON_MASTERS_MODULE = "common-masters";
	public static final String COMMON_MASTER_JSONPATH_CODE = "$.MdmsRes.common-masters";

	public StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsSearchEndpoint());
	}

	

	public List<ModuleDetail> getPurposeCodeRequest(String purposeCode) {

		final String filterCode = "$.[?(@.purposeCode=='" + purposeCode + "')]";
		List<MasterDetail> commonMaster = new ArrayList<>();
		commonMaster.add(MasterDetail.builder().name("Purpose").filter(filterCode).build());

		ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMaster)
				.moduleName(COMMON_MASTER_JSONPATH_CODE).build();

		return Arrays.asList(commonMasterMDtl);

	}

	public MdmsCriteriaReq getMDMSRequestPurposeCode(RequestInfo requestInfo, String tenantId, String purposeCode) {

		List<ModuleDetail> moduleRequest = getPurposeCodeRequest(purposeCode);
		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.addAll(moduleRequest);

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo)
				.build();
		return mdmsCriteriaReq;
	}

	public Object mDMSCallPurposeCode(RequestInfo requestInfo, String tenantId, String purposeCode) {
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequestPurposeCode(requestInfo, tenantId, purposeCode);
		Object result = calculationRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
		return result;
	}

	public void defaultJsonPathConfig() {
		Configuration.setDefaults(new Configuration.Defaults() {

			private final JsonProvider jsonProvider = new JacksonJsonProvider();
			private final MappingProvider mappingProvider = new JacksonMappingProvider();

			@Override
			public JsonProvider jsonProvider() {
				return jsonProvider;
			}

			@Override
			public MappingProvider mappingProvider() {
				return mappingProvider;
			}

			@Override
			public Set<Option> options() {
				return EnumSet.noneOf(Option.class);
			}
		});

	}
	
	
	
	public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenentID";
	public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";
	public Map<String, List<String>> getAttributeValues(Object mdmsData) {

		List<String> modulepaths = Arrays.asList(COMMON_MASTER_JSONPATH_CODE);
		final Map<String, List<String>> mdmsResMap = new HashMap<>();
		modulepaths.forEach(modulepath -> {
			try {
				mdmsResMap.putAll(JsonPath.read(mdmsData, modulepath));
			} catch (Exception e) {
				throw new CustomException(INVALID_TENANT_ID_MDMS_KEY,
						INVALID_TENANT_ID_MDMS_MSG);
			}
		});
		return mdmsResMap;
	}
}
