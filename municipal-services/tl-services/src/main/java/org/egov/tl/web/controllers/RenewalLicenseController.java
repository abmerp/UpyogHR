package org.egov.tl.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.egov.tl.service.LicenseService;
import org.egov.tl.service.RenewalLicenseService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.LicenseServiceRequest;
import org.egov.tl.web.models.LicenseServiceResponse;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.RenewalLicense;
import org.egov.tl.web.models.RenewalLicenseRequest;
import org.egov.tl.web.models.RequestInfoWrapper;
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
@RequestMapping("renewal/")
public class RenewalLicenseController {
	
	
	@Autowired
	LicenseService newServiceInfoService;
	
	@Autowired
	RenewalLicenseService renewalLicenseService;
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	
	
	@PostMapping(value = "_create")
	public ResponseEntity<Map<String,Object>> createRenewLicense(@RequestBody RenewalLicenseRequest renewalRequest,@RequestParam String applicationNumber)
			throws JsonProcessingException {
        Map<String,Object> response=new HashMap<>();
       
        renewalLicenseService.saveRenewalLicense(renewalRequest);
        response.put("data", renewalRequest);
        
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("_get")
	public ResponseEntity<LicenseServiceResponseInfo> getNewServicesDetailById(
			@RequestParam("applicationNumber") String applicationNumber,
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return new ResponseEntity<>(
				newServiceInfoService.getNewServicesInfoById(applicationNumber, requestInfoWrapper.getRequestInfo()),
				HttpStatus.OK);
	}

}
