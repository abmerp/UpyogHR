package org.egov.land.abm.service;

import java.util.List;

import org.egov.land.abm.models.EgLiecnseUiFieldInfoRequest;
import org.egov.land.abm.newservices.entity.EgLiecnseUiField;
import org.egov.land.repository.EgLicenseUiFieldRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EgLiecnseUiFieldServices {

	@Autowired EgLicenseUiFieldRepo egLicenseUiFieldRepo;
	
	public EgLiecnseUiField create(EgLiecnseUiFieldInfoRequest egLiecnseUiFieldInfoRequest) {		 
		return egLicenseUiFieldRepo.save(egLiecnseUiFieldInfoRequest.getEgLiecnseUiField());
	}
	
	public EgLiecnseUiField search(Integer id){
		return egLicenseUiFieldRepo.findById(id);
	}
}
