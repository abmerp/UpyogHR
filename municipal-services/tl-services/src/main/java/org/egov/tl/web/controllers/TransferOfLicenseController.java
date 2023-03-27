package org.egov.tl.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.egov.tl.service.TransferOfLicenseServices;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.Transfer;
import org.egov.tl.web.models.TransferOfLicenseRequest;
import org.egov.tl.web.models.TransferOfLicenseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/TransferOfLicenseRequest")
public class TransferOfLicenseController {
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired 
	private TransferOfLicenseServices transferOfLicenseServices;
	
	@PostMapping("/_create")
	public ResponseEntity<TransferOfLicenseResponse> create(@RequestBody TransferOfLicenseRequest transferOfLicenseRequest) {
		
		Transfer transfer = transferOfLicenseServices.create(transferOfLicenseRequest);
		
		List<Transfer> TransferList = new ArrayList<>();
		TransferList.add(transfer);
		TransferOfLicenseResponse transferOfLicenseResponse = TransferOfLicenseResponse.builder().transfer(TransferList).
				responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(transferOfLicenseRequest.getRequestInfo(),true)).build();
				
	
		return new ResponseEntity<>(transferOfLicenseResponse, HttpStatus.OK);
	}
	
	@PostMapping("/_update")
	public ResponseEntity<TransferOfLicenseResponse> update(
			@RequestBody TransferOfLicenseRequest transferOfLicenseRequest) {
		Transfer transfer = transferOfLicenseServices.create(transferOfLicenseRequest);
		List<Transfer> TransferList = new ArrayList<>();
		
		TransferList.add(transfer);
		TransferOfLicenseResponse transferOfLicenseResponse = TransferOfLicenseResponse.builder(). transfer(TransferList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(transferOfLicenseRequest.getRequestInfo(), true))
				.build();
		
		return new ResponseEntity<>(transferOfLicenseResponse, HttpStatus.OK);
	}
	
	@PostMapping("/_search")
	public ResponseEntity<TransferOfLicenseResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,@RequestParam("licenseNo") Integer id) {

		Transfer transfer = transferOfLicenseServices.search(id);
		List<Transfer> TransferList = new ArrayList<>();
		TransferList.add(transfer);
		
		TransferOfLicenseResponse transferOfLicenseResponse = TransferOfLicenseResponse.builder(). transfer(TransferList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		
		return new ResponseEntity<>(transferOfLicenseResponse, HttpStatus.OK);
	}


}
