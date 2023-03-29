package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ServicePlanRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class ApprovalStandardRowMapper implements ResultSetExtractor<List<ApprovalStandardEntity>> {

	@Override
	public List<ApprovalStandardEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub

		List<ApprovalStandardEntity> approvalStandardEntityList = new ArrayList<>();
		while (rs.next()) {
			ApprovalStandardEntity approvalStandardEntity = new ApprovalStandardEntity();

			approvalStandardEntity.setLicenseNo(rs.getString("license_no"));
			approvalStandardEntity.setOtherDocument(rs.getString("other_document"));
			approvalStandardEntity.setPlan(rs.getString("plan"));
			approvalStandardEntity.setAmount(rs.getBigDecimal("amount"));
			approvalStandardEntity.setApplicationNumber(rs.getString("application_number"));
			approvalStandardEntity.setAction(rs.getString("action"));
			approvalStandardEntity.setBusinessService(rs.getString("business_service"));
			approvalStandardEntity.setComment(rs.getString("comment"));
			approvalStandardEntity.setId(rs.getString("id"));
			approvalStandardEntity.setStatus(rs.getString("status"));
			approvalStandardEntity.setTenantId(rs.getString("tenantid"));
			approvalStandardEntity.setWorkflowCode(rs.getString("workflow_code"));

			AuditDetails auditDetails = new AuditDetails();

			AuditDetails auditDetails_build = auditDetails.builder()
					.createdBy(rs.getString("created_by"))
					.createdTime(rs.getLong("created_time"))
					.lastModifiedBy(rs.getString("last_modified_by"))
					.lastModifiedTime(rs.getLong("last_modified_time")).build();
			approvalStandardEntity.setAuditDetails(auditDetails_build);
			approvalStandardEntityList.add(approvalStandardEntity);
		}

		return approvalStandardEntityList;
	}

}
