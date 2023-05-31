package org.egov.tl.web.controllers;

import org.egov.tl.service.CompletionCertificateService;
import org.egov.tl.web.models.CompletionCertificateRequest;
import org.egov.tl.web.models.CompletionCertificateResponse;
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
@RequestMapping("/certicifate/")
public class CompletionCertificateController {
	
	@Autowired
	CompletionCertificateService completionCertificateService;
	
	@PostMapping("_create")
	public ResponseEntity<CompletionCertificateResponse> saveCompletionCertificate(@RequestBody CompletionCertificateRequest completionRequest){
		CompletionCertificateResponse completionCertificateResponse=completionCertificateService.createCompletionCertificate(completionRequest,false);
       return new ResponseEntity<>(completionCertificateResponse, HttpStatus.OK);
	}
	
	@PostMapping("_update")
	public ResponseEntity<CompletionCertificateResponse> updateCompletionCertificate(@RequestBody CompletionCertificateRequest completionRequest
			,@RequestParam(value = "isScrutiny", required = false) String isScrutiny){
		CompletionCertificateResponse completionCertificateResponse=completionCertificateService.createCompletionCertificate(completionRequest,isScrutiny!=null&&!isScrutiny.equals("0")?true:false);   
       return new ResponseEntity<>(completionCertificateResponse, HttpStatus.OK);
	}
	
	@PostMapping("_get")
	public ResponseEntity<CompletionCertificateResponse> getBeneficial(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(value = "licenseNumber", required = false) String licenceNumber,
			@RequestParam(value = "applicationNumber", required = false) String applicationNumber
			) {
		CompletionCertificateResponse changeBeneficialResponse=completionCertificateService.getCompletionCertificate(requestInfoWrapper.getRequestInfo(),applicationNumber,licenceNumber);   
       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
	}
	
//	@PostMapping("_pay")
//	public ResponseEntity<ChangeBeneficialResponse> changeBeneficialPay(@RequestBody RequestInfoWrapper requestInfoWrapper,
//			@RequestParam("licenseNumber") String licenseNumber){
//		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.pay(requestInfoWrapper.getRequestInfo(),licenseNumber);   
//       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
//	}

//	
//	@PostMapping("_billDemandRefresh")
//	public ResponseEntity<ChangeBeneficialResponse>changeBeneficialBillDemandRefresh(@RequestBody RequestInfoWrapper requestInfoWrapper,
//			@RequestParam("licenseNumber") String licenseNumber){
//		ChangeBeneficialResponse changeBeneficialResponse=changeBeneficialService.billAndDemandRefresh(requestInfoWrapper.getRequestInfo(),licenseNumber);   
//       return new ResponseEntity<>(changeBeneficialResponse, HttpStatus.OK);
//	}
//	
//	@PostMapping(value = "/transaction/v1/_redirect", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//	public ResponseEntity<Object> method(@RequestBody MultiValueMap<String, String> formData) {
//
//		return changeBeneficialService.postTransactionDeatil(formData);
//	}
	
}
