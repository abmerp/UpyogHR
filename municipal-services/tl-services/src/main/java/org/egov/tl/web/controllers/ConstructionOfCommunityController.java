package org.egov.tl.web.controllers;

import org.egov.tl.service.ConstructionOfCommunityService;
import org.egov.tl.web.models.CompletionCertificateRequest;
import org.egov.tl.web.models.CompletionCertificateResponse;
import org.egov.tl.web.models.ConstructionOfCommunityRequest;
import org.egov.tl.web.models.ConstructionOfCommunityResponse;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/construction/")
public class ConstructionOfCommunityController {
	
	@Autowired
	ConstructionOfCommunityService constructionOfCommunityService;
	
	
	@PostMapping("_create")
	public ResponseEntity<ConstructionOfCommunityResponse> saveConstructionOfCommunity(@RequestBody ConstructionOfCommunityRequest constructionOfCommunity){
		ConstructionOfCommunityResponse constructionOfCommunityResponse=constructionOfCommunityService.saveConstructionOfCommunity(constructionOfCommunity);
       return new ResponseEntity<>(constructionOfCommunityResponse, HttpStatus.OK);
	}
	
	@PostMapping("_update")
	public ResponseEntity<ConstructionOfCommunityResponse> updateCompletionCertificate(@RequestBody ConstructionOfCommunityRequest constructionOfCommunity){
		ConstructionOfCommunityResponse constructionOfCommunityResponse=constructionOfCommunityService.saveConstructionOfCommunity(constructionOfCommunity);   
       return new ResponseEntity<>(constructionOfCommunityResponse, HttpStatus.OK);
	}
	
	@PostMapping("_get")
	public ResponseEntity<ConstructionOfCommunityResponse> getBeneficial(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(value = "licenseNumber", required = false) String licenceNumber,
			@RequestParam(value = "applicationNumber", required = false) String applicationNumber
			) {
		ConstructionOfCommunityResponse constructionOfCommunityResponse=constructionOfCommunityService.getConstructionOfCommunity(requestInfoWrapper.getRequestInfo(),applicationNumber,licenceNumber);   
       return new ResponseEntity<>(constructionOfCommunityResponse, HttpStatus.OK);
	}
	
}
