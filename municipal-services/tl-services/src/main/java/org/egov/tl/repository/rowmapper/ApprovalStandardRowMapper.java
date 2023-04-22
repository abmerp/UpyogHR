package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ServicePlanRequest;
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
public class ApprovalStandardRowMapper implements ResultSetExtractor<List<ApprovalStandardEntity>> {
	  @Autowired
	    private ObjectMapper mapper;
	@Override
	public List<ApprovalStandardEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub

		List<ApprovalStandardEntity> approvalStandardEntityList = new ArrayList<>();
		while (rs.next()) {
			ApprovalStandardEntity approvalStandardEntity = new ApprovalStandardEntity();
			
			approvalStandardEntity.setLicenseNo(rs.getString("license_no"));
			approvalStandardEntity.setStandardDrawingDesigns("standarddrawingdesigns");
			approvalStandardEntity.setAnyOtherDoc("anyotherdoc");	
			approvalStandardEntity.setApplicationNumber(rs.getString("application_number"));
			approvalStandardEntity.setAction(rs.getString("action"));
			approvalStandardEntity.setBusinessService(rs.getString("business_service"));
			approvalStandardEntity.setComment(rs.getString("comment"));
			approvalStandardEntity.setId(rs.getString("id"));
			approvalStandardEntity.setStatus(rs.getString("status"));
			approvalStandardEntity.setTenantId(rs.getString("tenantid"));
			approvalStandardEntity.setWorkflowCode(rs.getString("workflow_code"));
			approvalStandardEntity.setTcpApplicationNumber(rs.getString("tcpapplicationnumber"));
			approvalStandardEntity.setTcpCaseNumber(rs.getString("tcpcasenumber"));
			approvalStandardEntity.setTcpDairyNumber(rs.getString("tcpdairynumber"));
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
				approvalStandardEntity.setAdditionalDetails(additionalDetail);
			}
			AuditDetails auditDetails = new AuditDetails();

			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("created_by"))
					.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("last_modified_by"))
					.lastModifiedTime(rs.getLong("last_modified_time")).build();
			approvalStandardEntity.setAuditDetails(auditDetails_build);
			approvalStandardEntityList.add(approvalStandardEntity);
		}

		return approvalStandardEntityList;
	}

}
