package org.egov.land.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.egov.land.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	public ResponseEntity<Map> token(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {

			return partyAPiCall.getAuthToken();
	}
	@PostMapping(value = "/_TransactionNumber")
	public ResponseEntity<Map> generateTransactionNo(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("MobNo", "7589489623");
		map.put("CaseTypeId", "1");
				map.put("AppTypeId", "1");
						map.put("ChargesTypeId", "34");
		return partyAPiCall.generateTransactionNumber(map);
}
}
