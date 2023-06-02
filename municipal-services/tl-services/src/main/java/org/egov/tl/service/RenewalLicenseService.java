package org.egov.tl.service;

import static org.egov.tl.util.TLConstants.businessService_TL;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.repo.RenewalLicenseServiceRepo;
import org.egov.tl.util.ConvertUtil;
import org.egov.tl.util.TLConstants;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.RenewalLicense;
import org.egov.tl.web.models.RenewalLicenseAddetionalDetails;
import org.egov.tl.web.models.RenewalLicense.ApplicationTypeEnum;
import org.egov.tl.web.models.RenewalLicense.LicenseTypeEnum;
import org.egov.tl.web.models.RenewalLicenseDetail;
import org.egov.tl.web.models.RenewalLicensePreviopusCondition;
import org.javers.common.collections.Arrays;
import org.egov.tl.web.models.RenewalLicenseRequest;
import org.egov.tl.web.models.RenewalLicenseRequestDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RenewalLicenseService {
	
	@Autowired
	ObjectMapper mapper;
	@Value("${tcp.employee.ctp}")
	private String ctpUser;
	@Value("${egov.timeZoneName}")
	private String timeZoneName;
	
	@Autowired
	RenewalLicenseServiceRepo renewalLicenseServiceRepo;
	
	@Autowired
	private ServicePlanService servicePlanService;
	
	private TradeUtil util;
	
	
	public List<RenewalLicenseRequestDetail> saveRenewalLicense(RenewalLicenseRequest renewalLicenseRequest) {
		List<RenewalLicenseRequestDetail> requestData=getRenewalLicenseData(renewalLicenseRequest).get(0).getRenewalLicenseRequestDetail();
//		validateRenewalLicense(requestData);
		renewalLicenseRequest.setRenewalLicenseRequestDetail(requestData);
//		
//		
//		RenewalLicenseAddetionalDetails renewalLicenseAddetionalDetails=renewalLicenseRequest.getRenewalLicenseRequestDetail().get(0).getRenewalLicenseDetail().get(0).getAdditionalDetail();
//		String additionaldetail=null;
//		try {
//			additionaldetail = mapper.writeValueAsString(renewalLicenseAddetionalDetails);
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		log.info("additionaldetail:---------------------"+additionaldetail);
//		
//		renewalLicenseServiceRepo.saveRenewalLicense(renewalLicenseRequest);
		return requestData;
	}
	
	private void validateRenewalLicense(List<RenewalLicenseRequestDetail> renewalLicenseRequestDetails) {
		try {
			for(RenewalLicenseRequestDetail renewalLicenseRequestDetail:renewalLicenseRequestDetails) {
				for(RenewalLicense renewalLicense:renewalLicenseRequestDetail.getRenewalLicense()) {
					for(PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(renewalLicense.getClass()).getPropertyDescriptors()){
						System.out.println("RenewalLicense:--"+propertyDescriptor.getReadMethod());
					}				
				}
	
				for(RenewalLicenseDetail renewalLicenseDetail:renewalLicenseRequestDetail.getRenewalLicenseDetail()) {
					for(PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(renewalLicenseDetail.getClass()).getPropertyDescriptors()){
						System.out.println("RenewalLicenseDetail:--"+propertyDescriptor.getName());
						
					}
				}
			}
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
//	public List<RenewalLicense> getRenewalLicense(String applicationNumber) {
//		List<RenewalLicense> renewalLicense = null;
//		try {
//			List<RenewalLicense> renewalLicenseList = null;// renewalLicenseServiceRepo.getRenewalLicense(applicationNumber);
//			if(renewalLicenseList!=null&&!renewalLicenseList.isEmpty()) {
//				renewalLicense=renewalLicenseList;
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return renewalLicense;
//	}
	
	public List<RenewalLicenseRequest> getRenewalLicenseData(RenewalLicenseRequest renewalLicenseRequest){
		
//		Object mdmsData = util.mDMSCall(renewalLicenseRequest.getRequestInfo(), "hr");
//		String jsonPath = TLConstants.MDMS_CURRENT_FINANCIAL_YEAR.replace("{}", businessService_TL);
//		List<Map<String, Object>> jsonOutput = JsonPath.read(mdmsData, jsonPath);
		
		RequestInfo requestInfo=renewalLicenseRequest.getRequestInfo();
		Timestamp currentDate=Timestamp.valueOf(ConvertUtil.getCurrentFullDate(timeZoneName, null));
		List<String> assignees=java.util.Arrays.asList(servicePlanService.assignee(ctpUser, "hr", true, renewalLicenseRequest.getRequestInfo()));
		String renewalLicenceId=UUID.randomUUID().toString();
		long currentTime=currentDate.getTime();
		AuditDetails auditDetails=new AuditDetails();
		auditDetails.setCreatedBy(requestInfo.getUserInfo().getUuid());
		auditDetails.setCreatedTime(currentTime);
		
		List<RenewalLicenseDetail> renewalLicenseDetails=renewalLicenseRequest.getRenewalLicenseRequestDetail().get(0).getRenewalLicenseDetail();
		renewalLicenseDetails=renewalLicenseDetails.stream().map(renewalLicenseDetail->{
			renewalLicenseDetail.setAuditDetails(auditDetails);
			renewalLicenseDetail.setId(UUID.randomUUID().toString());
			renewalLicenseDetail.setRenewllicenseId(renewalLicenceId);
			renewalLicenseDetail.setCurrentVersion(renewalLicenseRequest.getCurrentVersion());
			renewalLicenseDetail.setRenewalType("PERMANENT");
			renewalLicenseDetail.setPenaltyPayable("0.0");
			renewalLicenseDetail.setRenewalAmount("0.0");
			renewalLicenseDetail.setAdditionalDetail(renewalLicenseRequest.getRenewalLicenseRequestDetail().get(0).getRenewalLicenseDetail().get(0).getAdditionalDetail());
			return renewalLicenseDetail;
		}).collect(Collectors.toList());
		
		
		List<RenewalLicense> renewalLicense = renewalLicenseRequest.getRenewalLicenseRequestDetail().get(0).getRenewalLicense().stream().map(renewallicense->{
			renewallicense.setAuditDetails(auditDetails);;
			renewallicense.setApplicationDate(currentDate.getTime());
			renewallicense.setAssignee(assignees);
			renewallicense.setApplicationType(ApplicationTypeEnum.RENEWAL);
			renewallicense.setLicenseType(LicenseTypeEnum.PERMANENT);
			renewallicense.setWorkflowCode("TCPRL");
			renewallicense.setFinancialYear("2022-23");
			
			renewallicense.setAction("INITIATE");
			renewallicense.setBusinessService("TL");
			renewallicense.setTenantId("hr");
			renewallicense.setId(renewalLicenceId);
			renewallicense.setVer(renewalLicenseRequest.getCurrentVersion());
//			renewallicense.setRenewalLicenseDetail(renewalLicenseDetails);
			return renewallicense;
		}).collect(Collectors.toList());
		renewalLicenseRequest.getRenewalLicenseRequestDetail().get(0).setRenewalLicense(renewalLicense);
		renewalLicenseRequest.getRenewalLicenseRequestDetail().get(0).setRenewalLicenseDetail(renewalLicenseDetails);
		
		List<RenewalLicenseRequest> arrayList=new ArrayList<>();
		arrayList.add(renewalLicenseRequest);
		
		return arrayList;
	}

}
