package org.egov.land.abm.service;

import org.egov.land.abm.contract.ElectricPlanContract;
import org.egov.land.abm.newservices.entity.ElectricPlan;
import org.egov.land.abm.repo.ElectricPlanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElectricPlanService {

	@Autowired ElectricPlanRepo electricPlanRepo;
	
	
	public ElectricPlan createAndUpdateElectricPlan(ElectricPlanContract electricPlanContract) {
		return this.electricPlanRepo.save(electricPlanContract.getElectricPlanRequest().toBuilder());
	}
	
	public ElectricPlan searchElectricPlan(Long id ) {
		return this.electricPlanRepo.findById(id).get();
	}
	
}
