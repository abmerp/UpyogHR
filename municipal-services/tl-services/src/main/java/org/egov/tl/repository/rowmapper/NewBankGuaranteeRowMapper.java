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
						.memoNumber(rs.getString("ebnbg_memo_number"))
						.typeOfBg(rs.getString("ebnbg_type_of_bg"))
						.uploadBg(rs.getString("ebnbg_upload_bg"))
						.bankName(rs.getString("ebnbg_bank_name"))
						.amountInFig(rs.getString("ebnbg_amount_in_fig"))
						.amountInWords(rs.getString("ebnbg_amount_in_words"))
						.consentLetter(rs.getString("ebnbg_consent_letter"))
						.licenseApplied(rs.getString("ebnbg_license_applied"))
						.validity(rs.getString("ebnbg_validity"))
						.tenantId(rs.getString("ebnbg_tenantid"))
						.additionalDetails(additionalDetails)
						.auditDetails(auditDetails)
						.build();
				newBankGuaranteeRequestMap.put(id, currentNewBankGuaranteeRequest);
			}
			
			
		}
		return new ArrayList<>(newBankGuaranteeRequestMap.values());
	}
}
