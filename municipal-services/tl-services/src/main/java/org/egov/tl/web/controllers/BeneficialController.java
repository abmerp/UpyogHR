package org.egov.tl.web.controllers;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.service.ChangeBeneficialService;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.ChangeBeneficialResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<ChangeBeneficialResponse> changeBeneficial(@RequestBody ChangeBeneficialRequest beneficialRequest,@RequestParam("applicationNumber") String applicationNumber)
			throws JsonProcessingException {
//		changeBeneficialService.getLicenseByApplication
         ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.createChangeBeneficial(beneficialRequest,applicationNumber);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "_calculationBillGenerate")
	public ResponseEntity<ChangeBeneficialResponse> changeBeneficialPay(@RequestBody RequestInfo requestInfo,
			@RequestParam("applicationNumber") String applicationNumber,
			@RequestParam("isIntialPayment") int isIntialPayment,
			@RequestParam("calculationType") int calculationType,
			@RequestParam("calculationServiceName") String calculationServiceName
			)
			throws JsonProcessingException {
		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.createChangeBeneficialPay(requestInfo,applicationNumber,calculationServiceName,calculationType,isIntialPayment);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
}
