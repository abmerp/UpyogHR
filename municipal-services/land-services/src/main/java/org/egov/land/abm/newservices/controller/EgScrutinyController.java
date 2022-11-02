package org.egov.land.abm.newservices.controller;

import java.util.ArrayList;
import java.util.List;

import org.egov.land.abm.models.EgScrutinyInfoRequest;
import org.egov.land.abm.models.EgScrutinyInfoResponse;
import org.egov.land.abm.newservices.entity.EgScrutiny;
import org.egov.land.abm.service.EgScrutinyService;
import org.egov.land.util.ResponseInfoFactory;
import org.egov.land.web.models.LandInfoResponse;
import org.egov.land.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/egscrutiny")
public class EgScrutinyController {

	@Autowired
	EgScrutinyService egScrutinyService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<EgScrutinyInfoResponse> createEgScrutiny(
			@RequestBody EgScrutinyInfoRequest egScrutinyInfoRequest) {

		EgScrutiny egScrutiny = egScrutinyService.createAndUpdateEgScrutiny(egScrutinyInfoRequest);

		List<EgScrutiny> egScrutinyList = new ArrayList<>();
		egScrutinyList.add(egScrutiny);
		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutinyList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(egScrutinyInfoRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}

	@PostMapping("/_update")
	public ResponseEntity<EgScrutinyInfoResponse> updateEgScrutiny(
			@RequestBody EgScrutinyInfoRequest egScrutinyInfoRequest) {

		EgScrutiny egScrutiny = egScrutinyService.createAndUpdateEgScrutiny(egScrutinyInfoRequest);
		List<EgScrutiny> egScrutinyList = new ArrayList<>();
		egScrutinyList.add(egScrutiny);
		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutinyList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(egScrutinyInfoRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<EgScrutinyInfoResponse> searchEgScrutiny(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationNumber") Integer applicationNumber) {

		List<EgScrutiny> egScrutiny = this.egScrutinyService.search(applicationNumber);
		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutiny)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}

	@PostMapping("/_getById")
	public ResponseEntity<EgScrutinyInfoResponse> findByScrutinyId(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("id") Integer id) {

		EgScrutiny egScrutiny = this.egScrutinyService.findById(id);
		List<EgScrutiny> egScrutinyList = new ArrayList<>();
		egScrutinyList.add(egScrutiny);
		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutinyList)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}

	@GetMapping("/_searchbyfield")
	public ResponseEntity<EgScrutinyInfoResponse> findByApplicationIdAndFieldId(
			@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicatinNumber") Integer applicatinNumber, @RequestParam("fieldId") String fieldId) {
		System.out.println("search by id ");
		EgScrutiny egScrutiny = this.egScrutinyService.findByApplicationIdAndField_d(applicatinNumber, fieldId);
		List<EgScrutiny> egScrutinyList = new ArrayList<>();
		egScrutinyList.add(egScrutiny);

		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutinyList)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}

	@PostMapping("/_searchbylogin")
	public ResponseEntity<EgScrutinyInfoResponse> findLogin(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationId") Integer applicatinNumber, @RequestParam("userid") Integer fieldId) {
		System.out.println("search by id ");
		List<EgScrutiny> egScrutiny = this.egScrutinyService.findByApplicationIdAndUserId(applicatinNumber, fieldId);

		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutiny)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}
}
