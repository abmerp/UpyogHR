package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.bankguarantee.NewBankGuaranteeRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class NewBankGuaranteeRowMapper implements ResultSetExtractor<List<NewBankGuaranteeRequest>>{

	@Override
	public List<NewBankGuaranteeRequest> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String,NewBankGuaranteeRequest> newBankGuaranteeRequestMap = new LinkedHashMap<>();
		while (rs.next()) {
			String id = rs.getString("ebnbg_id");
			NewBankGuaranteeRequest currentNewBankGuaranteeRequest= newBankGuaranteeRequestMap.get(id);
			if(currentNewBankGuaranteeRequest==null) {
				
				Object additionalDetails = new Gson().fromJson(rs.getString("ebnbg_additionaldetails").equals("{}")
						|| rs.getString("ebnbg_additionaldetails").equals("null") ? null
								: rs.getString("ebnbg_additionaldetails"),
						Object.class);
				Map<String, String> additionalDocuments = new Gson()
						.fromJson(rs.getString("ebnbg_additional_Documents").equals("{}")
								|| rs.getString("ebnbg_additional_Documents").equals("null") ? null
										: rs.getString("ebnbg_additional_Documents"),
								Map.class);
				
				AuditDetails auditDetails = AuditDetails.builder()
						.createdBy(rs.getString("ebnbg_createdby"))
						.lastModifiedBy(rs.getString("ebnbg_lastmodifiedby"))
						.createdTime(rs.getLong("ebnbg_createdtime"))
						.lastModifiedTime(rs.getLong("ebnbg_lastmodifiedtime"))
						.build();
						
				
				currentNewBankGuaranteeRequest=NewBankGuaranteeRequest.builder()
						.id(id)
						.applicationNumber(rs.getString("ebnbg_application_number"))
						.status(rs.getString("ebnbg_status"))
						.loiNumber(rs.getString("ebnbg_loi_number"))
						//.memoNumber(rs.getString("ebnbg_memo_number"))
						.bgNumber(rs.getString("ebnbg_bg_number"))
						.typeOfBg(rs.getString("ebnbg_type_of_bg"))
						.uploadBg(rs.getString("ebnbg_upload_bg"))
						.bankName(rs.getString("ebnbg_bank_name"))
						.amountInFig(rs.getBigDecimal("ebnbg_amount_in_fig"))
						.amountInWords(rs.getString("ebnbg_amount_in_words"))
						//.consentLetter(rs.getString("ebnbg_consent_letter"))
						.licenseApplied(rs.getString("ebnbg_license_applied"))
						.validity(rs.getString("ebnbg_validity"))
						.tenantId(rs.getString("ebnbg_tenantid"))
						.additionalDetails(additionalDetails)
						.auditDetails(auditDetails)
						.hardcopySubmittedDocument(rs.getString("ebnbg_hardcopy_Submitted_Document"))
						.existingBgNumber(rs.getString("ebnbg_existing_Bg_Number"))
						.claimPeriod(rs.getInt("ebnbg_claim_Period"))
						.originCountry(rs.getString("ebnbg_origin_Country"))
						.tcpSubmissionReceived(rs.getString("ebnbg_tcp_submission_received"))
						.indianBankAdvisedCertificate(rs.getString("ebnbg_indian_bank_advised_certificate"))
						.releaseBankGuarantee(rs.getString("ebnbg_release_bank_guarantee"))
						.bankGuaranteeStatus(rs.getString("ebnbg_bank_Guarantee_Status"))
						.licenceNumber(rs.getString("ebnbg_licence_Number"))
						.hardcopySubmittedDocument(rs.getString("ebnbg_hardcopy_Submitted"))
						.fullCertificate(rs.getString("ebnbg_full_Certificate"))
						.partialCertificate(rs.getString("ebnbg_partial_Certificate"))
						.additionalDocuments(additionalDocuments)
						.businessService(rs.getString("ebnbg_businessservice"))
					
						.updateType(rs.getString("ebnbg_update_type"))
						.status(rs.getString("ebnbg_status"))
						
						
						.anyOtherDocument(rs.getString("ebnbg_any_other_document"))
						.anyOtherDocumentDescription(rs.getString("ebnbg_any_other_document_description"))
						.dateOfAmendment(rs.getString("ebnbg_date_of_amendment"))
						.amendmentExpiryDate(rs.getString("ebnbg_amendment_expiry_date"))
						.amendmentClaimExpiryDate(rs.getString("ebnbg_amendment_claim_expiry_date"))
						.issuingBank(rs.getString("ebnbg_issuing_bank"))
						.bankGurenteeCertificate(rs.getString("ebnbg_bank_gurentee_certificate"))
						.bankGurenteeCertificateDescription(rs.getString("ebnbg_bank_gurentee_certificate_description"))
					
						.applicationCerficifate(rs.getString("ebnbg_application_cerficifate"))
						.applicationCerficifateDescription(rs.getString("ebnbg_application_cerficifate_description"))
						.bankGuaranteeReplacedWith(rs.getString("ebnbg_bank_guarantee_replaced_with"))
						.completionCertificate(rs.getString("ebnbg_completion_certificate"))
						.completionCertificateDescription(rs.getString("ebnbg_completion_certificate_description"))
						.reasonForReplacement(rs.getString("ebnbg_reason_for_replacement"))
						.release(rs.getString("ebnbg_release"))
						.build();
				newBankGuaranteeRequestMap.put(id, currentNewBankGuaranteeRequest);
			}
			
			
		}
		return new ArrayList<>(newBankGuaranteeRequestMap.values());
	}
}
