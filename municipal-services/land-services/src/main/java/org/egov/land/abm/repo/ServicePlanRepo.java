package org.egov.land.abm.repo;

import org.egov.land.abm.newservices.entity.ServicePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ServicePlanRepo extends JpaRepository<ServicePlan, String>{

	public ServicePlan findByLoiNumber(String loiNumber);
}
