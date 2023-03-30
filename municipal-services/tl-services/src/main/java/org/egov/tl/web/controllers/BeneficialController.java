package org.egov.tl.web.controllers;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.service.ChangeBeneficialService;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.ChangeBeneficialResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/beneficial/")
public class BeneficialController {
	
	@Autowired
	ChangeBeneficialService changeBeneficialService;
	
	@PostMapping(value = "_create")
	public ResponseEntity<ChangeBeneficialResponse> changeBeneficial(@RequestBody ChangeBeneficialRequest beneficialRequest)
			throws JsonProcessingException {
//		changeBeneficialService.getLicenseByApplication
         ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.createChangeBeneficial(beneficialRequest);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "_pay")
	public ResponseEntity<ChangeBeneficialResponse> changeBeneficialPay(@RequestBody RequestInfo requestInfo,
			@RequestParam("applicationNumber") String applicationNumber)
			throws JsonProcessingException {
		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.pay(requestInfo,applicationNumber);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	@GetMapping(value = "_get")
	public ResponseEntity<ChangeBeneficialResponse> getBeneficial(@RequestBody RequestInfo requestInfo,
			@RequestParam("applicationNumber") String applicationNumber)
			throws JsonProcessingException {
		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.getChangeBeneficial(requestInfo,applicationNumber);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "_billDemandRefresh")
	public ResponseEntity<ChangeBeneficialResponse> _changeBeneficialBillDemandRefresh(@RequestBody RequestInfo requestInfo,
			@RequestParam("applicationNumber") String applicationNumber,
			@RequestParam("isIntialPayment") int isIntialPayment,
			@RequestParam("calculationType") int calculationType,
			@RequestParam("calculationServiceName") String calculationServiceName
			)
			throws JsonProcessingException {
		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.billAndDemandRefresh(requestInfo,applicationNumber,calculationServiceName,calculationType,isIntialPayment);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "/transaction/v1/_redirect", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<Object> method(@RequestBody MultiValueMap<String, String> formData) {

		return changeBeneficialService.postTransactionDeatil(formData);
	}
	
	
}
