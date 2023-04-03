package org.egov.tl.abm.repo;

import java.util.List;

import javax.persistence.EntityManager;

import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.TLRowMapper;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.RenewalLicense;
import org.egov.tl.web.models.RenewalLicenseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository
public class RenewalLicenseServiceRepo {

	@Autowired
	private Producer producer;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private TLConfiguration tlConfiguration;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	org.egov.tl.service.repo.RenewalLicenseRepo renewalLicenseRepo;

	@Autowired
	private TLRowMapper rowMapper;

	public void saveRenewalLicense(RenewalLicenseRequest renewalLicenseRequest) {
		try {
//			renewalLicenseRepo.save(renewalLicenseRequest.getRenewalLicense().get(0));
		 	producer.push(tlConfiguration.getSaveRenewalLicenseTopic(), renewalLicenseRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<RenewalLicense> getRenewalLicense(String applicationNumber) {
		List<RenewalLicense>  renewalLicense=null;
		try {
			renewalLicense=renewalLicenseRepo.findByApplicationNumber(applicationNumber);
//		 	producer.push(tlConfiguration.getSaveRenewalLicenseTopic(), renewalLicenseRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return renewalLicense;
	}
}
