package org.egov.land.abm.newservices.controller;

import java.util.ArrayList;
import java.util.List;

import org.egov.land.abm.contract.NewBankGuaranteeContract;
import org.egov.land.abm.models.NewBankGuaranteeResponse;
import org.egov.land.abm.newservices.entity.NewBankGuarantee;
import org.egov.land.abm.service.BankGuaranteeService;
import org.egov.land.util.ResponseInfoFactory;
import org.egov.land.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bank")
public class BankGuaranteeController {

	@Autowired private ResponseInfoFactory responseInfoFactory;
	@Autowired BankGuaranteeService bankGuaranteeService;
	
	@PostMapping("/guarantee/_create")
	public ResponseEntity<NewBankGuaranteeResponse> create(@RequestBody NewBankGuaranteeContract newBankGuaranteeContract){
		
		NewBankGuarantee newBankguarantee = bankGuaranteeService.createAndUpdate(newBankGuaranteeContract);
		List<NewBankGuarantee> newBankGuaranteeList = new ArrayList<NewBankGuarantee>();
		newBankGuaranteeList.add(newBankguarantee);
		NewBankGuaranteeResponse newBankGuaranteeResponse = NewBankGuaranteeResponse.builder().newBankGuaranteeList(newBankGuaranteeList)
											.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(newBankGuaranteeContract.getRequestInfo(), true))
											.build();
		
		return new ResponseEntity<>(newBankGuaranteeResponse, HttpStatus.OK);	
	}
	
	@PostMapping("/guarantee/_search")
	public ResponseEntity<NewBankGuaranteeResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper){
		
		NewBankGuarantee newBankguarantee = bankGuaranteeService.search(requestInfoWrapper.getLoiNumber());
		List<NewBankGuarantee> newBankGuaranteeList = new ArrayList<NewBankGuarantee>();
		newBankGuaranteeList.add(newBankguarantee);
		NewBankGuaranteeResponse newBankGuaranteeResponse = NewBankGuaranteeResponse.builder().newBankGuaranteeList(newBankGuaranteeList)
											.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
											.build();
		
		return new ResponseEntity<>(newBankGuaranteeResponse, HttpStatus.OK);	
	}
	
	
	
}
