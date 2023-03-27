package org.egov.tl.service.repo;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.egov.tl.web.models.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface TransferOfLicenseRepo extends JpaRepository<Transfer, Integer>{
	
	
	Optional<Transfer> findById (Integer id);

}
