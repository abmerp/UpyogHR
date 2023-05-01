package org.egov.tl.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.egov.tl.service.ServicePlanService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.ServicePlan;
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
@RequestMapping("/serviceplan")
public class ServicePlanController {

	@Autowired ServicePlanService servicePlanService;
	@Autowired private ResponseInfoFactory responseInfoFactory;
	
	@PostMapping("/_create")
	public ResponseEntity<ServicePlanInfoResponse> createServicePlan(
			@RequestBody ServicePlanContract servicePlanContract) throws JsonProcessingException {
		List<ServicePlanRequest> servicePlanRequestList = servicePlanService.create(servicePlanContract);
		ServicePlanInfoResponse servicePlanInfoResponse = ServicePlanInfoResponse.builder()
				.servicePlanResponse(servicePlanRequestList).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(servicePlanContract.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(servicePlanInfoResponse, HttpStatus.OK);
	}
	
	@PostMapping("/_get")
	public ResponseEntity<ServicePlanInfoResponse> getServicePlan(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(required = false) String loiNumber , @RequestParam(required = false) String applicationNumber){
		List<ServicePlanRequest> servicePlanRequest = servicePlanService.searchServicePlan(loiNumber , applicationNumber , requestInfoWrapper.getRequestInfo());
		ServicePlanInfoResponse servicePlanInfoResponse = ServicePlanInfoResponse.builder().
				servicePlanResponse(servicePlanRequest).responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(
						requestInfoWrapper.getRequestInfo(), true)).build();
		return new ResponseEntity<>(servicePlanInfoResponse, HttpStatus.OK);	
	}

	@PostMapping("/_update")
	public ResponseEntity<ServicePlanInfoResponse> UpdateServicePlan(
			@RequestBody ServicePlanContract servicePlanContract) {
		 List<ServicePlanRequest> servicePlanRequestList = servicePlanService.Update(servicePlanContract);
		ServicePlanInfoResponse servicePlanInfoResponse = ServicePlanInfoResponse.builder()
				.servicePlanResponse(servicePlanRequestList).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(servicePlanContract.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(servicePlanInfoResponse, HttpStatus.OK);
	}
}
