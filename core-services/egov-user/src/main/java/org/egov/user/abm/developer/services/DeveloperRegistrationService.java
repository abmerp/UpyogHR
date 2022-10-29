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
	public DeveloperRegistration addDeveloperRegistraion(Long id, DevDetail detail) throws JsonProcessingException {
		List<Developerdetail> listDevDetails;
		Developerdetail objDeveloperdetail = new Developerdetail();
		DeveloperRegistration devRegistration;
		if (id != null && id > 0) {
			devRegistration = em.find(DeveloperRegistration.class, id, LockModeType.PESSIMISTIC_WRITE);

			devRegistration.setCurrentVersion(devRegistration.getCurrentVersion() + 0.1f);
			objDeveloperdetail.setVersion(devRegistration.getCurrentVersion() );
			objDeveloperdetail.setDevDetail(detail);
			listDevDetails = devRegistration.getDeveloperDetail();
			listDevDetails.add(objDeveloperdetail);
			
			devRegistration.setUpdatedDate(new Date());
		} else {
			listDevDetails = new ArrayList<Developerdetail>();

			objDeveloperdetail.setVersion(0.1f);
			objDeveloperdetail.setDevDetail(detail);
			listDevDetails.add(objDeveloperdetail);
			devRegistration = new DeveloperRegistration();
			devRegistration.setCreatedDate(new java.util.Date());
			devRegistration.setCurrentVersion(0.1f);
			devRegistration.setDeveloperDetail(listDevDetails);
			// devRegistration.setUpdatedDate(null);

		}

		return develloperRegistrationRepo.save(devRegistration);

	}

	public DeveloperRegistration getById(Long id, boolean isAllData) {
		// TO DO TO BE TESTED and FIXED
		DeveloperRegistration developerRegistration = develloperRegistrationRepo.findById(id);
		if (!isAllData) {
			for (int i = 0; i < developerRegistration.getDeveloperDetail().size(); i++) {
				if (developerRegistration.getCurrentVersion() == developerRegistration.getDeveloperDetail().get(i)
						.getVersion()) {

					developerRegistration
							.setDeveloperDetail(Arrays.asList(developerRegistration.getDeveloperDetail().get(i)));
				}
			}
		}
		return developerRegistration;
	}

	public List<DeveloperRegistration> findAllDeveloperDetail() {
		return this.develloperRegistrationRepo.findAll();
	}

}
