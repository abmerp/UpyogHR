package org.egov.tl.web.controllers;

import java.util.List;

import org.egov.tl.service.GetServices;
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
@RequestMapping("/_getServices")
public class GetServiceController {

	@Autowired
	GetServices getServices;

	@PostMapping("/_search")
	public ResponseEntity<List<String>> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(required = false) String type, @RequestParam(required = false) String businessService) {

		List<String> response = getServices.search(requestInfoWrapper.getRequestInfo(), type, businessService);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
