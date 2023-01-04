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
import org.springframework.dao.DataAccessException;
//import org.junit.jupiter.params.shadow.com.univocity.parsers.common.ResultIterator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;


@Component
public class SPRowMapper implements ResultSetExtractor<List<ServicePlanRequest>>{

	
//	SELECT loi_number, auto_cad_file, certifiead_copy_of_the_plan, environmental_clearance,
//	self_certified_drawing_from_empaneled_doc, self_certified_drawings_from_chareted_eng,
//	shape_file_as_per_template, status, sp_action, undertaking, assignee, "action", 
//	business_service, "comment", tenantid, application_number
//	FROM public.eg_service_plan
//	WHERE loi_number='111111111111111111137';
	@Override
	public List<ServicePlanRequest> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub
		
		 Map<String, ServicePlanRequest> servicePlanRequestMap = new LinkedHashMap<>();
		
		List<ServicePlanRequest> servicePlanRequestList = new ArrayList<>();
		while (rs.next()) {
			String loi_number = rs.getString("loi_number");
			ServicePlanRequest servicePlanRequest = new ServicePlanRequest();
			servicePlanRequest.setLoiNumber(rs.getString("loi_number"));
			servicePlanRequest.setAutoCadFile(rs.getString("auto_cad_file"));
			servicePlanRequest.setCertifieadCopyOfThePlan(rs.getString("certifiead_copy_of_the_plan"));
			servicePlanRequest.setEnvironmentalClearance(rs.getString("environmental_clearance"));
			servicePlanRequest.setSelfCertifiedDrawingFromEmpaneledDoc(rs.getString("self_certified_drawing_from_empaneled_doc"));
			
			servicePlanRequest.setSelfCertifiedDrawingsFromCharetedEng(rs.getBoolean("self_certified_drawings_from_chareted_eng"));
			servicePlanRequest.setShapeFileAsPerTemplate(rs.getString("shape_file_as_per_template"));
			servicePlanRequest.setStatus(rs.getString("status"));
			servicePlanRequest.setAction(rs.getString("sp_action"));
			servicePlanRequest.setUndertaking(rs.getBoolean("undertaking"));
			
//			servicePlanRequest.setAssignee(rs.getString("assignee"));
//			servicePlanRequest.setAutoCadFile(rs.getString("action"));
//			servicePlanRequest.setCertifieadCopyOfThePlan(rs.getString("business_service"));
//			servicePlanRequest.setEnvironmentalClearance(rs.getString("comment"));
//			servicePlanRequest.setSelfCertifiedDrawingFromEmpaneledDoc(rs.getString("tenantid"));
//			servicePlanRequest.setSelfCertifiedDrawingFromEmpaneledDoc(rs.getString("application_number"));
			
			
			servicePlanRequest.setAction(rs.getString("action"));
			servicePlanRequest.setBusinessService(rs.getString("business_service"));
			servicePlanRequest.setComment(rs.getString("comment"));
			servicePlanRequest.setTenantID(rs.getString("tenantid"));
			servicePlanRequest.setApplicationNumber(rs.getString("application_number"));
			
			AuditDetails auditDetails = new  AuditDetails();
			
			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("created_by")).
			createdTime(rs.getLong("created_time"))
			.lastModifiedBy(rs.getString("last_modified_by"))
			.lastModifiedTime(rs.getLong("last_modified_time")).build();
					
					
					
			servicePlanRequest.setAuditDetails(auditDetails_build);
			
			
			
			
			servicePlanRequestList.add(servicePlanRequest);
			servicePlanRequestMap.put(loi_number, servicePlanRequest);
		}
		return servicePlanRequestList;
	}

}
