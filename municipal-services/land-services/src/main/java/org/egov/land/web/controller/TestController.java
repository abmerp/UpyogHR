package org.egov.land.web.controller;

import javax.validation.Valid;

import org.egov.land.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

	@Autowired
	private org.egov.land.abm.service.ThirPartyAPiCall partyAPiCall;

	@PostMapping(value = "/getToken")
	public ResponseEntity<String> token(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {

			return partyAPiCall.getAuthToken();
	}
}
