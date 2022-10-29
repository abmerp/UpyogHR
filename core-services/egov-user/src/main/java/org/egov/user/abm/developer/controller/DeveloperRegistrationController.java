package org.egov.user.abm.developer.controller;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.user.abm.developer.entity.DevDetail;
import org.egov.user.abm.developer.entity.DeveloperInfo;
import org.egov.user.abm.developer.entity.DeveloperRegistration;
import org.egov.user.abm.developer.entity.Developerdetail;
import org.egov.user.abm.developer.repo.DeveloperRegistrationRepo;
import org.egov.user.abm.developer.services.DeveloperRegistrationService;
import org.egov.user.domain.model.User;
import org.egov.user.domain.service.UserService;
import org.egov.user.web.contract.CreateUserRequest;
import org.egov.user.web.contract.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

	@PostMapping("/_registration")
	public DeveloperRegistration createDeveloperRegistraion(@RequestBody Long id, DevDetail detail)
			throws JsonProcessingException {

		DeveloperRegistration developerRegistration1 = developerRegistrationService.addDeveloperRegistraion(id, detail);
		return developerRegistration1;

	}

	@GetMapping("/_searchall")
	public List<DeveloperRegistration> searchDeveloperRegistraion() {

		return this.developerRegistrationService.findAllDeveloperDetail();

	}

	@GetMapping("/_getDeveloperById")
	public DeveloperRegistration getById(@RequestParam(value = "id") Long id, boolean isAllData) {

		return developerRegistrationService.getById(id, isAllData);

	}

}
