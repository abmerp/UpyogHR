package org.egov.tl.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;


import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

@Component
public class LandUtil {

	@Autowired
	private TLConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	private static final String COMMON_MASTERS_MODULE = "common-masters";

	/**
	 * Returns the URL for MDMS search end point
	 *
	 * @return URL for MDMS search end point
	 */
	public StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());
	}

	public List<ModuleDetail> getPurposeCodeRequest(String purposeCode) {

		final String filterCode = "$.[?(@.purposeCode=='" + purposeCode + "')]";
		List<MasterDetail> commonMaster = new ArrayList<>();
		commonMaster.add(MasterDetail.builder().name("Purpose").filter(filterCode).build());

		ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMaster)
				.moduleName(COMMON_MASTERS_MODULE).build();

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
		Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
		return result;
	}
	public Object mDMSCallDistrictCode(RequestInfo requestInfo, String tenantId, String districtCode) {
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequestDistrictCode(requestInfo, tenantId, districtCode);
		Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
		return result;
	}
	public MdmsCriteriaReq getMDMSRequestDistrictCode(RequestInfo requestInfo, String tenantId, String districtCode) {

		List<ModuleDetail> moduleRequest = getDistrictCodeRequest(districtCode);
		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.addAll(moduleRequest);

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo)
				.build();
		return mdmsCriteriaReq;
	}
	public List<ModuleDetail> getDistrictCodeRequest(String districtCode) {

		final String filterCode = "$.[?(@.disCode=='" + districtCode + "')]";
		List<MasterDetail> commonMaster = new ArrayList<>();
		commonMaster.add(MasterDetail.builder().name("District").filter(filterCode).build());

		ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMaster)
				.moduleName(COMMON_MASTERS_MODULE).build();

		return Arrays.asList(commonMasterMDtl);

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
}
