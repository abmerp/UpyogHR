package org.egov.tl.web.controllers;

import java.util.List;

import org.egov.tl.abm.newservices.contract.PerformaContract;
import org.egov.tl.abm.newservices.contract.PerformaResponse;
import org.egov.tl.service.PerformaService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.Performa;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.ServicePlanContract;
import org.egov.tl.web.models.ServicePlanInfoResponse;
import org.egov.tl.web.models.ServicePlanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/performaQuestions")
public class PerformaController {
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired
	private PerformaService performaService;

	@PostMapping("/_create")
	public ResponseEntity<PerformaResponse> create(@RequestBody PerformaContract performaContract) {
		List<Performa> performaList = performaService.create(performaContract);
		PerformaResponse performaResponse = PerformaResponse.builder().performa(performaList)
				.responseInfo(
						responseInfoFactory.createResponseInfoFromRequestInfo(performaContract.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(performaResponse, HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<PerformaResponse> getServicePlan(@RequestBody RequestInfoWrapper requestInfoWrapper,

			@RequestParam(required = false) String applicationNumber) {
		List<Performa> performaList = performaService.search(requestInfoWrapper.getRequestInfo(), applicationNumber);
		PerformaResponse performaResponse = PerformaResponse.builder().performa(performaList).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(performaResponse, HttpStatus.OK);
	}

}
