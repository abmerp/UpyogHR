package org.egov.land.abm.repo;

import org.egov.land.abm.newservices.entity.NewBankGuarantee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewBankGuaranteeRepo extends JpaRepository<NewBankGuarantee, Long>{

	public boolean existsByLoiNumber(String loiNumber);
	public NewBankGuarantee findByLoiNumber(String loiNumber);
	
}
