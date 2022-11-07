package org.egov.land.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.egov.common.contract.request.User;
import org.egov.land.abm.service.NewServiceInfoService;
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
	
	@Autowired
	NewServiceInfoService newServiceInfoService;

	@PostMapping(value = "/getToken")
	public ResponseEntity<Map> token(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		return partyAPiCall.getAuthToken(map);
	}

	@PostMapping(value = "/_TransactionNumber")
	public ResponseEntity<Map> generateTransactionNo(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> authtoken  = new HashMap<String, Object>();
		return partyAPiCall.generateTransactionNumber(map,authtoken);
	}

	@PostMapping(value = "/_SaveTransactionNumber")
	public ResponseEntity<Map> TransactionData(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> authtoken  = new HashMap<String, Object>();
		return partyAPiCall.saveTransactionData(map,authtoken);

	}

	@PostMapping(value = "/_Dairynumber")
	public ResponseEntity<Map> DiaryNumber(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> authtoken  = new HashMap<String, Object>();

		return partyAPiCall.generateDiaryNumber(map,authtoken);
	}

	@PostMapping(value = "/_CaseNumber")
	public ResponseEntity<Map> generateCaseNumber(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> authtoken  = new HashMap<String, Object>();
		return partyAPiCall.generateCaseNumber(map,authtoken);
	}

	@PostMapping(value = "/_ApplicationNumber")
	public ResponseEntity<Map> generateApplicationNumber(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> authtoken  = new HashMap<String, Object>();
		return partyAPiCall.generateApplicationNumber(map,authtoken);
	}

	@PostMapping(value = "/_SSOToken")
	public ResponseEntity<Map> isExistSSOToken(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		return partyAPiCall.isExistSSOToken(map);
	}
	
	
	
	

	@PostMapping(value = "/_generate")
	public  Object generate(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		
		 newServiceInfoService.postTransactionDeatil(requestInfoWrapper.getApplicationid(), requestInfoWrapper.getRequestInfo().getUserInfo());
		 return null;	
	}
	
}
