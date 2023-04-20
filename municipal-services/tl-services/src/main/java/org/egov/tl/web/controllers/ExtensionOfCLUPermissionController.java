package org.egov.tl.web.controllers;

import java.util.List;
import org.egov.tl.service.ExtensionOfCLUPermissionServices;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.ExtensionOfCLUPermission;
import org.egov.tl.web.models.ExtensionOfCLUPermissionRequest;
import org.egov.tl.web.models.ExtensionOfCLUPermissionResponse;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.SurrendOfLicense;
import org.egov.tl.web.models.SurrendOfLicenseRequest;
import org.egov.tl.web.models.SurrendOfLicenseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ExtensionOfCLUPermissionRequest")
public class ExtensionOfCLUPermissionController {

	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired
	private ExtensionOfCLUPermissionServices extensionOfCLUPermissionServices;

	@PostMapping("/_create")
	public ResponseEntity<ExtensionOfCLUPermissionResponse> create(
			@RequestBody ExtensionOfCLUPermissionRequest extensionOfCLUPermissionRequest) {

		List<ExtensionOfCLUPermission> extensionOfCLUPermission = extensionOfCLUPermissionServices
				.create(extensionOfCLUPermissionRequest);

		ExtensionOfCLUPermissionResponse extensionOfCLUPermissionResponse = ExtensionOfCLUPermissionResponse.builder()
				.extensionOfCLUPermission(extensionOfCLUPermission)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(extensionOfCLUPermissionRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(extensionOfCLUPermissionResponse, HttpStatus.OK);
	}

	@PostMapping("/_update")
	public ResponseEntity<ExtensionOfCLUPermissionResponse> update(
			@RequestBody ExtensionOfCLUPermissionRequest extensionOfCLUPermissionRequest) {

		List<ExtensionOfCLUPermission> extensionOfCLUPermission = extensionOfCLUPermissionServices
				.update(extensionOfCLUPermissionRequest);

//		List<SurrendOfLicense> SurrendOfLicenseList = new ArrayList<>();
//		SurrendOfLicenseList.add(surrendOfLicense);
		ExtensionOfCLUPermissionResponse extensionOfCLUPermissionResponse = ExtensionOfCLUPermissionResponse.builder()
				.extensionOfCLUPermission(extensionOfCLUPermission)
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
