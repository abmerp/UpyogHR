package org.egov.tl.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.contract.ApprovalStandardContract;
import org.egov.tl.abm.newservices.contract.ApprovalStandardResponse;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.abm.newservices.entity.NewBankGuarantee;
import org.egov.tl.service.ApprovalStandardService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.SurrendOfLicense;
import org.egov.tl.web.models.TradeLicenseResponse;
import org.egov.tl.web.models.bankguarantee.NewBankGuaranteeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("_ApprovalStandard")
public class ApprovalOfStandardController {
	@Autowired
	ApprovalStandardService approvalStandardService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping(value = "_create")
	public ResponseEntity<ApprovalStandardResponse> createNewService(
			@RequestBody ApprovalStandardContract approvalStandardContract) throws JsonProcessingException {

		ApprovalStandardEntity newApprovalServiceInfo = approvalStandardService
				.createNewServic(approvalStandardContract);
		List<ApprovalStandardEntity> approvalStandardEntityList = new ArrayList<>();
		approvalStandardEntityList.add(newApprovalServiceInfo);
		ApprovalStandardResponse response = ApprovalStandardResponse.builder()
				.approvalStandardRequest(approvalStandardEntityList).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(approvalStandardContract.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("_search")
	public ResponseEntity<ApprovalStandardResponse> searchApprovalStandard(
			@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(value = "licenseNo", required = false) String licenseNo,
			@RequestParam(value = "applicationNumber", required = false) String applicationNumber) {

		List<ApprovalStandardEntity> searchApproval = approvalStandardService
				.searchApprovalStandard(requestInfoWrapper.getRequestInfo(), licenseNo, applicationNumber);

		ApprovalStandardResponse responseSearch = ApprovalStandardResponse.builder()
				.approvalStandardRequest(searchApproval).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(responseSearch, HttpStatus.OK);
	}

	@PostMapping(value = "_update")
	public ResponseEntity<ApprovalStandardResponse> update(
			@RequestBody ApprovalStandardContract approvalStandardContract) {

		ApprovalStandardEntity newApprovalServiceInfo = approvalStandardService.Update(approvalStandardContract);
		List<ApprovalStandardEntity> approvalStandardEntityList = new ArrayList<>();
		approvalStandardEntityList.add(newApprovalServiceInfo);
		ApprovalStandardResponse response = ApprovalStandardResponse.builder()
				.approvalStandardRequest(approvalStandardEntityList).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(approvalStandardContract.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
