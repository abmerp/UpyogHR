package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.tl.web.models.AdditionalDocuments;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.Compliance;
import org.egov.tl.web.models.ComplianceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Component
public class ComplianceRowMapper implements ResultSetExtractor<List<ComplianceRequest>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<ComplianceRequest> extractData(ResultSet rs) throws SQLException, DataAccessException {

		List<ComplianceRequest> complianceRequestList = new ArrayList<>();
		while (rs.next()) {

			ComplianceRequest complianceRequest = new ComplianceRequest();
			complianceRequest.setId(rs.getString("id"));
			complianceRequest.setLoiNumber(rs.getString("loi_number"));
			complianceRequest.setBusinessService(rs.getString("business_service"));
			complianceRequest.setTcpAapplicationNumber(rs.getString("tcp_application_number"));

			Object additionalDetails = new Gson().fromJson(
					rs.getString("additional_details").equals("{}") || rs.getString("additional_details").equals("null")
							? null
							: rs.getString("additional_details"),
					Object.class);
			complianceRequest.setAdditionalDetails((additionalDetails));
			Compliance Compliance = new Compliance();
			List<Compliance> ComplianceList = new ArrayList<Compliance>();
			AuditDetails auditDetails = new AuditDetails();

			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("created_by"))
					.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("last_modify_by"))
					.lastModifiedTime(rs.getLong("last_modified_time")).build();

			complianceRequest.setAuditDetails(auditDetails_build);
			Compliance.setCompliance(rs.getString("compliance"));
			Compliance.setCreated_On(rs.getString("created_on"));
			Compliance.setDesignation(rs.getString("designation"));
			Compliance.setPartOfLoi(rs.getBoolean("ispartofloi"));
			Compliance.setUserId(rs.getString("userid"));
			Compliance.setUserName(rs.getString("username"));
		//	ComplianceList.add(Compliance);
			complianceRequest.setCompliance(Compliance);
			complianceRequestList.add(complianceRequest);

		}
		return complianceRequestList;
	}

}
