package org.egov.land.abm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.egov.land.abm.models.NewServiceInfoModel;
import org.egov.land.abm.newservices.entity.NewServiceInfo;
import org.egov.land.abm.newservices.pojo.NewServiceInfoData;
import org.egov.land.abm.repo.NewServiceInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewServiceInfoService {

	@Autowired
	NewServiceInfoRepo newServiceInfoRepo;
	@Autowired
	EntityManager em;
	private long id = 1;

	@Transactional
	public NewServiceInfo createNewServic(NewServiceInfoModel newServiceInfo) {

		List<NewServiceInfoData> newServiceInfoData;
		NewServiceInfo newServiceIn;
		List<NewServiceInfoData> newServiceInfoDatas;
		if (newServiceInfo.getId() != null && newServiceInfo.getId() > 0) {

			newServiceIn = em.find(NewServiceInfo.class, newServiceInfo.getId(), LockModeType.PESSIMISTIC_WRITE);

			newServiceInfoData = newServiceIn.getNewServiceInfoData();
			float cv = newServiceIn.getCurrentVersion() + 0.1f;
			for (NewServiceInfoData newobj : newServiceInfoData) {
				if (newobj.getVer() == newServiceIn.getCurrentVersion()) {
					if (newServiceInfo.getPageName().equalsIgnoreCase("ApplicantInfo")) {
						newobj.setApplicantInfo(newServiceInfo.getNewServiceInfoData().getApplicantInfo());

					}
					if (newServiceInfo.getPageName().equalsIgnoreCase("ApplicantPurpose")) {
						newobj.setApplicantPurpose(newServiceInfo.getNewServiceInfoData().getApplicantPurpose());
					}
					if (newServiceInfo.getPageName().equalsIgnoreCase("LandSchedule")) {
						newobj.setLandSchedule(newServiceInfo.getNewServiceInfoData().getLandSchedule());
					}
					if (newServiceInfo.getPageName().equalsIgnoreCase("DetailsofAppliedLand")) {
						newobj.setDetailsofAppliedLand(
								newServiceInfo.getNewServiceInfoData().getDetailsofAppliedLand());
					}
					if (newServiceInfo.getPageName().equalsIgnoreCase("FeesAndCharges")) {
						newobj.setFeesAndCharges(newServiceInfo.getNewServiceInfoData().getFeesAndCharges());
					}
					newobj.setVer(cv);
					newServiceIn.getNewServiceInfoData().add(newobj);
					break;
				}
			}

			newServiceIn.setUpdatedDate(new Date());
			newServiceIn.setUpdateddBy(newServiceInfo.getUpdateddBy());
			newServiceIn.setCurrentVersion(cv);

		} else {
			newServiceInfoDatas = new ArrayList<>();
			newServiceIn = new NewServiceInfo();
			newServiceIn.setCreatedBy(newServiceInfo.getCreatedBy());
			newServiceIn.setCreatedDate(new Date());
			newServiceIn.setUpdatedDate(new Date());
			newServiceInfo.getNewServiceInfoData().setVer(0.1f);
			newServiceIn.setUpdateddBy(newServiceInfo.getUpdateddBy());
			newServiceInfoDatas.add(newServiceInfo.getNewServiceInfoData());
			newServiceIn.setNewServiceInfoData(newServiceInfoDatas);
			newServiceIn.setCurrentVersion(0.1f);
		}
		return newServiceInfoRepo.save(newServiceIn);
	}

	public NewServiceInfo getNewServicesInfoById(Long id) {

		NewServiceInfo newServiceInfo = newServiceInfoRepo.getOne(id);
		System.out.println("new service info size : " + newServiceInfo.getNewServiceInfoData().size());
		for (int i = 0; i < newServiceInfo.getNewServiceInfoData().size(); i++) {
			if (newServiceInfo.getCurrentVersion() == newServiceInfo.getNewServiceInfoData().get(i).getVer()) {
				newServiceInfo.setNewServiceInfoData(Arrays.asList(newServiceInfo.getNewServiceInfoData().get(i)));
			}
		}
		return newServiceInfo;
	}

	public List<NewServiceInfo> getNewServicesInfoAll() {
		return newServiceInfoRepo.findAll();
	}

	public List<String> getApplicantsNumber() {
		return this.newServiceInfoRepo.getApplicantsNumber();
	}
}