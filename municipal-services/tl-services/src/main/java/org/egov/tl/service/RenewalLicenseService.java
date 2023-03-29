package org.egov.tl.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.tl.abm.repo.RenewalLicenseServiceRepo;
import org.egov.tl.web.models.RenewalLicense;
import org.egov.tl.web.models.RenewalLicenseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RenewalLicenseService {
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	RenewalLicenseServiceRepo renewalLicenseServiceRepo;
	
	
	public void saveRenewalLicense(RenewalLicenseRequest renewalLicenseRequest) {
		JsonNode previopusCondition = mapper.valueToTree(renewalLicenseRequest.getRenewalLicenseDetails().getPreviopusCondition());
		List<RenewalLicense> renewalLicense = renewalLicenseRequest.getRenewalLicenseDetails().getRenewalLicense().stream().map(renewallicense->{
			renewallicense.setPreviouslyCondition_RL(previopusCondition);
			return renewallicense;
		  }).collect(Collectors.toList());
		renewalLicenseRequest.getRenewalLicenseDetails().setRenewalLicense(renewalLicense);
		
		renewalLicenseServiceRepo.saveRenewalLicense(renewalLicenseRequest);
	}

}
