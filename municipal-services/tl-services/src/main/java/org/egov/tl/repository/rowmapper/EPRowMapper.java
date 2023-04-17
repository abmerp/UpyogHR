package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ElectricPlanRequest;
import org.egov.tl.web.models.ElectricPlanRequest;
import org.egov.tl.web.models.TradeLicense;
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
public class EPRowMapper implements ResultSetExtractor<List<ElectricPlanRequest>> {
	

	@Override
	public List<ElectricPlanRequest> extractData(ResultSet rs) throws SQLException, DataAccessException {

		Map<String, ElectricPlanRequest> ElectricPlanRequestMap = new LinkedHashMap<>();

		List<ElectricPlanRequest> electricPlanRequestList = new ArrayList<>();
		while (rs.next()) {
			String loi_number = rs.getString("loi_number");
			ElectricPlanRequest ElectricPlanRequest = new ElectricPlanRequest();
			ElectricPlanRequest.setLoiNumber(rs.getString("loi_number"));
			ElectricPlanRequest.setId(rs.getString("id"));
			ElectricPlanRequest.setAutoCad(rs.getString("auto_cad"));
			ElectricPlanRequest.setElectricDistribution(rs.getString("elecric_distribution"));
			ElectricPlanRequest.setEnvironmentalClearance(rs.getString("environmental_clearance"));
			ElectricPlanRequest.setElectricalCapacity(rs.getString("electrical_capacity"));

			ElectricPlanRequest.setElectricInfra(rs.getString("electrical_infra"));
			ElectricPlanRequest.setLoadSancation(rs.getString("load_sancation"));
			ElectricPlanRequest.setStatus(rs.getString("status"));
			ElectricPlanRequest.setSwitchingStation(rs.getString("switching_station"));

			ElectricPlanRequest.setVerifiedPlan(rs.getString("verified_plan"));
			ElectricPlanRequest.setSelfCenteredDrawings(rs.getString("self_centred_drawing"));
			ElectricPlanRequest.setPdfFormat(rs.getString("pdf_format"));

			ElectricPlanRequest.setAction(rs.getString("action"));
			ElectricPlanRequest.setBusinessService(rs.getString("business_service"));
			ElectricPlanRequest.setComment(rs.getString("comment"));
			ElectricPlanRequest.setTenantID(rs.getString("tenantid"));
			ElectricPlanRequest.setApplicationNumber(rs.getString("application_number"));
			
			ElectricPlanRequest.setDevName(rs.getString("devname"));
			ElectricPlanRequest.setDevelopmentPlan(rs.getString("developmentPlan"));
			ElectricPlanRequest.setPurpose(rs.getString("purpose"));
			ElectricPlanRequest.setTotalArea(rs.getString("totalArea"));
			ElectricPlanRequest.setTcpApplicationNumber(rs.getString("tcpapplicationnumber"));
			ElectricPlanRequest.setTcpCaseNumber(rs.getString("tcpcasenumber"));
			ElectricPlanRequest.setTcpDairyNumber(rs.getString("tcpdairynumber"));
			
			Object additionalDetails = new Gson().fromJson(rs.getString("additionaldetails").equals("{}")
					|| rs.getString("additionaldetails").equals("null")  ? null
							: rs.getString("additionaldetails"),
					Object.class);
			ElectricPlanRequest.setAdditionalDetails((additionalDetails));
			
		

			AuditDetails auditDetails = new AuditDetails();

			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("created_by"))
					.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("last_modified_by"))
					.lastModifiedTime(rs.getLong("last_modified_time")).build();

			ElectricPlanRequest.setAuditDetails(auditDetails_build);

			electricPlanRequestList.add(ElectricPlanRequest);
			ElectricPlanRequestMap.put(loi_number, ElectricPlanRequest);
		}
		return electricPlanRequestList;
	}

}
