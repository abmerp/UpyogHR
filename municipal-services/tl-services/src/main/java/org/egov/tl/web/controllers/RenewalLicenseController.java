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
import org.egov.tl.web.models.RenewalLicenseResponse;
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
	public ResponseEntity<RenewalLicenseResponse> createRenewLicense(@RequestBody RenewalLicenseRequest renewalRequest){
        List<RenewalLicense> renewalLicense=renewalLicenseService.saveRenewalLicense(renewalRequest);
        RenewalLicenseResponse renewalLicenseResponse=RenewalLicenseResponse.builder().message("Record has been inserted successfully.").status(true).renewalLicense(renewalLicense).build();
		return new ResponseEntity<>(renewalLicenseResponse, HttpStatus.OK);
	}
	
	@PostMapping("_get")
	public ResponseEntity<RenewalLicenseResponse> getNewServicesDetailById(
			@RequestParam("applicationNumber") String applicationNumber,
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		RenewalLicenseResponse renewalLicenseResponse=null;
		List<RenewalLicense> renuLicenseDetails = renewalLicenseService.getRenewalLicense(applicationNumber);
			if(renuLicenseDetails!=null) {
				renewalLicenseResponse =RenewalLicenseResponse.builder().message("Record has been Fetched successfully.").status(true).renewalLicense(renuLicenseDetails).build();
			}else{
				renewalLicenseResponse =RenewalLicenseResponse.builder().message("Record Not Found.").status(false).renewalLicense(null).build();
			}
		return new ResponseEntity<>(renewalLicenseResponse, HttpStatus.OK);
	}

}
