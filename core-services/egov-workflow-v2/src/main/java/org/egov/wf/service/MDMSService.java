package org.egov.wf.service;

import com.jayway.jsonpath.JsonPath;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.wf.config.WorkflowConfig;
import org.egov.wf.repository.ServiceRequestRepository;
import org.egov.wf.util.WorkflowConstants;
import org.egov.wf.web.models.ProcessInstanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.egov.wf.util.WorkflowConstants.*;

@Service
public class MDMSService {

   private WorkflowConfig config;

   private ServiceRequestRepository serviceRequestRepository;

   private WorkflowConfig workflowConfig;

   private Map<String,Boolean> stateLevelMapping;

    @Autowired
    public MDMSService(WorkflowConfig config, ServiceRequestRepository serviceRequestRepository, WorkflowConfig workflowConfig) {
        this.config = config;
        this.serviceRequestRepository = serviceRequestRepository;
        this.workflowConfig = workflowConfig;
    }


    public Map<String, Boolean> getStateLevelMapping(RequestInfo requestinfo) {
    	stateLevelMapping(requestinfo);
    	return this.stateLevelMapping;
    }


   
    public void stateLevelMapping(RequestInfo requestinfo){
        Map<String, Boolean> stateLevelMapping = new HashMap<>();

        Object mdmsData = getBusinessServiceMDMS(requestinfo);
        List<HashMap<String, Object>> configs = JsonPath.read(mdmsData,JSONPATH_BUSINESSSERVICE_STATELEVEL);


        for (Map map : configs){

            String businessService = (String) map.get("businessService");
            Boolean isStatelevel = Boolean.valueOf((String) map.get("isStatelevel"));

            stateLevelMapping.put(businessService, isStatelevel);
        }

        this.stateLevelMapping = stateLevelMapping;
    }


    /**
     * Calls MDMS service to fetch master data
     * @param requestInfo
     * @return
     */
    public Object mDMSCall(RequestInfo requestInfo){
        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo,workflowConfig.getStateLevelTenantId());
        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
        return result;
    }

    /**
     * Calls MDMS service to fetch master data
     * @return
     */
    public Object getBusinessServiceMDMS(RequestInfo requestinfo){
        MdmsCriteriaReq mdmsCriteriaReq = getBusinessServiceMDMSRequest(requestinfo, workflowConfig.getStateLevelTenantId());
        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
        return result;
    }


    /**
     * Creates MDMSCriteria
     * @param requestInfo The RequestInfo of the request
     * @param tenantId TenantId of the request
     * @return MDMSCriteria for search call
     */
    private MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId){
        ModuleDetail escalationDetail = getAutoEscalationConfig();
        ModuleDetail tenantDetail = getTenants();

        List<ModuleDetail> moduleDetails = new LinkedList<>(Arrays.asList(escalationDetail,tenantDetail));

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails)
                .tenantId(tenantId)
                .build();

        MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
                .requestInfo(requestInfo).build();
        return mdmsCriteriaReq;
    }

    /**
     * Creates MDMSCriteria
     * @param requestInfo The RequestInfo of the request
     * @param tenantId TenantId of the request
     * @return MDMSCriteria for search call
     */
    private MdmsCriteriaReq getBusinessServiceMDMSRequest(RequestInfo requestInfo, String tenantId){
        ModuleDetail wfMasterDetails = getBusinessServiceMasterConfig();


        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Collections.singletonList(wfMasterDetails))
                .tenantId(tenantId)
                .build();

        MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
                .requestInfo(requestInfo).build();
        return mdmsCriteriaReq;
    }


    /**
     * Fetches BusinessServiceMasterConfig from MDMS
     * @return ModuleDetail for workflow
     */
    private ModuleDetail getBusinessServiceMasterConfig() {

        // master details for WF module
        List<MasterDetail> wfMasterDetails = new ArrayList<>();

        wfMasterDetails.add(MasterDetail.builder().name(MDMS_BUSINESSSERVICE).build());

        ModuleDetail wfModuleDtls = ModuleDetail.builder().masterDetails(wfMasterDetails)
                .moduleName(MDMS_WORKFLOW).build();

        return wfModuleDtls;
    }

    /**
     * Creates MDMS ModuleDetail object for AutoEscalation
     * @return ModuleDetail for AutoEscalation
     */
    private ModuleDetail getAutoEscalationConfig() {

        // master details for WF module
        List<MasterDetail> masterDetails = new ArrayList<>();

        masterDetails.add(MasterDetail.builder().name(MDMS_AUTOESCALTION).build());

        ModuleDetail wfModuleDtls = ModuleDetail.builder().masterDetails(masterDetails)
                .moduleName(MDMS_WORKFLOW).build();

        return wfModuleDtls;
    }

    /**
     * Creates MDMS ModuleDetail object for tenants
     * @return ModuleDetail for tenants
     */
    private ModuleDetail getTenants() {

        // master details for WF module
        List<MasterDetail> masterDetails = new ArrayList<>();

        masterDetails.add(MasterDetail.builder().name(MDMS_TENANTS).build());

        ModuleDetail wfModuleDtls = ModuleDetail.builder().masterDetails(masterDetails)
                .moduleName(MDMS_MODULE_TENANT).build();

        return wfModuleDtls;
    }





    /**
     * Returns the url for mdms search endpoint
     * @return url for mdms search endpoint
     */
    public StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());
    }







}
