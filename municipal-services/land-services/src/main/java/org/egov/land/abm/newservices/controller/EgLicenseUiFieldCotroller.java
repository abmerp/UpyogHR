package org.egov.land.abm.newservices.controller;

import java.util.ArrayList;
import java.util.List;

import org.egov.land.abm.models.EgLiecnseUiFieldInfoRequest;
import org.egov.land.abm.models.EgLiecnseUiFieldInfoResponse;
import org.egov.land.abm.models.EgScrutinyInfoRequest;
import org.egov.land.abm.models.EgScrutinyInfoResponse;
import org.egov.land.abm.newservices.entity.EgLiecnseUiField;
import org.egov.land.abm.newservices.entity.EgScrutiny;
import org.egov.land.abm.service.EgLiecnseUiFieldServices;
import org.egov.land.util.ResponseInfoFactory;
import org.egov.land.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/LicenseUiField")
public class EgLicenseUiFieldCotroller {

	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired EgLiecnseUiFieldServices egLiecnseUiFieldServices;
	
	
	@PostMapping("/_create")
	public ResponseEntity<EgLiecnseUiFieldInfoResponse> create(@RequestBody EgLiecnseUiFieldInfoRequest egLiecnseUiFieldInfoRequest) {

		EgLiecnseUiField egLiecnseUiField = egLiecnseUiFieldServices.create(egLiecnseUiFieldInfoRequest);
		List<EgLiecnseUiField> EgLiecnseUiFieldList = new ArrayList<>();
		EgLiecnseUiFieldList.add(egLiecnseUiField);
		EgLiecnseUiFieldInfoResponse egLiecnseUiFieldInfoResponse = EgLiecnseUiFieldInfoResponse.builder(). egLiecnseUiField(EgLiecnseUiFieldList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(egLiecnseUiFieldInfoRequest.getRequestInfo(), true))
				.build();
		
		return new ResponseEntity<>(egLiecnseUiFieldInfoResponse, HttpStatus.OK);
	}
	
	@PostMapping("/_update")
	public ResponseEntity<EgLiecnseUiFieldInfoResponse> update(
			@RequestBody EgLiecnseUiFieldInfoRequest egLiecnseUiFieldInfoRequest) {

		EgLiecnseUiField egLiecnseUiField = egLiecnseUiFieldServices.create(egLiecnseUiFieldInfoRequest);
		List<EgLiecnseUiField> EgLiecnseUiFieldList = new ArrayList<>();
		EgLiecnseUiFieldList.add(egLiecnseUiField);
		EgLiecnseUiFieldInfoResponse egLiecnseUiFieldInfoResponse = EgLiecnseUiFieldInfoResponse.builder(). egLiecnseUiField(EgLiecnseUiFieldList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(egLiecnseUiFieldInfoRequest.getRequestInfo(), true))
				.build();
		
		return new ResponseEntity<>(egLiecnseUiFieldInfoResponse, HttpStatus.OK);
	}
	
	@PostMapping("/_search")
	public ResponseEntity<EgLiecnseUiFieldInfoResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,@RequestParam("id") Integer id) {

		EgLiecnseUiField egLiecnseUiField = egLiecnseUiFieldServices.search(id);
		List<EgLiecnseUiField> egLiecnseUiFieldList = new ArrayList<>();
		egLiecnseUiFieldList.add(egLiecnseUiField);
		
		EgLiecnseUiFieldInfoResponse egLiecnseUiFieldInfoResponse = EgLiecnseUiFieldInfoResponse.builder(). egLiecnseUiField(egLiecnseUiFieldList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		
		return new ResponseEntity<>(egLiecnseUiFieldInfoResponse, HttpStatus.OK);
	}
	
	
}
