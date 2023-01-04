package org.egov.tl.service.repo;

import org.egov.tl.web.models.ServicePlan;
import org.egov.tl.web.models.ServicePlanRequest;
//import org.egov.land.abm.newservices.entity.ServicePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ServicePlanRepo extends JpaRepository<ServicePlan, String>{

	public ServicePlanRequest findByLoiNumber(String loiNumber);
}
