package org.egov.land.abm.repo;

import org.egov.land.abm.newservices.entity.RenewBankGuarantee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RenewBankGuaranteeRepo extends JpaRepository<RenewBankGuarantee, Long> {

}
