package org.egov.user.abm.developer.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.user.abm.developer.contract.DevDetail;
import org.egov.user.abm.developer.contract.DeveloperRegistration;
import org.egov.user.abm.developer.contract.Developerdetail;
import org.egov.user.abm.developer.repo.DeveloperRegistrationRepo;
import org.egov.user.abm.developer.services.DeveloperRegistrationService;
import org.egov.user.domain.service.UserService;
import org.egov.user.web.contract.DeveloperDetailResponse;
import org.egov.user.web.contract.DeveloperResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/developer")
public class DeveloperRegistrationController {

	@Autowired
	DeveloperRegistrationService developerRegistrationService;
	@Autowired
	DeveloperRegistrationRepo developerRegistrationRepo;
	@Autowired
	UserService userService;

	@GetMapping("/_searchall")
	public List<DeveloperRegistration> searchDeveloperRegistraion() {

		return this.developerRegistrationService.findAllDeveloperDetail();

	}

	@GetMapping("/_getDeveloperById")
	public DeveloperDetailResponse getById(@RequestParam(value = "id") Long id, boolean isAllData) {
		DeveloperRegistration developerRegistration1 = developerRegistrationService.getById(id, isAllData);
		List<Developerdetail> listDevelopers = null;
		DeveloperDetailResponse response = null;
		if (developerRegistration1 != null && developerRegistration1.getDeveloperDetail().size() > 0) {
			listDevelopers = developerRegistration1.getDeveloperDetail();

			ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();

			if (isAllData) {
				response = new DeveloperDetailResponse(responseInfo, developerRegistration1.getId(),
						developerRegistration1.getCurrentVersion(), developerRegistration1.getCreatedBy().toString(),
						developerRegistration1.getCreatedDate(), developerRegistration1.getUpdateddBy().toString(),
						developerRegistration1.getUpdatedDate(), listDevelopers);
			} else {
				List<Developerdetail> listDevelopers1 = new ArrayList<Developerdetail>();
				if (listDevelopers != null) {
					for (Developerdetail developerdetail : listDevelopers) {
						if (developerdetail.getVersion() == developerRegistration1.getCurrentVersion()) {
							listDevelopers1.add(developerdetail);
							break;
						}

					}
					response = new DeveloperDetailResponse(responseInfo, developerRegistration1.getId(),
							developerRegistration1.getCurrentVersion(),
							developerRegistration1.getCreatedBy().toString(), developerRegistration1.getCreatedDate(),
							developerRegistration1.getUpdateddBy().toString(), developerRegistration1.getUpdatedDate(),
							listDevelopers1);
				}

			}

		} else {
			ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();
			response = new DeveloperDetailResponse();

			responseInfo.setResMsgId("No Record found");
			response.setResponseInfo(responseInfo);

		}
		return response;
	}

}
