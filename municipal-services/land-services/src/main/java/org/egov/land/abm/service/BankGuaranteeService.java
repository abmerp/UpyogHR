package org.egov.land.abm.service;

import org.egov.land.abm.contract.NewBankGuaranteeContract;
import org.egov.land.abm.contract.RenewBankGuaranteeContract;
import org.egov.land.abm.newservices.entity.NewBankGuarantee;
import org.egov.land.abm.newservices.entity.RenewBankGuarantee;
import org.egov.land.abm.repo.NewBankGuaranteeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankGuaranteeService {

	@Autowired
	NewBankGuaranteeRepo newBankGuaranteeRepo;

	public NewBankGuarantee createAndUpdate(NewBankGuaranteeContract newBankGuaranteeContract) {

		boolean exists = newBankGuaranteeRepo
				.existsByLoiNumber(newBankGuaranteeContract.getNewBankGuaranteeRequest().getLoiNumber());
		if (!exists) {
			return newBankGuaranteeRepo.save(newBankGuaranteeContract.getNewBankGuaranteeRequest().toBuilder());
		} else {
			NewBankGuarantee newBankGuarantee = newBankGuaranteeRepo
					.findById(newBankGuaranteeContract.getNewBankGuaranteeRequest().getId()).get();
			newBankGuarantee.setAmount(newBankGuaranteeContract.getNewBankGuaranteeRequest().getAmount());
			newBankGuarantee.setAmountInWords(newBankGuaranteeContract.getNewBankGuaranteeRequest().getAmountInWords());
			newBankGuarantee.setBankName(newBankGuaranteeContract.getNewBankGuaranteeRequest().getBankName());
			newBankGuarantee.setLoiNumber(newBankGuaranteeContract.getNewBankGuaranteeRequest().getLoiNumber());
			newBankGuarantee.setMemoNumber(newBankGuaranteeContract.getNewBankGuaranteeRequest().getMemoNumber());
			newBankGuarantee.setTypeOfBg(newBankGuaranteeContract.getNewBankGuaranteeRequest().getTypeOfBg());
			newBankGuarantee.setValidity(newBankGuaranteeContract.getNewBankGuaranteeRequest().getValidity());
			return newBankGuaranteeRepo.save(newBankGuarantee);
		}

	}

	public NewBankGuarantee search(String loiNumber) {

		return this.newBankGuaranteeRepo.findByLoiNumber(loiNumber);
	}
	
	public RenewBankGuarantee createRenewBankGuarantee(RenewBankGuaranteeContract renewBankGuarantee) {
		
		
		return null;
	}
}
