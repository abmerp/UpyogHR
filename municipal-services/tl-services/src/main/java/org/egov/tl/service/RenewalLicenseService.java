package org.egov.tl.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.tl.abm.repo.RenewalLicenseServiceRepo;
import org.egov.tl.util.ConvertUtil;
import org.egov.tl.web.models.RenewalLicense;
import org.egov.tl.web.models.RenewalLicenseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RenewalLicenseService {
	
	@Autowired
	ObjectMapper mapper;
	
	@Value("${egov.timeZoneName}")
	private String timeZoneName;
	
	@Autowired
	RenewalLicenseServiceRepo renewalLicenseServiceRepo;
	
	
	public List<RenewalLicense> saveRenewalLicense(RenewalLicenseRequest renewalLicenseRequest) {
		Timestamp currentDate=Timestamp.valueOf(ConvertUtil.getCurrentFullDate(timeZoneName, null));
		List<RenewalLicense> renewalLicense = renewalLicenseRequest.getRenewalLicense().stream().map(renewallicense->{
			renewallicense.setCreatedAt(currentDate);
			return renewallicense;
		  }).collect(Collectors.toList());
		renewalLicenseRequest.setRenewalLicense(renewalLicense);
		renewalLicenseServiceRepo.saveRenewalLicense(renewalLicenseRequest);
		return renewalLicense;
	}
	
	public List<RenewalLicense> getRenewalLicense(String applicationNumber) {
		List<RenewalLicense> renewalLicense = null;
		try {
			List<RenewalLicense> renewalLicenseList = renewalLicenseServiceRepo.getRenewalLicense(applicationNumber);
			if(renewalLicenseList!=null&&!renewalLicenseList.isEmpty()) {
				renewalLicense=renewalLicenseList;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return renewalLicense;
	}

}
