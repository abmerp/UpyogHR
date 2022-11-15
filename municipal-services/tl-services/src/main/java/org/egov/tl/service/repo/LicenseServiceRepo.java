package org.egov.tl.service.repo;

import java.util.List;

import org.egov.tl.service.dao.LicenseServiceDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseServiceRepo extends JpaRepository<LicenseServiceDao, Long> {

	public boolean existsById(Long id);
	
	
	
	@Query(value="select n.id from LicenseServiceDao n")
	public List<String> getApplicantsNumber();
}
