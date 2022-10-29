package org.egov.land.abm.repo;

import java.util.List;

import org.egov.land.abm.newservices.entity.EgLiecnseUiFieldsRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EgLiecnseUiFieldsRoleRepo extends JpaRepository<EgLiecnseUiFieldsRole, Long> {

	List<EgLiecnseUiFieldsRole> findByRoleId(Integer roleId);
}
