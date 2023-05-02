package org.egov.tl.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.repo.CompletionCertificateRepo;
import org.egov.tl.abm.repo.RealeseBankGurenteeRepo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.service.repo.LicenseServiceRepo;
import org.egov.tl.util.LandUtil;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.validator.LandMDMSValidator;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.CompletionCertificate;
import org.egov.tl.web.models.CompletionCertificateRequest;
import org.egov.tl.web.models.CompletionCertificateResponse;
import org.egov.tl.web.models.RealeseBankGurenteeRequest;
import org.egov.tl.web.models.RealeseBankGurenteeResponse;
import org.egov.tl.web.models.ReleaseBankGuarantee;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.Transfer;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;

@Service
@Log4j2
public class RealeseBankGurenteeService {

	
	@Autowired
	private RealeseBankGurenteeRepo realeseBankGurenteeRepo;
	
	public RealeseBankGurenteeResponse createReleaseBankGuarantee(RealeseBankGurenteeRequest realeseBankGurenteeRequest) {
		RealeseBankGurenteeResponse realeseBankGurenteeResponse = null;
		String bankGuaranteeNumber = null;
		try {
		  bankGuaranteeNumber = realeseBankGurenteeRequest.getReleaseBankGuarantee().get(0).getBankGuaranteeNumber();
		}catch (Exception e) {}
		
		ReleaseBankGuarantee releaseBankGuarantee=realeseBankGurenteeRepo.getReleaseBankGuaranteeByBGNo(bankGuaranteeNumber);
		if(releaseBankGuarantee!=null) {
			realeseBankGurenteeResponse=createRealeseBankGurentee(realeseBankGurenteeRequest, releaseBankGuarantee, false);
		}else {
			realeseBankGurenteeResponse=createRealeseBankGurentee(realeseBankGurenteeRequest, releaseBankGuarantee, true);
	 	}
		return realeseBankGurenteeResponse;
	}

	private RealeseBankGurenteeResponse createRealeseBankGurentee(RealeseBankGurenteeRequest realeseBankGurenteeRequest, ReleaseBankGuarantee releaseBankGuaranteedata,boolean isCreate) {
		RealeseBankGurenteeResponse realeseBankGurenteeResponse = null;
		List<ReleaseBankGuarantee> releaseBankGuarantee = (List<ReleaseBankGuarantee>) realeseBankGurenteeRequest
				.getReleaseBankGuarantee().stream().map(realese -> {
					Long time = System.currentTimeMillis();
					AuditDetails auditDetails = null;
					
					if (isCreate) {

						auditDetails = AuditDetails.builder()
								.createdBy(realeseBankGurenteeRequest.getRequestInfo().getUserInfo().getUuid())
								.createdTime(time).build();
						realese.setId(UUID.randomUUID().toString());
						realese.setApplicationStatus(1);
					} else {
						auditDetails = releaseBankGuaranteedata.getAuditDetails();
						auditDetails.setLastModifiedBy(realeseBankGurenteeRequest.getRequestInfo().getUserInfo().getUuid());
						auditDetails.setLastModifiedTime(time);
					}
					realese.setAuditDetails(auditDetails);

				
					return realese;
				}).collect(Collectors.toList());
		realeseBankGurenteeRequest.setReleaseBankGuarantee(releaseBankGuarantee);
		if (isCreate) {
			realeseBankGurenteeRepo.save(realeseBankGurenteeRequest);
			realeseBankGurenteeResponse = RealeseBankGurenteeResponse.builder()
					.releaseBankGuarantee(releaseBankGuarantee)
					.requestInfo(realeseBankGurenteeRequest.getRequestInfo())
					.message("Records has been inserted successfully.").status(true).build();
		} else {
			realeseBankGurenteeRepo.update(realeseBankGurenteeRequest);
			realeseBankGurenteeResponse = RealeseBankGurenteeResponse.builder()
					.releaseBankGuarantee(releaseBankGuarantee)
					.requestInfo(realeseBankGurenteeRequest.getRequestInfo())
					.message("Records has been updated successfully.").status(true).build();
		}
		return realeseBankGurenteeResponse;
	}
	
	public RealeseBankGurenteeResponse getRealeseBankGurentee(String bgNumber,RequestInfoWrapper requestInfoWrapper) {
		RealeseBankGurenteeResponse realeseBankGurenteeResponse = null;
		
		ReleaseBankGuarantee realeseBankGurentee=realeseBankGurenteeRepo.getReleaseBankGuaranteeByBGNo(bgNumber);
		realeseBankGurenteeResponse = RealeseBankGurenteeResponse.builder()
				.releaseBankGuarantee(realeseBankGurentee!=null?Arrays.asList(realeseBankGurentee):Arrays.asList(new ReleaseBankGuarantee()))
				.requestInfo(requestInfoWrapper.getRequestInfo())
				.message("Records has been fetch successfully.").status(true).build();

		return realeseBankGurenteeResponse;
	}
	
	public List<String> getDropDownList(int type, RequestInfo requestInfo) {
		List<Map<String,Object>> dropList=realeseBankGurenteeRepo.getDropDownList();
		List<String> dropDoneList=new ArrayList<>();
		if(type==1) {
			dropDoneList=dropList.stream().filter(bg->bg.get("application_number")!=null&&!bg.get("application_number").equals("")).map(bg->bg.get("application_number").toString()).collect(Collectors.toList());
		}else if(type==2){
			dropDoneList=dropList.stream().filter(bg->bg.get("loi_number")!=null&&!bg.get("loi_number").equals("")).map(bg->bg.get("loi_number").toString()).collect(Collectors.toList());
		}else {
			dropDoneList=dropList.stream().filter(bg->bg.get("licence_number")!=null&&!bg.get("licence_number").equals("")).map(bg->bg.get("licence_number").toString()).collect(Collectors.toList());
		}
		
		return dropDoneList;
		
	}
	

}

