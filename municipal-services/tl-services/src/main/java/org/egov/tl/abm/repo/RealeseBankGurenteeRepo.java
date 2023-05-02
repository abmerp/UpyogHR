package org.egov.tl.abm.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.TLRowMapper;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.CompletionCertificate;
import org.egov.tl.web.models.CompletionCertificateRequest;
import org.egov.tl.web.models.RealeseBankGurenteeRequest;
import org.egov.tl.web.models.ReleaseBankGuarantee;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class RealeseBankGurenteeRepo {

	@Autowired
	private Producer producer;

	@Autowired
	private TLConfiguration tlConfiguration;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	String querybyBankGuaranteeNumber = "select * from public.eg_tl_release_bank_guarantee where bank_guarantee_number=:bankGuaranteeNumber order by created_at desc limit 1";

	public void save(RealeseBankGurenteeRequest realeseBankGurenteeRequest) {
		try {
			producer.push(tlConfiguration.getSaveRealeseBgTopic(), realeseBankGurenteeRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(RealeseBankGurenteeRequest realeseBankGurenteeRequest) {
		try {
			producer.push(tlConfiguration.getUpdateRealeseBgTopic(), realeseBankGurenteeRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ReleaseBankGuarantee getReleaseBankGuaranteeByBGNo(String bgNumber) {
		if(bgNumber==null)
			return null;

		ReleaseBankGuarantee releaseBankGuarantee = null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			List<ReleaseBankGuarantee> releaseBankGuaranteeList = jdbcTemplate.query(
					querybyBankGuaranteeNumber.replace(":bankGuaranteeNumber", "'" + bgNumber + "'")
					,
					preparedStmtList.toArray(), (rs, rowNum) -> {

						AuditDetails auditDetails = null;
						try {
							AuditDetails audit_details = new Gson().fromJson(rs.getString("audit_details").equals("{}")
									|| rs.getString("audit_details").equals("null") ? null
											: rs.getString("audit_details"),
									AuditDetails.class);
							System.out.println(audit_details);
							auditDetails = audit_details;
						} catch (Exception e) {
							e.printStackTrace();
						}

						return ReleaseBankGuarantee.builder().id(rs.getString("id"))
								.bankGuaranteeNumber(rs.getString("bank_guarantee_number"))
								.applicationStatus(rs.getInt("application_status"))
								.anyOtherDocument(rs.getString("any_other_document"))
								.anyOtherDocumentDescription(rs.getString("any_other_document_description"))
								.applicationCerficifate(rs.getString("application_cerficifate"))
								.applicationCerficifateDescription(rs.getString("application_cerficifate_description"))
								.bankGuaranteeIssueDate(rs.getString("bank_guarantee_issue_date"))
								.bankGuaranteeReplacedWith(rs.getString("bank_guarantee_replaced_with"))
								.bgAmount(rs.getString("bg_amount"))
								.claimExpiryDate(rs.getString("claim_expiry_date"))
								.completionCertificate(rs.getString("completion_certificate"))
								.completionCertificateDescription(rs.getString("completion_certificate_description"))
								.expiryDate(rs.getString("expiry_date"))
								.reasonForReplacement(rs.getString("reason_for_replacement"))
								.release(rs.getString("release"))
								.createdAt(rs.getTimestamp("created_at"))
								.auditDetails(auditDetails)
								.build();
					});
			if (releaseBankGuaranteeList != null && !releaseBankGuaranteeList.isEmpty()) {
				releaseBankGuarantee = releaseBankGuaranteeList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return releaseBankGuarantee;
	}
	
	public List<Map<String,Object>> getDropDownList() {
		List<Map<String,Object>> dropDownList = null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			dropDownList = jdbcTemplate.query("select * from eg_tl_bank_guarantee", preparedStmtList.toArray(),
					(rs, rowNum) -> {
						Map<String,Object> lst=new HashMap<>();
					    lst.put("application_number", rs.getString("application_number"));
					    lst.put("loi_number", rs.getString("loi_number"));
					    lst.put("licence_number", rs.getString("licence_number"));
						return lst;
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dropDownList;
	}


}
