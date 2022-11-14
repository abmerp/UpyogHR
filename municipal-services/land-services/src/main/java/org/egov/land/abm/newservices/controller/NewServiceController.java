package org.egov.land.abm.newservices.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.land.abm.models.EgScrutinyInfoResponse;
import org.egov.land.abm.newservices.entity.*;
import org.egov.land.abm.report.LoiApprovalReport;
import org.egov.land.abm.service.NewServiceInfoService;
import org.egov.land.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("new")
public class NewServiceController {

	@Autowired
	NewServiceInfoService newServiceInfoService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired LoiApprovalReport loiApprovalreport;

	@PostMapping(value = "_create")
	public  ResponseEntity<NewServiceResponseInfo> createNewService(@RequestBody NewService newService) throws JsonProcessingException {

		NewServiceInfo newServiceInfo = newServiceInfoService.createNewServic(newService.getNewServiceInfo(),newService.getRequestInfo().getUserInfo());

		List<NewServiceInfo> newServiceInfoList = new ArrayList<>();
		newServiceInfoList.add(newServiceInfo);
		NewServiceResponseInfo newServiceResponseInfo = NewServiceResponseInfo.builder().newServiceInfo(newServiceInfoList).
																								responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(newService.getRequestInfo(), true)).
																								build();
		
		return new ResponseEntity<>(newServiceResponseInfo, HttpStatus.OK);  
	}

	@GetMapping("/licenses/_get")
	public NewServiceInfo getNewServicesDetailById(@RequestParam("id") Long id) {
		return newServiceInfoService.getNewServicesInfoById(id);
	}
	
	@GetMapping("/licenses/_getall")
	public List<NewServiceInfo> getNewServicesDetailAll() {

		return newServiceInfoService.getNewServicesInfoAll();
	}

	
	@GetMapping("/licenses/_applicants")
	public List<String> getApplicantsNumber(){
		return newServiceInfoService.getApplicantsNumber();
	}
	
	@PostMapping("/generatereport")
	public String generateReport() {
		//loiApprovalreport.createLoiReport();
		return null;
	}

}
