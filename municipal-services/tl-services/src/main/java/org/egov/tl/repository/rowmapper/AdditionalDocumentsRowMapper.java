package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.tl.web.models.AdditionalDocuments;
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
import com.google.gson.Gson;
@Component
public class AdditionalDocumentsRowMapper implements ResultSetExtractor<List<AdditionalDocuments>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<AdditionalDocuments> extractData(ResultSet rs) throws SQLException, DataAccessException {

		List<AdditionalDocuments> allServiceFindtList = new ArrayList<>();
		while (rs.next()) {

			AdditionalDocuments allServiceFind = new AdditionalDocuments();
			allServiceFind.setId(rs.getString("id"));
		//	allServiceFind.setLoiNumber(rs.getString("loi_number"));
			allServiceFind.setBusinessService(rs.getString("business_service"));
		//	allServiceFind.setApplicationNumber(rs.getString("application_number"));
			allServiceFind.setLicenceNumber(rs.getString("licence_number"));
			allServiceFind.setType(rs.getString("type"));
			Object additionalDetails = new Gson().fromJson(
					rs.getString("additional_details").equals("{}") || rs.getString("additional_details").equals("null")
							? null
							: rs.getString("additional_details"),
					Object.class);
			allServiceFind.setAdditionalDetails((additionalDetails));

			AuditDetails auditDetails = new AuditDetails();

			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("created_by"))
					.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("last_modify_by"))
					.lastModifiedTime(rs.getLong("last_modified_time")).build();

			allServiceFind.setAuditDetails(auditDetails_build);

			allServiceFindtList.add(allServiceFind);

		}
		return allServiceFindtList;
	}

}
