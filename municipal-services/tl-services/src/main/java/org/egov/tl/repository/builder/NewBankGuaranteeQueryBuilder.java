package org.egov.tl.repository.builder;

import java.util.List;
import java.util.Objects;

import org.egov.tl.config.TLConfiguration;
import org.egov.tl.web.models.bankguarantee.BankGuaranteeSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NewBankGuaranteeQueryBuilder {
	
	@Autowired
	private TLConfiguration tlConfiguration;
	
	private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";
	
	private static final String QUERY = "select ebnbg.id as ebnbg_id, ebnbg.application_number as ebnbg_application_number, ebnbg.status as ebnbg_status, ebnbg.loi_number as ebnbg_loi_number, ebnbg.bg_number as ebnbg_bg_number, ebnbg.type_of_bg as ebnbg_type_of_bg, ebnbg.upload_bg as ebnbg_upload_bg, ebnbg.bank_name as ebnbg_bank_name, ebnbg.amount_in_fig as ebnbg_amount_in_fig, ebnbg.amount_in_words as ebnbg_amount_in_words, ebnbg.license_applied as ebnbg_license_applied, ebnbg.validity as ebnbg_validity, ebnbg.tenantid as ebnbg_tenantid, ebnbg.additionaldetails as ebnbg_additionaldetails, ebnbg.createdby as ebnbg_createdby, ebnbg.lastmodifiedby as ebnbg_lastmodifiedby, ebnbg.createdtime as ebnbg_createdtime, ebnbg.lastmodifiedtime as ebnbg_lastmodifiedtime ,ebnbg.hardcopy_Submitted_Document as ebnbg_hardcopy_Submitted_Document ,ebnbg.existing_Bg_Number as ebnbg_existing_Bg_Number  ,ebnbg.claim_Period as ebnbg_claim_Period ,ebnbg.origin_Country as ebnbg_origin_Country ,ebnbg.tcp_submission_received as ebnbg_tcp_submission_received ,ebnbg.indian_bank_advised_certificate as ebnbg_indian_bank_advised_certificate ,ebnbg.release_bank_guarantee as ebnbg_release_bank_guarantee, ebnbg.bank_Guarantee_Status as ebnbg_bank_Guarantee_Status, ebnbg.licence_Number as ebnbg_licence_Number, ebnbg.hardcopy_Submitted as ebnbg_hardcopy_Submitted, ebnbg.full_Certificate as ebnbg_full_Certificate, ebnbg.partial_Certificate as ebnbg_partial_Certificate, ebnbg.additional_Documents as ebnbg_additional_Documents, ebnbg.businessservice as ebnbg_businessservice,ebnbg.update_type as ebnbg_update_type,ebnbg.any_other_document as ebnbg_any_other_document,ebnbg.any_other_document_description  as ebnbg_any_other_document_description,ebnbg.application_cerficifate as ebnbg_application_cerficifate,ebnbg.application_cerficifate_description as ebnbg_application_cerficifate_description,ebnbg.bank_guarantee_replaced_with  as ebnbg_bank_guarantee_replaced_with,ebnbg.completion_certificate as ebnbg_completion_certificate,ebnbg.completion_certificate_description  as ebnbg_completion_certificate_description,ebnbg.reason_for_replacement as ebnbg_reason_for_replacement ,ebnbg.date_of_amendment as ebnbg_date_of_amendment,ebnbg.amendment_expiry_date as ebnbg_amendment_expiry_date,ebnbg.amendment_claim_expiry_date as ebnbg_amendment_claim_expiry_date,ebnbg.issuing_bank as ebnbg_issuing_bank,ebnbg.bank_gurentee_certificate as ebnbg_bank_gurentee_certificate,ebnbg.bank_gurentee_certificate_description as ebnbg_bank_gurentee_certificate_description,ebnbg.release as ebnbg_release  from eg_tl_bank_guarantee ebnbg";
	
	private static final String AUDIT_QUERY = "select ebnbg.id as ebnbg_id, ebnbg.application_number as ebnbg_application_number, ebnbg.status as ebnbg_status, ebnbg.loi_number as ebnbg_loi_number, ebnbg.bg_number as ebnbg_bg_number, ebnbg.type_of_bg as ebnbg_type_of_bg, ebnbg.upload_bg as ebnbg_upload_bg, ebnbg.bank_name as ebnbg_bank_name, ebnbg.amount_in_fig as ebnbg_amount_in_fig, ebnbg.amount_in_words as ebnbg_amount_in_words, ebnbg.license_applied as ebnbg_license_applied, ebnbg.validity as ebnbg_validity, ebnbg.tenantid as ebnbg_tenantid, ebnbg.additionaldetails as ebnbg_additionaldetails, ebnbg.createdby as ebnbg_createdby, ebnbg.lastmodifiedby as ebnbg_lastmodifiedby, ebnbg.createdtime as ebnbg_createdtime, ebnbg.lastmodifiedtime as ebnbg_lastmodifiedtime ,ebnbg.hardcopy_Submitted_Document as ebnbg_hardcopy_Submitted_Document ,ebnbg.existing_Bg_Number as ebnbg_existing_Bg_Number  ,ebnbg.claim_Period as ebnbg_claim_Period ,ebnbg.origin_Country as ebnbg_origin_Country ,ebnbg.tcp_submission_received as ebnbg_tcp_submission_received ,ebnbg.indian_bank_advised_certificate as ebnbg_indian_bank_advised_certificate ,ebnbg.release_bank_guarantee as ebnbg_release_bank_guarantee, ebnbg.bank_Guarantee_Status as ebnbg_bank_Guarantee_Status, ebnbg.licence_Number as ebnbg_licence_Number, ebnbg.hardcopy_Submitted as ebnbg_hardcopy_Submitted, ebnbg.full_Certificate as ebnbg_full_Certificate, ebnbg.partial_Certificate as ebnbg_partial_Certificate, ebnbg.additional_Documents as ebnbg_additional_Documents, ebnbg.businessservice as ebnbg_businessservice,ebnbg.update_type as ebnbg_update_type,ebnbg.any_other_document as ebnbg_any_other_document,ebnbg.any_other_document_description  as ebnbg_any_other_document_description,ebnbg.application_cerficifate as ebnbg_application_cerficifate,ebnbg.application_cerficifate_description as ebnbg_application_cerficifate_description,ebnbg.bank_guarantee_replaced_with  as ebnbg_bank_guarantee_replaced_with,ebnbg.completion_certificate as ebnbg_completion_certificate,ebnbg.completion_certificate_description  as ebnbg_completion_certificate_description,ebnbg.reason_for_replacement as ebnbg_reason_for_replacement ,ebnbg.date_of_amendment as ebnbg_date_of_amendment,ebnbg.amendment_expiry_date as ebnbg_amendment_expiry_date,ebnbg.amendment_claim_expiry_date as ebnbg_amendment_claim_expiry_date,ebnbg.issuing_bank as ebnbg_issuing_bank,ebnbg.bank_gurentee_certificate as ebnbg_bank_gurentee_certificate,ebnbg.bank_gurentee_certificate_description as ebnbg_bank_gurentee_certificate_description,ebnbg.release as ebnbg_release  from eg_tl_bank_guarantee_auditdetails ebnbg where ebnbg.application_number = ? order by ebnbg.lastmodifiedtime asc";
	
	public String getNewBankGuaranteeSearchQuery(BankGuaranteeSearchCriteria bankGuaranteeSearchCriteria,
			List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(QUERY);
		if (Objects.nonNull(bankGuaranteeSearchCriteria.getApplicationNumber())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebnbg.application_number in ("
					+ createQuery(bankGuaranteeSearchCriteria.getApplicationNumber()) + ")");
			addToPreparedStatement(preparedStmtList, bankGuaranteeSearchCriteria.getApplicationNumber());
		}
		if (!StringUtils.isEmpty(bankGuaranteeSearchCriteria.getLoiNumber())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebnbg.loi_number=?");
			preparedStmtList.add(bankGuaranteeSearchCriteria.getLoiNumber());
		}
		if (!StringUtils.isEmpty(bankGuaranteeSearchCriteria.getTypeOfBg())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebnbg.type_of_bg=?");
			preparedStmtList.add(bankGuaranteeSearchCriteria.getTypeOfBg());
		}
		if (!StringUtils.isEmpty(bankGuaranteeSearchCriteria.getBgNumber())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebnbg.bg_number=?");
			preparedStmtList.add(bankGuaranteeSearchCriteria.getBgNumber());
		}
		if (!StringUtils.isEmpty(bankGuaranteeSearchCriteria.getBankName())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebnbg.bank_name=?");
			preparedStmtList.add(bankGuaranteeSearchCriteria.getBankName());
		}
		if (!StringUtils.isEmpty(bankGuaranteeSearchCriteria.getExistingBgNumber())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebnbg.existing_Bg_Number=?");
			preparedStmtList.add(bankGuaranteeSearchCriteria.getExistingBgNumber());
		}
		if (!StringUtils.isEmpty(bankGuaranteeSearchCriteria.getLicenceNumber())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebnbg.licence_number=?");
			preparedStmtList.add(bankGuaranteeSearchCriteria.getLicenceNumber());
		}
		return builder.toString();

	}
	
	public String getBankGuaranteeAuditSearchQuery(String applicationNumber, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(AUDIT_QUERY);
		preparedStmtList.add(applicationNumber);
		return builder.toString();
	}
	
	/**
	 * add values to the preparedStatment List
	 * @param preparedStmtList
	 * @param ids
	 */
	private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
		ids.forEach(id -> {
			preparedStmtList.add(id);
		});

	}
	
	/**
	 * produce a query input for the multiple values
	 * @param ids
	 * @return
	 */
	private Object createQuery(List<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
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
