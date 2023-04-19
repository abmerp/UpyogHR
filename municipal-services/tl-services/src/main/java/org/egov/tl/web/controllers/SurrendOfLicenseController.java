package org.egov.tl.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.egov.tl.service.SurrendOfLicenseServices;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.RevisedPlan;
import org.egov.tl.web.models.RevisedPlanRequest;
import org.egov.tl.web.models.RevisedPlanResponse;
import org.egov.tl.web.models.SurrendOfLicense;
import org.egov.tl.web.models.SurrendOfLicenseRequest;
import org.egov.tl.web.models.SurrendOfLicenseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/SurrendOfLicenseRequest")
public class SurrendOfLicenseController {

	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired
	private SurrendOfLicenseServices surrendOfLicenseServices;

	@PostMapping("/_create")
	public ResponseEntity<SurrendOfLicenseResponse> create(
			@RequestBody SurrendOfLicenseRequest surrendOfLicenseRequest) {

		List<SurrendOfLicense> surrendOfLicense = surrendOfLicenseServices.create(surrendOfLicenseRequest);

		SurrendOfLicenseResponse surrendOfLicenseResponse = SurrendOfLicenseResponse.builder()
				.surrendOfLicense(surrendOfLicense).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(surrendOfLicenseRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(surrendOfLicenseResponse, HttpStatus.OK);
	}

	@PostMapping("/_update")
	public ResponseEntity<SurrendOfLicenseResponse> update(
			@RequestBody SurrendOfLicenseRequest surrendOfLicenseRequest) {

		List<SurrendOfLicense> surrendOfLicense = surrendOfLicenseServices.update(surrendOfLicenseRequest);

//		List<SurrendOfLicense> SurrendOfLicenseList = new ArrayList<>();
//		SurrendOfLicenseList.add(surrendOfLicense);
		SurrendOfLicenseResponse surrendOfLicenseResponse = SurrendOfLicenseResponse.builder()
				.surrendOfLicense(surrendOfLicense).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(surrendOfLicenseRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(surrendOfLicenseResponse, HttpStatus.OK);
	}
	
	@PostMapping("/_search")
	public ResponseEntity<SurrendOfLicenseResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(value = "licenseNo", required = false) String licenceNumber,
			@RequestParam(value = "applicationNumber", required = false) String applicationNumber) {
		List<SurrendOfLicense> surrendOfLicense = surrendOfLicenseServices.search(requestInfoWrapper.getRequestInfo(),
				licenceNumber,applicationNumber);
		
		SurrendOfLicenseResponse surrendOfLicenseResponse = SurrendOfLicenseResponse.builder(). surrendOfLicense(surrendOfLicense)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		
		return new ResponseEntity<>(surrendOfLicenseResponse, HttpStatus.OK);
	}

}
