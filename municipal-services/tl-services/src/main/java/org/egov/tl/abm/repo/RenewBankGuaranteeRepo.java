package org.egov.tl.abm.repo;

import org.egov.tl.abm.newservices.contract.RenewBankGuaranteeContract;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class RenewBankGuaranteeRepo {
	
	@Autowired
	private Producer producer;
	@Autowired
	private TLConfiguration tlConfiguration;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void save(RenewBankGuaranteeContract renewBankGuaranteeContract) {
		producer.push(tlConfiguration.getSaveRenewBankGuaranteeTopic(), renewBankGuaranteeContract);
	}

}
