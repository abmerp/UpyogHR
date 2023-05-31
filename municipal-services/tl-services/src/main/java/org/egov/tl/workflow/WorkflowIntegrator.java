package org.egov.tl.workflow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.tl.abm.newservices.entity.NewBankGuarantee;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.service.BankGuaranteeService;
import org.egov.tl.util.TLConstants;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import static org.egov.tl.util.TLConstants.*;
@Service
@Slf4j
public class WorkflowIntegrator {
	



	private RestTemplate rest;

	private TLConfiguration config;

	@Value("${workflow.bpa.businessServiceCode.fallback_enabled}")
	private Boolean pickWFServiceNameFromTradeTypeOnly;

	@Autowired
	public WorkflowIntegrator(RestTemplate rest, TLConfiguration config) {
		this.rest = rest;
		this.config = config;
	}

	/**
	 * Method to integrate with workflow
	 *
	 * takes the trade-license request as parameter constructs the work-flow request
	 *
	 * and sets the resultant status from wf-response back to trade-license object
	 *
	 * @param tradeLicenseRequest
	 */
	public void callWorkFlow(TradeLicenseRequest tradeLicenseRequest) {
		TradeLicense currentLicense = tradeLicenseRequest.getLicenses().get(0);
		String wfTenantId = currentLicense.getTenantId();
		String businessServiceFromMDMS = tradeLicenseRequest.getLicenses().isEmpty()?null:currentLicense.getBusinessService();
		if (businessServiceFromMDMS == null)
			businessServiceFromMDMS = businessService_TL;
		JSONArray array = new JSONArray();
		for (TradeLicense license : tradeLicenseRequest.getLicenses()) {
			if((businessServiceFromMDMS.equals(businessService_TL))||(!license.getAction().equalsIgnoreCase(TRIGGER_NOWORKFLOW))) {
				JSONObject obj = new JSONObject();
				List<Map<String, String>> uuidmaps = new LinkedList<>();
				if(!CollectionUtils.isEmpty(license.getAssignee())){

					// Adding assignes to processInstance
					license.getAssignee().forEach(assignee -> {
						Map<String, String> uuidMap = new HashMap<>();
						uuidMap.put(UUIDKEY, assignee);
						uuidmaps.add(uuidMap);
					});
				}
				obj.put(BUSINESSIDKEY, license.getApplicationNumber());
				obj.put(TENANTIDKEY, wfTenantId);
				
				switch(businessServiceFromMDMS)
				{
				case ZONE_PLAN:
					obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
					obj.put(MODULENAMEKEY, TLMODULENAMEVALUE);
				break;
				case EXTENTION_OF_CLU_PERMISSION:
					obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
					obj.put(MODULENAMEKEY, TLMODULENAMEVALUE);
				break;
				case SURRENDER_OF_LICENSE:
					obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
					obj.put(MODULENAMEKEY, TLMODULENAMEVALUE);
				break;
				//TLR Changes
					case COMPOSITION_OF_URBAN_WORKFLOWCODE:
						obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
						obj.put(MODULENAMEKEY, TLMODULENAMEVALUE);
					break;
					case CONSTRUCTION_OF_COMMUNITY_WORKFLOWCODE:
						obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
						obj.put(MODULENAMEKEY, TLMODULENAMEVALUE);
					break;
				    case COMPLETION_CERTIFICATE_WORKFLOWCODE:
						obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
						obj.put(MODULENAMEKEY, TLMODULENAMEVALUE);
					break;
				    case CHANGE_BENEFICIAL_WORKFLOWCODE:
						obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
						obj.put(MODULENAMEKEY, TLMODULENAMEVALUE);
					break;
					
					case businessService_TL:
						obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
						obj.put(MODULENAMEKEY, TLMODULENAMEVALUE);
						break;
						
					case businessService_TRANSFER:
						obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
						obj.put(MODULENAMEKEY, TLMODULENAMEVALUE);
						break;
						
					case businessService_Revised:
						obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
						obj.put(MODULENAMEKEY, TLMODULENAMEVALUE);
						break;
						
					case ASNAMEVALUE:
						obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
						obj.put(MODULENAMEKEY, ASNAMEVALUE);
						break;
					case SPNAMEVALUE:
						obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
						obj.put(MODULENAMEKEY, SPNAMEVALUE);
						break;
						
					case SPNAMEVALUE_DEMARCATION:
							obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
							obj.put(MODULENAMEKEY, SPNAMEVALUE_DEMARCATION);
							break;
						
					case EPNAMEVALUE:
						obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
						obj.put(MODULENAMEKEY, EPNAMEVALUE);
						break;

					case businessService_BPA:
						String tradeType = tradeLicenseRequest.getLicenses().get(0).getTradeLicenseDetail().getTradeType();
						if(pickWFServiceNameFromTradeTypeOnly)
						{
							tradeType=tradeType.split("\\.")[0];
						}
						obj.put(BUSINESSSERVICEKEY, tradeType);
						obj.put(MODULENAMEKEY, BPAMODULENAMEVALUE);
						break;
				
					case BankGuaranteeService.BUSINESSSERVICE_BG_NEW:
					case BUSINESSSERVICE_BG_RELEASE:
					case BankGuaranteeService.BUSINESSSERVICE_BG_MORTGAGE:
						obj.put(BUSINESSSERVICEKEY, currentLicense.getWorkflowCode());
						obj.put(MODULENAMEKEY, TLMODULENAMEVALUE);
						break;
				}
				obj.put(ACTIONKEY, license.getAction());
				obj.put(COMMENTKEY, license.getComment());
				if (!CollectionUtils.isEmpty(license.getAssignee()))
					obj.put(ASSIGNEEKEY, uuidmaps);
				obj.put(DOCUMENTSKEY, license.getWfDocuments());
				array.add(obj);
			}
		}
		if(!array.isEmpty())
		{
			JSONObject workFlowRequest = new JSONObject();
			workFlowRequest.put(REQUESTINFOKEY, tradeLicenseRequest.getRequestInfo());
			workFlowRequest.put(WORKFLOWREQUESTARRAYKEY, array);
			
			String response = null;
			try {
				response = rest.postForObject(config.getWfHost().concat(config.getWfTransitionPath()), workFlowRequest, String.class);
			} catch (HttpClientErrorException e) {

				/*
				 * extracting message from client error exception
				 */
				DocumentContext responseContext = JsonPath.parse(e.getResponseBodyAsString());
				List<Object> errros = null;
				try {
					errros = responseContext.read("$.Errors");
				} catch (PathNotFoundException pnfe) {
					log.error("EG_TL_WF_ERROR_KEY_NOT_FOUND",
							" Unable to read the json path in error object : " + pnfe.getMessage());
					throw new CustomException("EG_TL_WF_ERROR_KEY_NOT_FOUND",
							" Unable to read the json path in error object : " + pnfe.getMessage());
				}
				throw new CustomException("EG_WF_ERROR", errros.toString());
			} catch (Exception e) {
				throw new CustomException("EG_WF_ERROR",
						" Exception occured while integrating with workflow : " + e.getMessage());
			}

			/*
			 * on success result from work-flow read the data and set the status back to TL
			 * object
			 */
			DocumentContext responseContext = JsonPath.parse(response);
			List<Map<String, Object>> responseArray = responseContext.read(PROCESSINSTANCESJOSNKEY);
			Map<String, String> idStatusMap = new HashMap<>();
			responseArray.forEach(
					object -> {

						DocumentContext instanceContext = JsonPath.parse(object);
						idStatusMap.put(instanceContext.read(BUSINESSIDJOSNKEY), instanceContext.read(STATUSJSONKEY));
					});

			// setting the status back to TL object from wf response
			tradeLicenseRequest.getLicenses()
					.forEach(tlObj -> tlObj.setStatus(idStatusMap.get(tlObj.getApplicationNumber())));
		}
	}
}