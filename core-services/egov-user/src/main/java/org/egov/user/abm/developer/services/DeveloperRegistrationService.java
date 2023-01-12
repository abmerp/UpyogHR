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
		// Developerdetail objDeveloperdetail = new Developerdetail();
		DeveloperRegistration devRegistration=null;
		if (detail.getId() != null && detail.getId() > 0) {
			devRegistration = develloperRegistrationRepo.findByUser(detail.getId());
		}
			if (devRegistration != null && devRegistration.getDeveloperDetail()!=null) {
			listDevDetails = devRegistration.getDeveloperDetail();
			float cv = devRegistration.getCurrentVersion() + 0.1f;

			for (Developerdetail listDevDetail : listDevDetails) {

				if (listDevDetail.getVersion() == devRegistration.getCurrentVersion()) {
					switch (detail.getPageName()) {
					case "applicantType": {
						listDevDetail.setApplicantType(detail.getDevDetail().getApplicantType());
						break;
					}
					case "addInfo": {
						listDevDetail.setAddInfo(detail.getDevDetail().getAddInfo());
						break;
					}
					case "licenceDetails": {
						listDevDetail.setLicenceDetails(detail.getDevDetail().getLicenceDetails());
						break;
					}
					case "aurthorizedUserInfoArray": {
						listDevDetail.setAurthorizedUserInfoArray(detail.getDevDetail().getAurthorizedUserInfoArray());
						break;
					}
					case "capacityDevelopAColony": {
						listDevDetail.setCapacityDevelopAColony(detail.getDevDetail().getCapacityDevelopAColony());
						break;
					}
					}
					listDevDetail.setVersion(cv);
					devRegistration.getDeveloperDetail().add(listDevDetail);
					break;
				}
			}
			devRegistration.setCurrentVersion(cv);
			devRegistration.setUpdateddBy(detail.getUpdatedBy());
			devRegistration.setUpdatedDate(new Date());
			
		} else {
			listDevDetails = new ArrayList<Developerdetail>();

			detail.getDevDetail().setVersion(0.1f);

			listDevDetails.add(detail.getDevDetail());
			devRegistration = new DeveloperRegistration();
			devRegistration.setCreatedBy(Long.parseLong(detail.getCreatedBy()));
			devRegistration.setCreatedDate(new java.util.Date());
			devRegistration.setCurrentVersion(0.1f);
			devRegistration.setUpdateddBy(detail.getUpdatedBy());
			devRegistration.setUpdatedDate(new Date());
			devRegistration.setDeveloperDetail(listDevDetails);
			devRegistration.setUserId(Long.parseLong(detail.getCreatedBy()));
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

		return develloperRegistrationRepo.findByUser(id);
		
	}

	public List<DeveloperRegistration> findAllDeveloperDetail() {
		return this.develloperRegistrationRepo.findAll();
	}

}
