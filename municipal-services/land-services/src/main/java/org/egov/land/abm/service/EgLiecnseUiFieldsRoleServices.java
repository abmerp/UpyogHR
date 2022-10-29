package org.egov.land.abm.service;

import java.util.List;

import org.egov.land.abm.models.EgLiecnseUiFieldsRoleRequest;
import org.egov.land.abm.newservices.entity.EgLiecnseUiFieldsRole;
import org.egov.land.abm.repo.EgLiecnseUiFieldsRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EgLiecnseUiFieldsRoleServices {

	@Autowired EgLiecnseUiFieldsRoleRepo egLiecnseUiFieldsRoleRepo;
	
	public EgLiecnseUiFieldsRole create(EgLiecnseUiFieldsRoleRequest egLiecnseUiFieldsRoleRequest) {
		//egLiecnseUiFieldsRoleRepo.
		return this.egLiecnseUiFieldsRoleRepo.save(egLiecnseUiFieldsRoleRequest.getEgLiecnseUiFieldsRole());
	}
	
	public List<EgLiecnseUiFieldsRole> search(){
		return this.egLiecnseUiFieldsRoleRepo.findAll();
	}
	
	public List<EgLiecnseUiFieldsRole> getByRoleId(Integer roleId){
		return this.egLiecnseUiFieldsRoleRepo.findByRoleId(roleId);
	}
}
