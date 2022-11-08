package org.egov.land.abm.newservices.controller;

import java.util.ArrayList;
import java.util.List;

import org.egov.land.abm.contract.ElectricPlanContract;
import org.egov.land.abm.models.ElectricInfoResponse;
import org.egov.land.abm.newservices.entity.ElectricPlan;
import org.egov.land.abm.service.ElectricPlanService;
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
@RequestMapping("/electric/plan")
public class ElectricPlanController {

	@Autowired ElectricPlanService electricPlanService;
	@Autowired private ResponseInfoFactory responseInfoFactory;
	
	@PostMapping("/_create")
	public  ResponseEntity<ElectricInfoResponse> createAndUpdateElectric(@RequestBody ElectricPlanContract electricPlanContract) {
		ElectricPlan electricPlan = electricPlanService.createAndUpdateElectricPlan(electricPlanContract);
		List<ElectricPlan> electricPlanList = new ArrayList<>();
		electricPlanList.add(electricPlan);
		ElectricInfoResponse electricInfoResponse = ElectricInfoResponse.builder().electricPlanList(electricPlanList).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(electricPlanContract.getRequestInfo(), true)).build();
		return new ResponseEntity<>(electricInfoResponse,HttpStatus.OK);
	}
	
	@PostMapping("/_get")
	public ResponseEntity<ElectricInfoResponse> getElectricPlan(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("id") Long id){
		ElectricPlan electricPlan = electricPlanService.searchElectricPlan(id);
		List<ElectricPlan> electricPlanList = new ArrayList<>();
		electricPlanList.add(electricPlan);
		ElectricInfoResponse electricInfoResponse = ElectricInfoResponse.builder().electricPlanList(electricPlanList).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true)).build();
		return new ResponseEntity<>(electricInfoResponse,HttpStatus.OK);
		
	}
}
