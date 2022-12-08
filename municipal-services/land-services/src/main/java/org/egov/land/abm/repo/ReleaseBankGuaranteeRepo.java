package org.egov.land.abm.repo;

import org.egov.land.abm.newservices.entity.ReleaseBankGuarantee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseBankGuaranteeRepo extends JpaRepository<ReleaseBankGuarantee, Long>{

}
