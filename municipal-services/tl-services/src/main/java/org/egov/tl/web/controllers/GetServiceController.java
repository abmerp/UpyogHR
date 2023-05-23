package org.egov.tl.web.controllers;

import java.util.List;

import org.egov.tl.service.GetServices;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.GetServiceResponse;
import org.egov.tl.web.models.RequestInfoWrapper;
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
@RequestMapping("/_getServices")
public class GetServiceController {

	@Autowired
	GetServices getServices;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@PostMapping("/_search")
	public ResponseEntity<GetServiceResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(required = false) String type, @RequestParam(required = false) String businessService) {

		List<String> response = getServices.search(requestInfoWrapper.getRequestInfo(), type, businessService);

		GetServiceResponse transferOfLicenseResponse = GetServiceResponse.builder().applicationNumbers(response)
				.responseinfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(transferOfLicenseResponse, HttpStatus.OK);
	}

}
