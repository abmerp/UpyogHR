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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

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
			ElectricPlanRequest.setId(rs.getLong("id"));
			ElectricPlanRequest.setAutoCad(rs.getString("auto_cad"));
			ElectricPlanRequest.setElecricDistribution(rs.getBoolean("elecric_distribution"));
			ElectricPlanRequest.setEnvironmentalClearance(rs.getString("environmental_clearance"));
			ElectricPlanRequest.setElectricalCapacity(rs.getBoolean("electrical_capacity"));

			ElectricPlanRequest.setElectricalInfra(rs.getBoolean("electrical_infra"));
			ElectricPlanRequest.setLoadSancation(rs.getBoolean("load_sancation"));
			ElectricPlanRequest.setStatus(rs.getString("status"));
			ElectricPlanRequest.setSwitchingStation(rs.getBoolean("switching_station"));

			ElectricPlanRequest.setVerifiedPlan(rs.getString("verified_plan"));
			ElectricPlanRequest.setSelfCenteredDrawings(rs.getString("self_centred_drawing"));
			ElectricPlanRequest.setPdfFormat(rs.getString("pdf_format"));

			ElectricPlanRequest.setAction(rs.getString("action"));
			ElectricPlanRequest.setBusinessService(rs.getString("business_service"));
			ElectricPlanRequest.setComment(rs.getString("comment"));
			ElectricPlanRequest.setTenantID(rs.getString("tenantid"));
			ElectricPlanRequest.setApplicationNumber(rs.getString("application_number"));

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
