package org.egov.tl.abm.newservices.controller;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.contract.BankGuaranteeSearchContract;
import org.egov.tl.abm.newservices.contract.NewBankGuaranteeContract;
import org.egov.tl.abm.newservices.contract.RenewBankGuaranteeContract;
import org.egov.tl.abm.newservices.entity.NewBankGuarantee;
import org.egov.tl.abm.newservices.entity.RenewBankGuarantee;
import org.egov.tl.service.BankGuaranteeService;
import org.egov.tl.service.RealeseBankGurenteeService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.CompletionCertificateRequest;
import org.egov.tl.web.models.CompletionCertificateResponse;
import org.egov.tl.web.models.RealeseBankGurenteeRequest;
import org.egov.tl.web.models.RealeseBankGurenteeResponse;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.bankguarantee.NewBankGuaranteeResponse;
import org.egov.tl.web.models.bankguarantee.RenewBankGuaranteeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/realese/bg/")
public class RealeseBankGurenteeController {

	@Autowired RealeseBankGurenteeService realeseBankGurenteeService;
	
	@PostMapping("_create")
	public ResponseEntity<RealeseBankGurenteeResponse> updateRealeseBankGurentee(@RequestBody RealeseBankGurenteeRequest realeseBankGurenteeRequest){
		RealeseBankGurenteeResponse realeseBankGurenteeResponse=realeseBankGurenteeService.createReleaseBankGuarantee(realeseBankGurenteeRequest);   
       return new ResponseEntity<>(realeseBankGurenteeResponse, HttpStatus.OK);
	}
	
	@PostMapping("_get")
	public ResponseEntity<RealeseBankGurenteeResponse> getRealeseBankGurentee(@RequestBody RequestInfoWrapper requestInfoWrapper,@RequestParam(value = "bgNumber") String bgNumber) {
		RealeseBankGurenteeResponse realeseBankGurenteeResponse=realeseBankGurenteeService.getRealeseBankGurentee(bgNumber,requestInfoWrapper);   
       return new ResponseEntity<>(realeseBankGurenteeResponse, HttpStatus.OK);
	}
	
	
	/********************************** Realese bank gurentee start ******************************/
	
	@PostMapping("/guarantee/dropdonelist")
	public ResponseEntity<List<String>> dropdonelist(@RequestBody RequestInfo requestInfo,
			@RequestParam(value = "type", required = true) int type){
			List<String> lst = realeseBankGurenteeService.getDropDownList(type,requestInfo);
		return new ResponseEntity<>(lst, HttpStatus.OK);
	}
	
	/********************************** Realese bank gurentee end******************************/
	
}
