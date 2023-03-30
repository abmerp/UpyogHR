package org.egov.tl.web.controllers;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.service.ChangeBeneficialService;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.ChangeBeneficialResponse;
import org.egov.tl.web.models.RequestInfoWrapper;
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
	
	@PostMapping("_create")
	public ResponseEntity<ChangeBeneficialResponse> changeBeneficial(@RequestBody ChangeBeneficialRequest beneficialRequest){
//		changeBeneficialService.getLicenseByApplication
         ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.createChangeBeneficial(beneficialRequest);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
	@PostMapping("_pay")
	public ResponseEntity<ChangeBeneficialResponse> changeBeneficialPay(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationNumber") String applicationNumber){
		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.pay(requestInfoWrapper.getRequestInfo(),applicationNumber);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	@PostMapping("_get")
	public ResponseEntity<ChangeBeneficialResponse> getBeneficial(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationNumber") String applicationNumber) {
		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.getChangeBeneficial(requestInfoWrapper.getRequestInfo(),applicationNumber);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
	@PostMapping("_billDemandRefresh")
	public ResponseEntity<ChangeBeneficialResponse> _changeBeneficialBillDemandRefresh(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationNumber") String applicationNumber,
			@RequestParam("isIntialPayment") int isIntialPayment,
			@RequestParam("calculationType") int calculationType,
			@RequestParam("calculationServiceName") String calculationServiceName
			){
		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.billAndDemandRefresh(requestInfoWrapper.getRequestInfo(),applicationNumber,calculationServiceName,calculationType,isIntialPayment);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "/transaction/v1/_redirect", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<Object> method(@RequestBody MultiValueMap<String, String> formData) {

		return changeBeneficialService.postTransactionDeatil(formData);
	}
	
	
}
