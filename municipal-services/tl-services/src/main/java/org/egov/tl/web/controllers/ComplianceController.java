package org.egov.tl.web.controllers;

import java.util.List;

import org.egov.tl.abm.newservices.contract.ComplianceContract;
import org.egov.tl.abm.newservices.contract.ComplianceResponse;
import org.egov.tl.service.ComplianceService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.ComplianceRequest;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.ServicePlanContract;
import org.egov.tl.web.models.ServicePlanInfoResponse;
import org.egov.tl.web.models.ServicePlanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/_compliance")
public class ComplianceController {
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired
	private ComplianceService complianceService;

	@PostMapping("/_create")
	public ResponseEntity<ComplianceResponse> create(@RequestBody ComplianceContract complianceContract)
			throws JsonProcessingException {
		List<ComplianceRequest> compliancelist = complianceService.create(complianceContract);
		ComplianceResponse complianceResponse = ComplianceResponse.builder().complianceRequest(compliancelist)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(complianceContract.getRequestInfo(),
						true))
				.build();
		return new ResponseEntity<>(complianceResponse, HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<ComplianceResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(required = false) String applicationNumber) {
		List<ComplianceRequest> compliancelist = complianceService.search(requestInfoWrapper.getRequestInfo(),
				applicationNumber);
		ComplianceResponse complianceResponse = ComplianceResponse.builder().complianceRequest(compliancelist)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();
		return new ResponseEntity<>(complianceResponse, HttpStatus.OK);
	}

	@PostMapping("/_update")
	public ResponseEntity<ComplianceResponse> update(@RequestBody ComplianceContract complianceContract)
			throws JsonProcessingException {
		List<ComplianceRequest> compliancelist = complianceService.update(complianceContract);
		ComplianceResponse complianceResponse = ComplianceResponse.builder().complianceRequest(compliancelist)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(complianceContract.getRequestInfo(),
						true))
				.build();
		return new ResponseEntity<>(complianceResponse, HttpStatus.OK);
	}
}
