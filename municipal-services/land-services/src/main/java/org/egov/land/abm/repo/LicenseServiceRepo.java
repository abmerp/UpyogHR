package org.egov.land.abm.repo;

import java.util.List;

import org.egov.land.abm.newservices.entity.LicenseServiceDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseServiceRepo extends JpaRepository<LicenseServiceDao, Long> {

	public boolean existsById(Long id);
	
	
	
	@Query(value="select n.id from LicenseServiceDao n")
	public List<String> getApplicantsNumber();
	
	@Query(value="select n from LicenseServiceDao n where n.loiNumber=?1")
	public LicenseServiceDao findByLoiNumber(String loiNumber);
	
	public boolean existsByLoiNumber(String loiNumber);
}
