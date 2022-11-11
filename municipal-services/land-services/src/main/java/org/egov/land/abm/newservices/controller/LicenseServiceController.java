package org.egov.land.abm.newservices.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.land.abm.models.EgScrutinyInfoResponse;
import org.egov.land.abm.models.LicenseServiceRequest;
import org.egov.land.abm.models.LicenseServiceResponse;
import org.egov.land.abm.newservices.entity.*;
import org.egov.land.abm.service.LicenseService;
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
public class LicenseServiceController {

	@Autowired
	LicenseService newServiceInfoService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping(value = "_create")
	public  ResponseEntity<LicenseServiceResponse> createNewService(@RequestBody LicenseServiceRequest newService) throws JsonProcessingException {

		LicenseServiceDao newServiceInfo = newServiceInfoService.createNewServic(newService.getNewServiceInfo(),newService.getRequestInfo().getUserInfo());

		List<LicenseServiceDao> newServiceInfoList = new ArrayList<>();
		newServiceInfoList.add(newServiceInfo);
		LicenseServiceResponse newServiceResponseInfo = LicenseServiceResponse.builder().newServiceInfo(newServiceInfoList).
																								responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(newService.getRequestInfo(), true)).
																								build();
		
		return new ResponseEntity<>(newServiceResponseInfo, HttpStatus.OK);  
	}

	@GetMapping("/licenses/_get")
	public LicenseServiceDao getNewServicesDetailById(@RequestParam("id") Long id) {
		return newServiceInfoService.getNewServicesInfoById(id);
	}
	
	@GetMapping("/licenses/_getall")
	public List<LicenseServiceDao> getNewServicesDetailAll() {

		return newServiceInfoService.getNewServicesInfoAll();
	}

	
	@GetMapping("/licenses/_applicants")
	public List<String> getApplicantsNumber(){
		return newServiceInfoService.getApplicantsNumber();
	}

}
