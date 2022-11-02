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

	@PostMapping(value = "/_SaveTransactionNumber")
	public ResponseEntity<Map> TransactionData(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("UserName", "manoj");
		map.put("MobNo", "7589489623");
		map.put("CaseTypeId", "1");
		map.put("AppTypeId", "1");
		map.put("ChargesTypeId", "34");
		map.put("TxnAmount", "200.23");
		map.put("LicenceFeeNla", "150.1");
		map.put("ScrutinyFeeNla", "50.2");
		map.put("PaymentMode", "101");
		map.put("PayAgreegator", "0300997");
		map.put("LcApplicantName", "DLF Ltd");
		map.put("LcPurpose", "TestPurpose");
		map.put("LcDevelopmentPlan", "GN");
		map.put("LcDistrict", "Gurugram");

		return partyAPiCall.saveTransactionData(map);

	}

	@PostMapping(value = "/_Dairynumber")
	public ResponseEntity<Map> DiaryNumber(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();

		return partyAPiCall.generateDiaryNumber(map);
	}

	@PostMapping(value = "/_CaseNumber")
	public ResponseEntity<Map> generateCaseNumber(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		return partyAPiCall.generateCaseNumber(map);
	}

	@PostMapping(value = "/_ApplicationNumber")
	public ResponseEntity<Map> generateApplicationNumber(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		return partyAPiCall.generateApplicationNumber(map);
	}

	@PostMapping(value = "/_SSOToken")
	public ResponseEntity<Map> isExistSSOToken(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		return partyAPiCall.isExistSSOToken(map);
	}
}
