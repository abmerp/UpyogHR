package org.egov.tl.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.egov.tl.service.LicenseService;
import org.egov.tl.service.dao.LicenseServiceDao;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.LicenseServiceRequest;
import org.egov.tl.web.models.LicenseServiceRequestInfo;
import org.egov.tl.web.models.LicenseServiceResponse;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
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
@RequestMapping("new")
public class LicenseServiceController {

	@Autowired
	LicenseService newServiceInfoService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	
	@PostMapping(value = "_create")
	public  ResponseEntity<LicenseServiceResponse> createNewService(@RequestBody LicenseServiceRequest newService) throws JsonProcessingException {

		LicenseServiceResponseInfo newServiceInfo = newServiceInfoService.createNewServic(newService.getNewServiceInfo(),newService.getRequestInfo().getUserInfo());

		List<LicenseServiceResponseInfo> newServiceInfoList = new ArrayList<>();
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
