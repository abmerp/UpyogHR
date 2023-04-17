package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.TradeLicense;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
//import org.junit.jupiter.params.shadow.com.univocity.parsers.common.ResultIterator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Component
public class SPRowMapper implements ResultSetExtractor<List<ServicePlanRequest>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<ServicePlanRequest> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub

		Map<String, ServicePlanRequest> servicePlanRequestMap = new LinkedHashMap<>();

		List<ServicePlanRequest> servicePlanRequestList = new ArrayList<>();
		while (rs.next()) {
			String loi_number = rs.getString("loi_number");
			ServicePlanRequest servicePlanRequest = new ServicePlanRequest();
			servicePlanRequest.setId(rs.getString("id"));
			servicePlanRequest.setLoiNumber(rs.getString("loi_number"));
			servicePlanRequest.setAutoCadFile(rs.getString("auto_cad_file"));
			servicePlanRequest.setCertifieadCopyOfThePlan(rs.getString("certifiead_copy_of_the_plan"));
			servicePlanRequest.setEnvironmentalClearance(rs.getString("environmental_clearance"));
			servicePlanRequest
					.setSelfCertifiedDrawingFromEmpaneledDoc(rs.getString("self_certified_drawing_from_empaneled_doc"));

			servicePlanRequest
					.setSelfCertifiedDrawingsFromCharetedEng(rs.getString("self_certified_drawings_from_chareted_eng"));
			servicePlanRequest.setShapeFileAsPerTemplate(rs.getString("shape_file_as_per_template"));
			servicePlanRequest.setStatus(rs.getString("status"));
			servicePlanRequest.setAction(rs.getString("sp_action"));
			servicePlanRequest.setUndertaking(rs.getString("undertaking"));

			servicePlanRequest.setAction(rs.getString("action"));
			servicePlanRequest.setBusinessService(rs.getString("business_service"));
			servicePlanRequest.setComment(rs.getString("comment"));
			servicePlanRequest.setTenantID(rs.getString("tenantid"));
			servicePlanRequest.setApplicationNumber(rs.getString("application_number"));

			servicePlanRequest.setDevName(rs.getString("devname"));
			servicePlanRequest.setDevelopmentPlan(rs.getString("developmentPlan"));
			servicePlanRequest.setPurpose(rs.getString("purpose"));
			servicePlanRequest.setTotalArea(rs.getString("totalArea"));

			servicePlanRequest.setLayoutPlan(rs.getString("layoutplan"));
			servicePlanRequest.setRevisedLayout(rs.getString("revisedlayout"));
			servicePlanRequest.setDemarcation(rs.getString("demarcation"));
			servicePlanRequest.setDemarcationgis(rs.getString("demarcationgis"));
			servicePlanRequest.setLayoutExcel(rs.getString("layoutexcel"));
			servicePlanRequest.setAnyOtherdoc(rs.getString("anyotherdoc"));
			servicePlanRequest.setTcpApplicationNumber(rs.getString("tcpapplicationnumber"));
			servicePlanRequest.setTcpCaseNumber(rs.getString("tcpcasenumber"));
			servicePlanRequest.setTcpDairyNumber(rs.getString("tcpdairynumber"));

			PGobject pgObj = (PGobject) rs.getObject("externalagency");
			if (pgObj != null) {
				JsonNode externalAgency = null;
				try {
					externalAgency = mapper.readTree(pgObj.getValue());
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				servicePlanRequest.setExternalAgency(externalAgency);
			}

			Object additionalDetails = new Gson().fromJson(
					rs.getString("additionaldetails").equals("{}") || rs.getString("additionaldetails").equals("null")
							? null
							: rs.getString("additionaldetails"),
					Object.class);
			servicePlanRequest.setAdditionalDetails((additionalDetails));

			AuditDetails auditDetails = new AuditDetails();

			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("created_by"))
					.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("last_modified_by"))
					.lastModifiedTime(rs.getLong("last_modified_time")).build();

			servicePlanRequest.setAuditDetails(auditDetails_build);

			servicePlanRequestList.add(servicePlanRequest);
			servicePlanRequestMap.put(loi_number, servicePlanRequest);
		}
		return servicePlanRequestList;
	}

}
