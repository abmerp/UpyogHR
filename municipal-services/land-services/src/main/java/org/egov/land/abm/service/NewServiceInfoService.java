package org.egov.land.abm.service;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.egov.land.abm.newservices.entity.NewServiceInfo;
import org.egov.land.abm.repo.NewServiceInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewServiceInfoService {

	@Autowired
	NewServiceInfoRepo newServiceInfoRepo;
	@Autowired EntityManager em;
	private long id = 1;

	@Transactional
	public NewServiceInfo createNewServic(NewServiceInfo newServiceInfo) {
		
		if(newServiceInfo.getId()!=null) {
			NewServiceInfo newServiceIn = em.find(NewServiceInfo.class, newServiceInfo.getId(),LockModeType.PESSIMISTIC_WRITE);
			for(int i =0;i<newServiceInfo.getNewServiceInfoData().size();i++) {
				//if()
				newServiceInfo.getNewServiceInfoData().get(i).setVer(newServiceIn.getCurrentVersion()+0.1f);
			}
			newServiceIn.setCurrentVersion(newServiceIn.getCurrentVersion()+0.1f);
			newServiceIn.getNewServiceInfoData().addAll(newServiceInfo.getNewServiceInfoData());
			return newServiceIn;
		}
		newServiceInfo.setCurrentVersion(0.0f);
		return newServiceInfoRepo.save(newServiceInfo);
	}

	public NewServiceInfo getNewServicesInfoById(Long id) {

		NewServiceInfo newServiceInfo = newServiceInfoRepo.getOne(id);
		System.out.println("new service info size : "+ newServiceInfo.getNewServiceInfoData().size());
		for(int i =0;i<newServiceInfo.getNewServiceInfoData().size();i++) {
			if(newServiceInfo.getCurrentVersion()==newServiceInfo.getNewServiceInfoData().get(i).getVer()){
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
