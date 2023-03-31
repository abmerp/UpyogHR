package org.egov.tl.service.repo;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.egov.tl.web.models.RenewalLicense;
import org.egov.tl.web.models.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface RenewalLicenseRepo extends JpaRepository<RenewalLicense, Integer>{
	
	
	Optional<RenewalLicense> findById (Integer id);
	
	@Query("from RenewalLicense where applicationNumber=applicationNumber")
	List<RenewalLicense> findByApplicationNumber (String applicationNumber);

}
