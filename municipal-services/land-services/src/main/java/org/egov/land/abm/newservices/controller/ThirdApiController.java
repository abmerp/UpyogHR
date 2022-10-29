package org.egov.land.abm.newservices.controller;

import java.util.ArrayList;
import java.util.List;

import org.egov.land.abm.models.EgLiecnseUiFieldInfoRequest;
import org.egov.land.abm.models.EgLiecnseUiFieldInfoResponse;
import org.egov.land.abm.newservices.entity.EgLiecnseUiField;
import org.egov.land.abm.service.EgLiecnseUiFieldServices;
import org.egov.land.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class ThirdApiController {
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired EgLiecnseUiFieldServices egLiecnseUiFieldServices;
	
	
	@GetMapping("/api/EpaymentApi/AuthenticationToken")
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
	

}
