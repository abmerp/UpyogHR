package org.egov.land.abm.repo;

import java.util.Optional;

import org.egov.land.abm.newservices.entity.ElectricPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricPlanRepo extends JpaRepository<ElectricPlan, Long>{

	public Optional<ElectricPlan> findById(Long id);
}
