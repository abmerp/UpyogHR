package org.egov.tl.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.egov.tl.abm.newservices.contract.PerformaScruitnyResponse;
import org.egov.tl.abm.newservices.contract.PerformaScrutinyContract;
import org.egov.tl.abm.newservices.entity.PerformaScruitny;
import org.egov.tl.service.PerformaScrutinyService;
import org.egov.tl.service.TransferOfLicenseServices;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.Transfer;
import org.egov.tl.web.models.TransferOfLicenseRequest;
import org.egov.tl.web.models.TransferOfLicenseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/_performaScrutiny")
public class PerformaScrutinyController {
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired
	private PerformaScrutinyService performaScrutinyService;

	@PostMapping("/_create")
	public ResponseEntity<PerformaScruitnyResponse> create(
			@RequestBody PerformaScrutinyContract performaScrutinyContract) throws JsonProcessingException {

		PerformaScruitny performaScruitny = performaScrutinyService.create(performaScrutinyContract);
		List<PerformaScruitny> performaScruitnyList = new ArrayList<>();
		performaScruitnyList.add(performaScruitny);
		PerformaScruitnyResponse performaScruitnyResponse = PerformaScruitnyResponse.builder()
				.performaScruitny(performaScruitnyList).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(performaScrutinyContract.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(performaScruitnyResponse, HttpStatus.OK);
	}
	@PostMapping("/_search")
	public ResponseEntity<PerformaScruitnyResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,	
			@RequestParam(value = "applicationNumber", required = false) String applicationNumber) {

		List<PerformaScruitny> performaScruitnyList = performaScrutinyService.search(requestInfoWrapper.getRequestInfo(),
				 applicationNumber);

		PerformaScruitnyResponse performaScruitnyResponse = PerformaScruitnyResponse.builder()
				.performaScruitny(performaScruitnyList).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(performaScruitnyResponse, HttpStatus.OK);
	}
	@PostMapping("/_update")
	public ResponseEntity<PerformaScruitnyResponse> update(@RequestBody PerformaScrutinyContract performaScrutinyContract) {

		PerformaScruitny performaScruitny = performaScrutinyService.update(performaScrutinyContract);
		List<PerformaScruitny> performaScruitnyList = new ArrayList<>();
		performaScruitnyList.add(performaScruitny);

		PerformaScruitnyResponse performaScruitnyResponse = PerformaScruitnyResponse.builder()
				.performaScruitny(performaScruitnyList).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(performaScrutinyContract.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(performaScruitnyResponse, HttpStatus.OK);
	}
}
