package org.egov.tl.repository.builder;

import java.util.List;
import java.util.Objects;

import org.egov.tl.config.TLConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NewBankGuaranteeQueryBuilder {
	
	@Autowired
	private TLConfiguration tlConfiguration;
	
	private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";
	
	private static final String QUERY = "select ebnbg.id as ebnbg_id, ebnbg.application_number as ebnbg_application_number, ebnbg.status as ebnbg_status, ebnbg.loi_number as ebnbg_loi_number, ebnbg.memo_number as ebnbg_memo_number, ebnbg.type_of_bg as ebnbg_type_of_bg, ebnbg.upload_bg as ebnbg_upload_bg, ebnbg.bank_name as ebnbg_bank_name, ebnbg.amount_in_fig as ebnbg_amount_in_fig, ebnbg.amount_in_words as ebnbg_amount_in_words, ebnbg.consent_letter as ebnbg_consent_letter, ebnbg.license_applied as ebnbg_license_applied, ebnbg.validity as ebnbg_validity, ebnbg.tenantid as ebnbg_tenantid, ebnbg.additionaldetails as ebnbg_additionaldetails, ebnbg.createdby as ebnbg_createdby, ebnbg.lastmodifiedby as ebnbg_lastmodifiedby, ebnbg.createdtime as ebnbg_createdtime, ebnbg.lastmodifiedtime as ebnbg_lastmodifiedtime from eg_bg_new_bank_guarantee ebnbg";
	
	public String getNewBankGuaranteeSearchQuery(String applicationNumber, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(QUERY);
		if(Objects.nonNull(applicationNumber)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebnbg.application_number=?");
			preparedStmtList.add(applicationNumber);
		}
		return builder.toString();
		
	}
	
	/**
	 * add if clause to the Statement if required or elese AND
	 * @param values
	 * @param queryString
	 */
	private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND");
		}
	}


}
