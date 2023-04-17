package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.RevisedPlan;
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
public class RevisedLayoutPlanRowMapper implements ResultSetExtractor<List<RevisedPlan>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<RevisedPlan> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub

		List<RevisedPlan> revisedPlanList = new ArrayList<>();
		while (rs.next()) {
			RevisedPlan revisedPlan = new RevisedPlan();

			revisedPlan.setLicenseNo(rs.getString("licence_number"));
			revisedPlan.setReasonRevision(rs.getString("reasonrevision"));
			revisedPlan.setAnyOtherFeature(rs.getString("anyotherfeature"));
			revisedPlan.setAmount(rs.getBigDecimal("amount"));
			revisedPlan.setApplicationNumber(rs.getString("application_number"));
			revisedPlan.setAction(rs.getString("action"));
			revisedPlan.setBusinessService(rs.getString("businessservice"));
			revisedPlan.setExistingArea(rs.getString("existingarea"));
			revisedPlan.setId(rs.getString("id"));
			revisedPlan.setStatus(rs.getString("status"));
			revisedPlan.setTenantId(rs.getString("tenantid"));
			revisedPlan.setWorkflowCode(rs.getString("workflowcode"));
			revisedPlan.setAreaPlanning(rs.getString("areaplanning"));
			revisedPlan.setEarlierApprovedlayoutPlan(rs.getString("earlyapprovedlayoutplan"));
			revisedPlan.setTcpApplicationNumber(rs.getString("tcpapplicationnumber"));
			revisedPlan.setTcpCaseNumber(rs.getString("tcpcasenumber"));
			revisedPlan.setTcpDairyNumber(rs.getString("tcpdairynumber"));
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
				revisedPlan.setAdditionalDetails(additionalDetail);
			}

			AuditDetails auditDetails = new AuditDetails();

			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("createdby"))
					.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("lastmodifyby"))
					.lastModifiedTime(rs.getLong("lastmodifiedtime")).build();
			revisedPlan.setAuditDetails(auditDetails_build);
			revisedPlanList.add(revisedPlan);
		}

		return revisedPlanList;
	}

}
