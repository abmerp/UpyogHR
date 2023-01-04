package org.egov.tl.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.egov.tl.abm.newservices.contract.NewBankGuaranteeContract;
import org.egov.tl.abm.newservices.entity.NewBankGuarantee;
import org.egov.tl.abm.repo.NewBankGuaranteeRepo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BankGuaranteeService {

	@Autowired
	private NewBankGuaranteeRepo newBankGuaranteeRepo;
	@Autowired
	private TradeUtil tradeUtil;
	@Autowired
	private Producer producer;
	@Autowired
	private TLConfiguration tlConfiguration;
	@Autowired
	private EnrichmentService enrichmentService;
	@Autowired
	private WorkflowIntegrator workflowIntegrator;
	
	public static final String BUSINESSSERVICE_BG_NEW = "BG_NEW";
	
	//@Autowired RenewBankGuaranteeRepo renewBankGuaranteeRepo;	
	//@Autowired ReleaseBankGuaranteeRepo releaseBankGuaranteeRepo;
	//@Autowired ReplaceBankGuaranteeRepo replaceBankGuaranteeRepo;

	public NewBankGuarantee createAndUpdate(NewBankGuaranteeContract newBankGuaranteeContract) {

		// populate audit details-
		AuditDetails auditDetails = tradeUtil
				.getAuditDetails(newBankGuaranteeContract.getRequestInfo().getUserInfo().getUuid(), true);
		newBankGuaranteeContract.getNewBankGuaranteeRequest().setAuditDetails(auditDetails);

		//populate applicationNumber from idgen-
		List<String> idGenIds = enrichmentService.getIdList(newBankGuaranteeContract.getRequestInfo(),
				newBankGuaranteeContract.getNewBankGuaranteeRequest().getTenantId(),
				tlConfiguration.getNewBankGuaranteeApplNoIdGenName(),
				tlConfiguration.getNewBankGuaranteeApplNoIdGenFormat(), 1);
		String applicationNo = idGenIds.get(0);
		newBankGuaranteeContract.getNewBankGuaranteeRequest().setApplicationNumber(applicationNo);
		//set INITIATED status as not expected from UI-
		newBankGuaranteeContract.getNewBankGuaranteeRequest().setStatus("INITIATED");
		newBankGuaranteeContract.getNewBankGuaranteeRequest().setId(UUID.randomUUID().toString());
		validateValidityFormat(newBankGuaranteeContract);
		NewBankGuarantee newBankGuaranteeEntity = newBankGuaranteeContract.getNewBankGuaranteeRequest().toBuilder();
		
		//call workflow to insert processinstance-
		TradeLicenseRequest processInstanceRequest = prepareProcessInstanceRequest(newBankGuaranteeContract);
		workflowIntegrator.callWorkFlow(processInstanceRequest);
		
		producer.push(tlConfiguration.getSaveNewBankGuaranteeTopic(), newBankGuaranteeContract);
		
		return newBankGuaranteeEntity;
		//return newBankGuaranteeRepo.save(newBankGuaranteeContract.getNewBankGuaranteeRequest().toBuilder());
		 
		/*
		 * boolean exists = newBankGuaranteeRepo
		 * .existsByLoiNumber(newBankGuaranteeContract.getNewBankGuaranteeRequest().
		 * getLoiNumber()); if (!exists) { return
		 * newBankGuaranteeRepo.save(newBankGuaranteeContract.getNewBankGuaranteeRequest
		 * ().toBuilder()); } else { NewBankGuarantee newBankGuarantee =
		 * newBankGuaranteeRepo
		 * .findById(newBankGuaranteeContract.getNewBankGuaranteeRequest().getId()).get(
		 * ); newBankGuarantee.setAmountInFig(newBankGuaranteeContract.
		 * getNewBankGuaranteeRequest().getAmountInFig());
		 * newBankGuarantee.setAmountInWords(newBankGuaranteeContract.
		 * getNewBankGuaranteeRequest().getAmountInWords());
		 * newBankGuarantee.setBankName(newBankGuaranteeContract.
		 * getNewBankGuaranteeRequest().getBankName());
		 * newBankGuarantee.setLoiNumber(newBankGuaranteeContract.
		 * getNewBankGuaranteeRequest().getLoiNumber());
		 * newBankGuarantee.setMemoNumber(newBankGuaranteeContract.
		 * getNewBankGuaranteeRequest().getMemoNumber());
		 * newBankGuarantee.setTypeOfBg(newBankGuaranteeContract.
		 * getNewBankGuaranteeRequest().getTypeOfBg());
		 * newBankGuarantee.setValidity(newBankGuaranteeContract.
		 * getNewBankGuaranteeRequest().getValidity());
		 * newBankGuarantee.setUploadBg(newBankGuaranteeContract.
		 * getNewBankGuaranteeRequest().getUploadBg());
		 * newBankGuarantee.setConsentLetter(newBankGuaranteeContract.
		 * getNewBankGuaranteeRequest().getConsentLetter());
		 * newBankGuarantee.setLicenseApplied(newBankGuaranteeContract.
		 * getNewBankGuaranteeRequest().getLicenseApplied()); return
		 * newBankGuaranteeRepo.save(newBankGuarantee); }
		 */

	}
	
	private TradeLicenseRequest prepareProcessInstanceRequest(NewBankGuaranteeContract newBankGuaranteeContract) {
		TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();
		List<TradeLicense> licenses = new ArrayList<>();
		TradeLicense tradeLicense = new TradeLicense();

		tradeLicense.setBusinessService(BUSINESSSERVICE_BG_NEW);
		tradeLicense.setAction(newBankGuaranteeContract.getNewBankGuaranteeRequest().getWorkflowAction());
		tradeLicense.setAssignee(newBankGuaranteeContract.getNewBankGuaranteeRequest().getWorkflowAssignee());
		tradeLicense.setApplicationNumber(newBankGuaranteeContract.getNewBankGuaranteeRequest().getApplicationNumber());
		tradeLicense.setWorkflowCode(BUSINESSSERVICE_BG_NEW);// workflowname
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(BUSINESSSERVICE_BG_NEW);
		tradeLicense.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicense.setComment(newBankGuaranteeContract.getNewBankGuaranteeRequest().getWorkflowComment());
		tradeLicense.setWfDocuments(null);
		tradeLicense.setTenantId("hr");
		tradeLicense.setBusinessService(BUSINESSSERVICE_BG_NEW);

		licenses.add(tradeLicense);
		tradeLicenseRequest.setLicenses(licenses);
		tradeLicenseRequest.setRequestInfo(newBankGuaranteeContract.getRequestInfo());
		return tradeLicenseRequest;
	}
	
	private void validateValidityFormat(NewBankGuaranteeContract newBankGuaranteeContract) {
		try {
			if (Objects.nonNull(newBankGuaranteeContract.getNewBankGuaranteeRequest().getValidity())) {
				log.debug("validity in payload:"+newBankGuaranteeContract.getNewBankGuaranteeRequest().getValidity());
				LocalDate localDate = LocalDate
						.parse(newBankGuaranteeContract.getNewBankGuaranteeRequest().getValidity());
			}
		} catch (Exception ex) {
			log.error("Exception while parsing validity into java.time.LocalDate", ex);
			throw new CustomException("Please send the validity in proper yyyy-MM-dd format",
					"Please send the validity in proper yyyy-MM-dd format");
		}
	}
	
	public NewBankGuarantee search(String loiNumber) {
		return this.newBankGuaranteeRepo.findByLoiNumber(loiNumber);
	}
	
	/*
	public RenewBankGuarantee createRenewBankGuarantee(RenewBankGuaranteeContract renewBankGuarantee) {
		return renewBankGuaranteeRepo.save(renewBankGuarantee.getRenewBankGuarantee().toBuilder());
	}
	
	public RenewBankGuarantee searchRenewBankGuarantee(Long renewBankGuaranteeId) {
		return renewBankGuaranteeRepo.getOne(renewBankGuaranteeId);
	}
	
	public ReleaseBankGuarantee createReleaseBankGuarantee(ReleaseBankGuaranteeContract releaseBankGuaranteeContract) {
		return releaseBankGuaranteeRepo.save(releaseBankGuaranteeContract.getReleaseBankGuaranteeRequest().toBuilder());
	}
	
	public ReleaseBankGuarantee searchReleaseBankGuarantee(Long releaseBankGuaranteeId) {
		return releaseBankGuaranteeRepo.getOne(releaseBankGuaranteeId);
	}
	
	public ReplaceBankGuarantee createReplaceBankGuarantee(ReplaceBankGuaranteeContract replaceBankGuaranteeContract) {
		return replaceBankGuaranteeRepo.save(replaceBankGuaranteeContract.getReplaceBankGuarantee().toBuilder());
	}
	
	public ReplaceBankGuarantee searchReplaceBankGuarantee(Long replaceBankGuaranteeId) {
		return replaceBankGuaranteeRepo.getOne(replaceBankGuaranteeId);
	}
	*/
}
