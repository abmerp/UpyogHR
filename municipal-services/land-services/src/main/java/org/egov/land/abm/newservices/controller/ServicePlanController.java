package org.egov.land.abm.newservices.controller;

import java.util.ArrayList;
import java.util.List;

import org.egov.land.abm.contract.ServicePlanContract;
import org.egov.land.abm.models.ServicePlanInfoResponse;
import org.egov.land.abm.newservices.entity.ServicePlan;
import org.egov.land.abm.service.ServicePlanService;
import org.egov.land.util.ResponseInfoFactory;
import org.egov.land.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/serviceplan")
public class ServicePlanController {

	@Autowired ServicePlanService servicePlanService;
	@Autowired private ResponseInfoFactory responseInfoFactory;
	
	@PostMapping("/_create")
	public ResponseEntity<ServicePlanInfoResponse> createAndUpdateServicePlan(@RequestBody ServicePlanContract servicePlanContract) {
		ServicePlan servicePlan = servicePlanService.createAndUpdate(servicePlanContract);
		List<ServicePlan> servicePlanList = new ArrayList<>();
		servicePlanList.add(servicePlan);
		ServicePlanInfoResponse servicePlanInfoResponse = ServicePlanInfoResponse.builder().
				servicePlanList(servicePlanList).responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(
						servicePlanContract.getRequestInfo(), true)).build();
		return new ResponseEntity<>(servicePlanInfoResponse, HttpStatus.OK);	
	}
	
	@PostMapping("/_get")
	public ResponseEntity<ServicePlanInfoResponse> getServicePlan(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("loiNumber") String loiNumber){
		ServicePlan servicePlan = servicePlanService.searchServicePlan(loiNumber);
		List<ServicePlan> servicePlanList = new ArrayList<>();
		servicePlanList.add(servicePlan);
		ServicePlanInfoResponse servicePlanInfoResponse = ServicePlanInfoResponse.builder().
				servicePlanList(servicePlanList).responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(
						requestInfoWrapper.getRequestInfo(), true)).build();
		return new ResponseEntity<>(servicePlanInfoResponse, HttpStatus.OK);	
	}
}
