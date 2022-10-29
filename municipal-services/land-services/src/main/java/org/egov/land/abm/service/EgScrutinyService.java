package org.egov.land.abm.service;

import java.util.List;

import org.egov.land.abm.models.EgScrutinyInfoRequest;
import org.egov.land.abm.newservices.entity.EgScrutiny;
import org.egov.land.abm.repo.EgScrutinyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EgScrutinyService {

	@Autowired EgScrutinyRepo egScrutinyRepo;
	
	public EgScrutiny createEgScrutiny(EgScrutinyInfoRequest egScrutinyInfoRequest) {
		return egScrutinyRepo.save(egScrutinyInfoRequest.getEgScrutiny());
	}
	
	public List<EgScrutiny> search(Integer applicationNumber) {
		
		return this.egScrutinyRepo.findByApplicationId(applicationNumber);
	}
}
