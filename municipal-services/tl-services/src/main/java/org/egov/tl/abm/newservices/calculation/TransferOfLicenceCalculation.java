package org.egov.tl.abm.newservices.calculation;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.service.TradeLicenseService;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.Transfer;
import org.egov.tl.web.models.TransferOfLicence;
import org.egov.tl.web.models.TransferOfLicenseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
@Service
public class TransferOfLicenceCalculation {
	@Autowired
	ObjectMapper mapper;
	@Autowired
	TradeLicenseService tradeLicenseService;

	public Transfer calculationAfterApproval(TransferOfLicenseRequest transferOfLicenseRequest) {
		
		Transfer transferRequest = transferOfLicenseRequest.getTransfer();
		RequestInfo requestInfo = transferOfLicenseRequest.getRequestInfo();
		TransferOfLicence transferOfLicence = transferRequest.getTransferOfLicence();
		String licenceTransferredFromLandOwn = transferOfLicence.getLicenceTransferredFromLandOwn();
		String transferredTitleOfLand = transferOfLicence.getTransferredTitleOfLand();
		String yesValue = "yes";
		String noValue = "no";
		BigDecimal compostionCharges=null;
		BigDecimal developerAdministrativeCharges=null;
		String changeOfDeveloper = transferOfLicence.getChangeOfDeveloper();
		String changeOfDeveloperNo = "no";
		String changeOfDeveloperYes = "yes";
		TradeLicenseSearchCriteria tradeLicenseSearchCriteria = new TradeLicenseSearchCriteria();
		List<String> licenseNumberList = new ArrayList<>();
		licenseNumberList.add(transferOfLicenseRequest.getTransfer().getLicenseNo());
		tradeLicenseSearchCriteria.setLicenseNumbers(licenseNumberList);
		List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(tradeLicenseSearchCriteria,
				requestInfo);

		for (TradeLicense tradeLicense : tradeLicenses) {

			ObjectReader reader = mapper.readerFor(new TypeReference<List<LicenseDetails>>() {
			});

			List<LicenseDetails> newServiceInfoData = null;
			try {
				newServiceInfoData = reader.readValue(tradeLicense.getTradeLicenseDetail().getAdditionalDetail());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (LicenseDetails newobj : newServiceInfoData) {

				if (newobj.getVer() == tradeLicense.getTradeLicenseDetail().getCurrentVersion()) {
					String totalArea = newobj.getApplicantPurpose().getTotalArea();
					String zone = newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getPotential();
					BigDecimal licenceFee = newobj.getFeesAndCharges().getFeesTypeCalculationDto().get(0)
							.getTotalLicenceFee();
					if (licenceTransferredFromLandOwn.equalsIgnoreCase(yesValue)
							&& transferredTitleOfLand.equalsIgnoreCase(noValue)) {
						switch (zone) {
						case "Hyper":
							compostionCharges=new BigDecimal(100).multiply(new BigDecimal(totalArea.toString()));
							break;
						case "High":
							compostionCharges=new BigDecimal(100).multiply(new BigDecimal(totalArea.toString()));
							break;
						case "High Potential":
							compostionCharges=new BigDecimal(100).multiply(new BigDecimal(totalArea.toString()));
							break;
						case "Medium":
							compostionCharges=new BigDecimal(50).multiply(new BigDecimal(totalArea.toString()));
							break;
						case "Low":
							compostionCharges=new BigDecimal(25).multiply(new BigDecimal(totalArea.toString()));
							break;
						case "Low Potential":
							compostionCharges=new BigDecimal(25).multiply(new BigDecimal(totalArea.toString()));
							break;

						}
					}
					if (licenceTransferredFromLandOwn.equalsIgnoreCase(noValue)
							&& transferredTitleOfLand.equalsIgnoreCase(yesValue)) {
						switch (zone) {
						case "Hyper":
							compostionCharges=new BigDecimal(200).multiply(new BigDecimal(totalArea.toString()));
							break;
						case "High":
							compostionCharges=new BigDecimal(200).multiply(new BigDecimal(totalArea.toString()));
							break;
						case "High Potential":
							compostionCharges=new BigDecimal(200).multiply(new BigDecimal(totalArea.toString()));
							break;
						case "Medium":
							compostionCharges=new BigDecimal(100).multiply(new BigDecimal(totalArea.toString()));
							break;
						case "Low":
							compostionCharges=new BigDecimal(50).multiply(new BigDecimal(totalArea.toString()));
							break;
						case "Low Potential":
							compostionCharges=new BigDecimal(50).multiply(new BigDecimal(totalArea.toString()));
							break;
						}
					}
					if (changeOfDeveloper.equalsIgnoreCase(changeOfDeveloperYes)) {
						developerAdministrativeCharges = new BigDecimal(0.6)
								.multiply(new BigDecimal(0.25).multiply(licenceFee));
						developerAdministrativeCharges = new BigDecimal(
								developerAdministrativeCharges.setScale(0, BigDecimal.ROUND_UP).toString());

					}else if(changeOfDeveloper.equalsIgnoreCase(changeOfDeveloperNo)) {
						developerAdministrativeCharges = new BigDecimal(0);
					}
					BigDecimal totalCharges=null;
					totalCharges = compostionCharges.add(developerAdministrativeCharges);
					transferRequest.setCompostionCharges(compostionCharges);
					transferRequest.setDeveloperAdministrativeCharges(developerAdministrativeCharges);
					transferRequest.setTotalCharges(totalCharges);
				}
			}
			
		}
		return transferRequest;
	}
}
