package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.Transfer;
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
public class TransferRowMapper implements ResultSetExtractor<List<Transfer>> {
	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<Transfer> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub

		List<Transfer> transferList = new ArrayList<>();
		while (rs.next()) {
			Transfer transfer = new Transfer();

			transfer.setLicenseNo(rs.getString("license_no"));
			transfer.setAraeInAcres(rs.getString("area_in_acres"));
			transfer.setApplicationNumber(rs.getString("application_number"));
			transfer.setAction(rs.getString("action"));
			transfer.setBusinessService(rs.getString("businessservice"));

			transfer.setId(rs.getString("id"));
			transfer.setStatus(rs.getString("status"));
			transfer.setTenantId(rs.getString("tenant_id"));
			transfer.setTcpApplicationNumber(rs.getString("tcpapplicationnumber"));
			transfer.setTcpCaseNumber(rs.getString("tcpcasenumber"));
			transfer.setTcpDairyNumber(rs.getString("tcpdairynumber"));
			PGobject pgObj = (PGobject) rs.getObject("additiona_details");

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
				transfer.setAdditionalDetails(additionalDetail);
			}
			AuditDetails auditDetails = new AuditDetails();

			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("created_by"))
					.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("last_modify_by"))
					.lastModifiedTime(rs.getLong("last_modified_time")).build();
			transfer.setAuditDetails(auditDetails_build);
			transferList.add(transfer);
		}

		return transferList;
	}

}
