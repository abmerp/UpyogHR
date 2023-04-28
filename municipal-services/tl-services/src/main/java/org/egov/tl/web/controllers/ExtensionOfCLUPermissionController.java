package org.egov.tl.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.egov.tl.abm.newservices.contract.ExtensionOfCLUPermissionContract;
import org.egov.tl.service.ExtensionOfCLUPermissionServices;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.ExtensionOfCLUPermission;
import org.egov.tl.web.models.ExtensionOfCLUPermissionRequest;
import org.egov.tl.web.models.ExtensionOfCLUPermissionResponse;
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
@RequestMapping("/ExtensionOfCLUPermissionRequest")
public class ExtensionOfCLUPermissionController {

	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired
	private ExtensionOfCLUPermissionServices extensionOfCLUPermissionServices;

	@PostMapping("/_create")
	public ResponseEntity<ExtensionOfCLUPermissionResponse> create(
			@RequestBody ExtensionOfCLUPermissionRequest extensionOfCLUPermissionRequest) throws JsonProcessingException {

		ExtensionOfCLUPermission extensionOfCLUPermission = extensionOfCLUPermissionServices
				.create(extensionOfCLUPermissionRequest);
		List<ExtensionOfCLUPermission> extensionOfCLUPermissionList = new ArrayList<>();
		extensionOfCLUPermissionList.add(extensionOfCLUPermission);
		ExtensionOfCLUPermissionResponse extensionOfCLUPermissionResponse = ExtensionOfCLUPermissionResponse.builder()
				.extensionOfCLUPermission(extensionOfCLUPermissionList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(extensionOfCLUPermissionRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(extensionOfCLUPermissionResponse, HttpStatus.OK);
	}

	@PostMapping("/_update")
	public ResponseEntity<ExtensionOfCLUPermissionResponse> update(
			@RequestBody ExtensionOfCLUPermissionContract extensionOfCLUPermissionRequest) {

		List<ExtensionOfCLUPermission> extensionOfCLUPermissionList = extensionOfCLUPermissionServices
				.Update(extensionOfCLUPermissionRequest);
//		List<ExtensionOfCLUPermission> extensionOfCLUPermissionList = new ArrayList<>();
//		extensionOfCLUPermissionList.add(extensionOfCLUPermission);
		ExtensionOfCLUPermissionResponse extensionOfCLUPermissionResponse = ExtensionOfCLUPermissionResponse.builder()
				.extensionOfCLUPermission(extensionOfCLUPermissionList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(extensionOfCLUPermissionRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(extensionOfCLUPermissionResponse, HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<ExtensionOfCLUPermissionResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(value = "licenseNo", required = false) String licenceNumber,
			@RequestParam(value = "applicationNumber", required = false) String applicationNumber) {
		List<ExtensionOfCLUPermission> extensionOfCLUPermission = extensionOfCLUPermissionServices
				.search(requestInfoWrapper.getRequestInfo(), licenceNumber, applicationNumber);

		ExtensionOfCLUPermissionResponse extensionOfCLUPermissionResponse = ExtensionOfCLUPermissionResponse.builder()
				.extensionOfCLUPermission(extensionOfCLUPermission).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(extensionOfCLUPermissionResponse, HttpStatus.OK);
	}

}
