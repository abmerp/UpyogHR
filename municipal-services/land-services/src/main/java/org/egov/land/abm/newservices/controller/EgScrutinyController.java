package org.egov.land.abm.newservices.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egov.land.abm.contract.PerformaContract;
import org.egov.land.abm.models.EgScrutinyEmployeeReportResponse;
import org.egov.land.abm.models.EgScrutinyInfoRequest;
import org.egov.land.abm.models.EgScrutinyInfoResponse;
import org.egov.land.abm.models.EgScrutinyReportResponse;
import org.egov.land.abm.models.EmployeeSecurtinyReport;
import org.egov.land.abm.newservices.entity.EgScrutiny;
import org.egov.land.abm.newservices.entity.SecurityReport;
import org.egov.land.abm.service.EgScrutinyService;
import org.egov.land.abm.service.ThirPartyAPiCall;
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
	@Autowired ThirPartyAPiCall thirPartyAPiCall;

	@PostMapping("/_create")
	public ResponseEntity<EgScrutinyInfoResponse> createEgScrutiny(
			@RequestBody EgScrutinyInfoRequest egScrutinyInfoRequest,@RequestParam("status")String status) {
	
		EgScrutiny egScrutiny = egScrutinyService.createAndUpdateEgScrutiny(egScrutinyInfoRequest);

		List<EgScrutiny> egScrutinyList = new ArrayList<>();
		egScrutinyList.add(egScrutiny);
		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutinyList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(egScrutinyInfoRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}

	//@PostMapping("/_update")
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
			@RequestParam("applicationNumber") String applicationNumber,@RequestParam(value = "userId", required=false) Integer userid) {

		List<EgScrutiny> egScrutiny = this.egScrutinyService.search(applicationNumber,userid);
		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutiny)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}
	
	
	@PostMapping("/_search2")
	public ResponseEntity<EgScrutinyReportResponse> searchEgScrutiny2(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationNumber") String applicationNumber,@RequestParam(value = "userId", required=false) Integer userid) {

		List<SecurityReport> egScrutiny = this.egScrutinyService.search2(applicationNumber,userid);
		EgScrutinyReportResponse egScrutinyInfoResponse = EgScrutinyReportResponse.builder().egScrutiny(egScrutiny)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}
	@PostMapping("/_search3")
	public ResponseEntity<EgScrutinyEmployeeReportResponse> searchEgScrutiny3(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationNumber") String applicationNumber,@RequestParam(value = "userId", required=false) Integer userid) {

		List<EmployeeSecurtinyReport> egScrutiny = this.egScrutinyService.search3(applicationNumber,userid);
		EgScrutinyEmployeeReportResponse egScrutinyInfoResponse = EgScrutinyEmployeeReportResponse.builder().egScrutiny(egScrutiny)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}
	@PostMapping("/_search4")
	public ResponseEntity<EgScrutinyEmployeeReportResponse> searchEgScrutiny4(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationNumber") String applicationNumber,@RequestParam(value = "roles", required=false) List<String> roles) {

		List<EmployeeSecurtinyReport> egScrutiny = this.egScrutinyService.search4(applicationNumber,roles);
		EgScrutinyEmployeeReportResponse egScrutinyInfoResponse = EgScrutinyEmployeeReportResponse.builder().egScrutiny(egScrutiny)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}
	@PostMapping("/_search5")
	public ResponseEntity<EgScrutinyEmployeeReportResponse> searchEgScrutiny3(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationNumber") String applicationNumber) {

		List<EmployeeSecurtinyReport> egScrutiny = this.egScrutinyService.search5(applicationNumber);
		EgScrutinyEmployeeReportResponse egScrutinyInfoResponse = EgScrutinyEmployeeReportResponse.builder().egScrutiny(egScrutiny)
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
			@RequestParam("applicationId") String applicationId,
			@RequestParam("fieldId") String fieldId,
			@RequestParam("userId") Integer userId,
			@RequestParam("serviceId") Integer serviceId,
			@RequestParam("applicationStatus") String applicationStatus) {
		
		EgScrutiny egScrutiny = this.egScrutinyService.findByApplicationIdAndField_d(applicationId, fieldId, userId, serviceId,applicationStatus);
		List<EgScrutiny> egScrutinyList = new ArrayList<>();
		egScrutinyList.add(egScrutiny);

		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutinyList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}

	@PostMapping("/_searchbylogin")
	public ResponseEntity<EgScrutinyInfoResponse> findLogin(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationId") String applicatinNumber, @RequestParam("userid") Integer fieldId) {
		System.out.println("search by id ");
		List<EgScrutiny> egScrutiny = this.egScrutinyService.findByApplicationIdAndUserId(applicatinNumber, fieldId);

		System.out.println("egScrutiny date : " + egScrutiny.get(0).getTs());
		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutiny)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}
	@PostMapping("/_performa/_create")
	public ResponseEntity<EgScrutinyInfoResponse> createPerforma(
			@RequestBody PerformaContract performaContract,@RequestParam("status")String status) {
	
		List<EgScrutiny> egScrutinyList = egScrutinyService.createAndUpdatePerforma(performaContract);

		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutinyList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(performaContract.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}
	@PostMapping("/_performa/_update")
	public ResponseEntity<EgScrutinyInfoResponse> updatePerforma(
			@RequestBody PerformaContract performaContract) {
	
		List<EgScrutiny> egScrutinyList = egScrutinyService.createAndUpdatePerforma(performaContract);

		EgScrutinyInfoResponse egScrutinyInfoResponse = EgScrutinyInfoResponse.builder().egScrutiny(egScrutinyList)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(performaContract.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(egScrutinyInfoResponse, HttpStatus.OK);
	}

	
}
