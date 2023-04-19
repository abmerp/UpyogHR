package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.Performa;
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
import com.google.gson.Gson;

@Component
public class PerformaRowMapper implements ResultSetExtractor<List<Performa>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<Performa> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub

		
		List<Performa> performaList = new ArrayList<>();
		while (rs.next()) {

			Performa performa = new Performa();
			performa.setId(rs.getString("id"));
			performa.setBusinessService(rs.getString("business_service"));
			performa.setTenantId(rs.getString("tenant_id"));
			performa.setApplicationNumber(rs.getString("application_number"));
			PGobject pgObj = (PGobject) rs.getObject("additional_details");
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
				performa.setAdditionalDetail(additionalDetail);

			}
			
			AuditDetails auditDetails = new AuditDetails();

			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("created_by"))
					.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("last_modify_by"))
					.lastModifiedTime(rs.getLong("last_modified_time")).build();

			performa.setAuditDetails(auditDetails_build);

			performaList.add(performa);
			
		}
		return performaList;
	}

}
