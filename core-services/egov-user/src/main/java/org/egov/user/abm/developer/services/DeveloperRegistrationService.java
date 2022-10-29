package org.egov.user.abm.developer.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.egov.user.abm.developer.contract.DevDetail;
import org.egov.user.abm.developer.contract.DeveloperRegistration;
import org.egov.user.abm.developer.contract.Developerdetail;
import org.egov.user.abm.developer.repo.DeveloperRegistrationRepo;
import org.egov.user.domain.model.Address;
import org.egov.user.domain.model.Role;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.enums.AddressType;
import org.egov.user.domain.model.enums.UserType;
import org.egov.user.domain.service.UserService;
import org.egov.user.web.contract.DeveloperRequest;
import org.egov.user.web.contract.DeveloperResponse;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class DeveloperRegistrationService {

	@Autowired
	DeveloperRegistrationRepo develloperRegistrationRepo;
	@Autowired
	EntityManager em;
	@Autowired
	UserService userService;

	@Transactional
	public DeveloperRegistration addDeveloperRegistraion(DeveloperRequest detail) throws JsonProcessingException {
		List<Developerdetail> listDevDetails;
		Developerdetail objDeveloperdetail = new Developerdetail();
		DeveloperRegistration devRegistration;
		if (detail.getId() != null && detail.getId() > 0) {
			devRegistration = em.find(DeveloperRegistration.class, detail.getId(), LockModeType.PESSIMISTIC_WRITE);

			devRegistration.setCurrentVersion(devRegistration.getCurrentVersion() + 0.1f);
			devRegistration.setUpdateddBy(detail.getUpdateddBy());
			devRegistration.setUpdatedDate(new Date());
			objDeveloperdetail.setVersion(devRegistration.getCurrentVersion());
			objDeveloperdetail.setDevDetail(detail.getDevDetail());
			listDevDetails = devRegistration.getDeveloperDetail();
			listDevDetails.add(objDeveloperdetail);

			devRegistration.setUpdatedDate(new Date());
		} else {
			listDevDetails = new ArrayList<Developerdetail>();

			objDeveloperdetail.setVersion(0.1f);
			objDeveloperdetail.setDevDetail(detail.getDevDetail());
			listDevDetails.add(objDeveloperdetail);
			devRegistration = new DeveloperRegistration();
			devRegistration.setCreatedBy(detail.getCreatedBy());
			devRegistration.setCreatedDate(new java.util.Date());
			devRegistration.setCurrentVersion(0.1f);
			devRegistration.setUpdateddBy(detail.getUpdateddBy());
			devRegistration.setUpdatedDate(new Date());
			devRegistration.setDeveloperDetail(listDevDetails);
			// devRegistration.setUpdatedDate(null);

		}

		return develloperRegistrationRepo.save(devRegistration);

	}

	/**
	 * 
	 * @param id
	 * @param isAllData
	 * @return
	 */
	public DeveloperRegistration getById(Long id, boolean isAllData) {

		DeveloperRegistration developerRegistration = develloperRegistrationRepo.findById(id);
		return developerRegistration;
	}

	public List<DeveloperRegistration> findAllDeveloperDetail() {
		return this.develloperRegistrationRepo.findAll();
	}

}
