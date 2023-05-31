package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.tl.abm.newservices.entity.PerformaScruitny;
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
public class PerformaScrutinyRowMapper implements ResultSetExtractor<List<PerformaScruitny>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<PerformaScruitny> extractData(ResultSet rs) throws SQLException, DataAccessException {

		List<PerformaScruitny> performaScruitnyList = new ArrayList<>();
		while (rs.next()) {

			PerformaScruitny performaScruitny = new PerformaScruitny();
			performaScruitny.setUserId(rs.getString("userid"));
			performaScruitny.setId(rs.getString("id"));
			performaScruitny.setApplicationStatus(rs.getString("applicationstatus"));
			performaScruitny.setApplicationNumber(rs.getString("applicationnumber"));
			performaScruitny.setDesignation(rs.getString("designation"));
			performaScruitny.setCreatedOn(rs.getString("created_on"));
			performaScruitny.setUserName(rs.getString("username"));

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
				performaScruitny.setAdditionalDetails(additionalDetail);
			}

			performaScruitnyList.add(performaScruitny);

		}
		return performaScruitnyList;
	}

}
