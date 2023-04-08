package org.egov.tl.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.egov.tl.service.RevisedPlanServices;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.RevisedPlan;
import org.egov.tl.web.models.RevisedPlanRequest;
import org.egov.tl.web.models.RevisedPlanResponse;
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
@RequestMapping("/revisedPlan")
public class RevisedPlanController {

	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired
	private RevisedPlanServices revisedPlanServices;

	@PostMapping("/_create")
	public ResponseEntity<RevisedPlanResponse> create(@RequestBody RevisedPlanRequest revisedPlanRequest) throws JsonProcessingException {

		List<RevisedPlan> revisedPlan = revisedPlanServices.create(revisedPlanRequest);

		RevisedPlanResponse revisedPlanResponse = RevisedPlanResponse.builder().revisedPlan(revisedPlan).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(revisedPlanRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(revisedPlanResponse, HttpStatus.OK);
	}

	@PostMapping("/_update")
	public ResponseEntity<RevisedPlanResponse> update(@RequestBody RevisedPlanRequest revisedPlanRequest) {

		List<RevisedPlan> revisedPlan = revisedPlanServices.update(revisedPlanRequest);
		RevisedPlanResponse revisedPlanResponse = RevisedPlanResponse.builder().revisedPlan(revisedPlan).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(revisedPlanRequest.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(revisedPlanResponse, HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<RevisedPlanResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(value = "licenseNo", required = false) String licenceNumber,
			@RequestParam(value = "applicationNumber", required = false) String applicationNumber) {
		List<RevisedPlan> revisedPlan = revisedPlanServices.search(requestInfoWrapper.getRequestInfo(),
				applicationNumber, licenceNumber);

		RevisedPlanResponse revisedPlanResponse = RevisedPlanResponse.builder().revisedPlan(revisedPlan).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(revisedPlanResponse, HttpStatus.OK);
	}

}
