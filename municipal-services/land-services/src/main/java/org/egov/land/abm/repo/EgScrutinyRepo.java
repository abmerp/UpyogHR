package org.egov.land.abm.repo;

import java.util.List;

import org.egov.land.abm.newservices.entity.EgScrutiny;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EgScrutinyRepo extends JpaRepository<EgScrutiny, Long>{

	public List<EgScrutiny> findByApplicationId(Integer applicationNumber);
}
