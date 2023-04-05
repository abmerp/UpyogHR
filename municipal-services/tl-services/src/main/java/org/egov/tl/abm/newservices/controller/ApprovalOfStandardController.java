package org.egov.tl.abm.newservices.controller;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.contract.ApprovalStandardContract;
import org.egov.tl.abm.newservices.contract.ApprovalStandardResponse;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.abm.newservices.entity.NewBankGuarantee;
import org.egov.tl.service.ApprovalStandardService;
import org.egov.tl.util.ResponseInfoFactory;
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

@RestController
@RequestMapping("_ApprovalStandard")
public class ApprovalOfStandardController {
	@Autowired
	ApprovalStandardService approvalStandardService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping(value = "_create")
	public ResponseEntity<ApprovalStandardResponse> createNewService(
			@RequestBody ApprovalStandardContract approvalStandardContract) {

		List<ApprovalStandardEntity> newApprovalServiceInfo = approvalStandardService
				.createNewServic(approvalStandardContract);

		ApprovalStandardResponse response = ApprovalStandardResponse.builder()
				.approvalStandardResponse(newApprovalServiceInfo).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(approvalStandardContract.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("_search")
	public ResponseEntity<ApprovalStandardResponse> searchNewBankGuarantee(@RequestBody RequestInfo requestInfo,
			@RequestParam(value = "licenseNo", required = false) String licenseNo,
			@RequestParam(value = "applicationNumber", required = false) String applicationNumber,
			@RequestParam(value = "tenantId", required = false) String tenantId) {

		List<ApprovalStandardEntity> searchApproval = approvalStandardService.searchApprovalStandard(requestInfo,
				licenseNo, applicationNumber, tenantId);

		ApprovalStandardResponse responseSearch = ApprovalStandardResponse.builder()
				.approvalStandardResponse(searchApproval)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true)).build();
		return new ResponseEntity<>(responseSearch, HttpStatus.OK);
	}

	@PostMapping(value = "_update")
	public ResponseEntity<ApprovalStandardResponse> update(
			@RequestBody ApprovalStandardContract approvalStandardContract) {

		List<ApprovalStandardEntity> newApprovalServiceInfo = approvalStandardService.Update(approvalStandardContract);

		ApprovalStandardResponse response = ApprovalStandardResponse.builder()
				.approvalStandardResponse(newApprovalServiceInfo).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(approvalStandardContract.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
