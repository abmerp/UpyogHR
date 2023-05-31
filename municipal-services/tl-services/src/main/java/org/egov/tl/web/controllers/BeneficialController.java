package org.egov.tl.web.controllers;

import org.egov.tl.service.ChangeBeneficialService;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.ChangeBeneficialResponse;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/beneficial/")
public class BeneficialController {
	
	@Autowired
	ChangeBeneficialService changeBeneficialService;
	
	@PostMapping("_create")
	public ResponseEntity<ChangeBeneficialResponse> changeBeneficial(@RequestBody ChangeBeneficialRequest beneficialRequest){
         ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.createChangeBeneficial(beneficialRequest,false);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
	@PostMapping("_update")
	public ResponseEntity<ChangeBeneficialResponse> updateChangeBeneficial(@RequestBody ChangeBeneficialRequest beneficialRequest
			,@RequestParam(value = "isScrutiny", required = false) String isScrutiny){
         ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.createChangeBeneficial(beneficialRequest,isScrutiny!=null&&!isScrutiny.equals("0")?true:false);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
	@PostMapping("_pay")
	public ResponseEntity<ChangeBeneficialResponse> changeBeneficialPay(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("licenseNumber") String licenseNumber){
		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.pay(requestInfoWrapper.getRequestInfo(),licenseNumber);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	@PostMapping("_get")
	public ResponseEntity<ChangeBeneficialResponse> getBeneficial(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(value = "licenseNumber", required = false) String licenceNumber,
			@RequestParam(value = "applicationNumber", required = false) String applicationNumber
			) {
		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.getChangeBeneficial(requestInfoWrapper.getRequestInfo(),applicationNumber,licenceNumber);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
	@PostMapping("_billDemandRefresh")
	public ResponseEntity<ChangeBeneficialResponse> _changeBeneficialBillDemandRefresh(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("licenseNumber") String licenseNumber){
		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.billAndDemandRefresh(requestInfoWrapper.getRequestInfo(),licenseNumber);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "/transaction/v1/_redirect", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<Object> method(@RequestBody MultiValueMap<String, String> formData) {

		return changeBeneficialService.postTransactionDeatil(formData);
	}
	
}
