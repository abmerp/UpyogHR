package org.egov.tl.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.tl.service.ElectricPlanService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.ElectricInfoResponse;
import org.egov.tl.web.models.ElectricPlan;
import org.egov.tl.web.models.ElectricPlanContract;
import org.egov.tl.web.models.ElectricPlanRequest;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.ServicePlanInfoResponse;
import org.egov.tl.web.models.ServicePlanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/electric/plan")
public class ElectricPlanController {

	@Autowired
	ElectricPlanService electricPlanService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<ElectricInfoResponse> create(@RequestBody ElectricPlanContract electricPlanContract)
			throws JsonProcessingException {
		List<ElectricPlanRequest> createElectricPlan = electricPlanService.create(electricPlanContract);
//		List<ElectricPlanRequest> electricPlanList = new ArrayList<>();
//		electricPlanList.add(createElectricPlan);
		ElectricInfoResponse electricInfoResponse = ElectricInfoResponse.builder()
				.electricPlanResponse(createElectricPlan).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(electricPlanContract.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(electricInfoResponse, HttpStatus.OK);
	}

	@PostMapping("/_get")
	public ResponseEntity<ElectricInfoResponse> getServicePlan(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(required = false) String loiNumber,
			@RequestParam(required = false) String applicationNumber) {
		List<ElectricPlanRequest> searchElectricPlan = electricPlanService.searchElectricPlan(loiNumber,
				applicationNumber, requestInfoWrapper.getRequestInfo());

		ElectricInfoResponse servicePlanInfoResponse = ElectricInfoResponse.builder()
				.electricPlanResponse(searchElectricPlan).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(servicePlanInfoResponse, HttpStatus.OK);
	}

	@PostMapping("/_update")
	public ResponseEntity<ElectricInfoResponse> UpdateServicePlan(
			@RequestBody ElectricPlanContract servicePlanContract) {
		List<ElectricPlanRequest> updateElectricPlan = electricPlanService.Update(servicePlanContract);
//		List<ElectricPlanRequest> servicePlanRequestList = new ArrayList<>();
//		servicePlanRequestList.add(update);
		ElectricInfoResponse servicePlanInfoResponse = ElectricInfoResponse.builder()
				.electricPlanResponse(updateElectricPlan).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(servicePlanContract.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(servicePlanInfoResponse, HttpStatus.OK);
	}
}
