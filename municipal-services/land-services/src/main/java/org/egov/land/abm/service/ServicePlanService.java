package org.egov.land.abm.service;

import org.egov.land.abm.contract.ServicePlanContract;
import org.egov.land.abm.newservices.entity.ServicePlan;
import org.egov.land.abm.repo.ServicePlanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicePlanService {

	@Autowired ServicePlanRepo servicePlanRepo;
	
	public ServicePlan createAndUpdate(ServicePlanContract servicePlanContract) {
		return this.servicePlanRepo.save(servicePlanContract.getServicePlanRequest().toBuilder());
	}
	
	public ServicePlan searchServicePlan(String loiNumber) {
		return this.servicePlanRepo.findByLoiNumber(loiNumber);
	}
}
