package org.egov.tl.web.controllers;

import java.util.List;

import org.egov.tl.abm.newservices.contract.AdditionalDocumentsContract;
import org.egov.tl.abm.newservices.contract.AdditionalDocumentResponse;
import org.egov.tl.service.AdditionalDocumentsService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.AdditionalDocuments;
import org.egov.tl.web.models.RequestInfoWrapper;
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
@RequestMapping("/_additionalDocuments")
public class AdditionalDocumentController {
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired
	private AdditionalDocumentsService allServiceFindservice;

	@PostMapping("/_create")
	public ResponseEntity<AdditionalDocumentResponse> create(
			@RequestBody AdditionalDocumentsContract allServiceFindContract) throws JsonProcessingException {
		List<AdditionalDocuments> allServiceFindList = allServiceFindservice.create(allServiceFindContract);
		AdditionalDocumentResponse allServiceResponse = AdditionalDocumentResponse.builder()
				.additionalDocuments(allServiceFindList).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(allServiceFindContract.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(allServiceResponse, HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<AdditionalDocumentResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(required = true) String businessService, @RequestParam(required = false) String type,
			@RequestParam(required = true) String licenceNumber,
			@RequestParam(required = true) String applicationSection) {
		List<AdditionalDocuments> allServiceFindList = allServiceFindservice.search(requestInfoWrapper.getRequestInfo(),
				businessService, type, licenceNumber, applicationSection);

		AdditionalDocumentResponse allServiceResponse = AdditionalDocumentResponse.builder()
				.additionalDocuments(allServiceFindList).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(allServiceResponse, HttpStatus.OK);
	}

}
