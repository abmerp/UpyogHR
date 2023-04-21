package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.SurrendOfLicense;
import org.egov.tl.web.models.ZonePlan;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ZonePlanRowMapper implements ResultSetExtractor<List<ZonePlan>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<ZonePlan> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub

		List<ZonePlan> zonePlanList = new ArrayList<>();
		while (rs.next()) {
			ZonePlan zonePlan = new ZonePlan();

			zonePlan.setLicenseNo(rs.getString("license_no"));
			zonePlan.setId(rs.getString("id"));
			zonePlan.setCaseNumber(rs.getString("case_number"));
			zonePlan.setLayoutPlan(rs.getString("layout_plan"));
			zonePlan.setAnyotherDocument(rs.getString("anyother_document"));
			zonePlan.setAmount(rs.getString("amount"));
			zonePlan.setApplicationNumber(rs.getString("application_number"));
			zonePlan.setComment(rs.getString("comment"));

			zonePlan.setWorkflowCode(rs.getString("workflowcode"));
			zonePlan.setStatus(rs.getString("status"));
			zonePlan.setBusinessService(rs.getString("businessservice"));
			zonePlan.setTenantId(rs.getString("tenant_id"));
			zonePlan.setTcpApplicationNumber(rs.getString("tcpapplicationnumber"));
			zonePlan.setTcpCaseNumber(rs.getString("tcpcasenumber"));
			zonePlan.setTcpDairyNumber(rs.getString("tcpdairynumber"));
			PGobject pgObj = (PGobject) rs.getObject("additionaldetails");
			if (pgObj != null) {
				JsonNode additionalDetail = null;
				try {
					additionalDetail = mapper.readTree(pgObj.getValue());
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				zonePlan.setAdditionalDetails(additionalDetail);
			}

			AuditDetails auditDetails = new AuditDetails();

			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("created_by"))
					.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("lastModified_by"))
					.lastModifiedTime(rs.getLong("lastModified_time")).build();
			zonePlan.setAuditDetails(auditDetails_build);
			zonePlanList.add(zonePlan);
		}

		return zonePlanList;
	}

}
