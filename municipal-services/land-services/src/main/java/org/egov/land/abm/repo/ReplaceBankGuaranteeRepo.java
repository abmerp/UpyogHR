package org.egov.land.abm.repo;

import org.egov.land.abm.newservices.entity.ReplaceBankGuarantee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplaceBankGuaranteeRepo extends JpaRepository<ReplaceBankGuarantee, Long>{

}
