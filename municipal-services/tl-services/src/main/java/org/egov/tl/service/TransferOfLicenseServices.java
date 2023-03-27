package org.egov.tl.service;

import org.egov.tl.service.repo.TransferOfLicenseRepo;
import org.egov.tl.web.models.Transfer;
import org.egov.tl.web.models.TransferOfLicenseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferOfLicenseServices {
	
	@Autowired
	TransferOfLicenseRepo transferOfLicenseRepo;

	@SuppressWarnings("null")
	public Transfer create(TransferOfLicenseRequest transferOfLicenseRequest) {
		return transferOfLicenseRepo.save(transferOfLicenseRequest.getTransfer());

	}

	public Transfer search(Integer id) {
		return transferOfLicenseRepo.findById(id).get();

	}


}
