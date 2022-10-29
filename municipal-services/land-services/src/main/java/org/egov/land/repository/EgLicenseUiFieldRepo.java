package org.egov.land.repository;

import org.egov.land.abm.newservices.entity.EgLiecnseUiField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EgLicenseUiFieldRepo extends JpaRepository<EgLiecnseUiField, Long>{

	public EgLiecnseUiField findById(Integer id);
}
