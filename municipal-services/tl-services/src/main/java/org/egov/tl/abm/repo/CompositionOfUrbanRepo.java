package org.egov.tl.abm.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.TLRowMapper;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.CompletionCertificate;
import org.egov.tl.web.models.CompletionCertificateRequest;
import org.egov.tl.web.models.CompositionOfUrban;
import org.egov.tl.web.models.CompositionOfUrbanRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class CompositionOfUrbanRepo {

	@Autowired
	private Producer producer;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private TLConfiguration tlConfiguration;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	String querybyApplicationNumber="select * from public.eg_tl_composition_of_urban where application_number=:applicationNumber and application_status IN(1,2,3) \r\n"
			+ " order by created_date desc limit 1";
	
	public void save(CompositionOfUrbanRequest compositionOfUrbanRequest) {
		try {
	        producer.push(tlConfiguration.getSaveCompositionOfUrbanTopic(), compositionOfUrbanRequest);
		  }catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(CompositionOfUrbanRequest compositionOfUrbanRequest) {
		try {
	     	producer.push(tlConfiguration.getUpdateCompositionOfUrbanTopic(), compositionOfUrbanRequest);
	  	}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
   public CompositionOfUrban getCompositionOfUrbanByApplicationNumber(String applicationNumber) {
		
	   CompositionOfUrban compositionOfUrban=null;
		try {
		List<Object> preparedStmtList = new ArrayList<>();
		List<CompositionOfUrban> compositionOfUrbanList = jdbcTemplate.query(querybyApplicationNumber.replaceAll(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->{
			
			AuditDetails auditDetails=null;
			try {
				AuditDetails audit_details = new Gson().fromJson(
						rs.getString("audit_details").equals("{}") || rs.getString("audit_details").equals("null")
								? null
								: rs.getString("audit_details"),
								AuditDetails.class);
				System.out.println(audit_details);
				auditDetails=audit_details;
			}catch (Exception e) {
			   e.printStackTrace();
			}
			
			
			return CompositionOfUrban.builder().id(rs.getString("id"))
				.applicationNumber(rs.getString("application_number"))
				.applicationStatus(rs.getInt("application_status"))
				.auditDetails(auditDetails)
				.build();});
		if(compositionOfUrbanList!=null&&!compositionOfUrbanList.isEmpty()) {
			compositionOfUrban=compositionOfUrbanList.get(0);
		}
		}catch (Exception e) {
		  e.printStackTrace();
		}
	   
		return compositionOfUrban;
	}
	
	
	private CompositionOfUrban formateCompositionOfUrbanData(String query){
		CompositionOfUrban compositionOfUrban=null;
		try {
			List<CompositionOfUrban> compositionOfUrbanList=getCompositionOfUrbanList(query);
			if(compositionOfUrbanList!=null&&!compositionOfUrbanList.isEmpty()) {
				compositionOfUrban=compositionOfUrbanList.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return compositionOfUrban;
	}
	
	public List<CompositionOfUrban> getCompositionOfUrbanByApplicationNumberList(String applicationNumber){
		String query=querybyApplicationNumber.replace(":applicationNumber", "'"+applicationNumber+"'");
		return getCompositionOfUrbanList(query);
	}
	
	
	private List<CompositionOfUrban> getCompositionOfUrbanList(String query){
		List<CompositionOfUrban> compositionOfUrbanList=null;
		
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			List<CompositionOfUrban> compositionOfUrban = jdbcTemplate.query(query, preparedStmtList.toArray(),  (rs, rowNum) ->{
				AuditDetails auditDetails=null;
				List<Map<String,Object>> totalLandSoldInPart =null;
				try {
					AuditDetails audit_details = new Gson().fromJson(
							rs.getString("audit_details").equals("{}") || rs.getString("audit_details").equals("null")
									? null
									: rs.getString("audit_details"),
									AuditDetails.class);
					
					totalLandSoldInPart = new Gson().fromJson(
							rs.getString("totalLandSoldInPart").equals("{}") || rs.getString("audit_details").equals("null")
									? null
									: rs.getString("totalLandSoldInPart"),
									List.class);
					
					
					System.out.println(audit_details);
					auditDetails=audit_details;
				}catch (Exception e) {
				   e.printStackTrace();
				}
				
				return CompositionOfUrban.builder()
						.id(rs.getString("id"))
						.applicationStatus(rs.getInt("application_status"))
						.applicationNumber(rs.getString("application_number"))
						.workFlowCode(rs.getString("work_flow_code"))
						.auditDetails(auditDetails)
						.isDraft(rs.getString("is_draft"))
						.createdDate(rs.getTimestamp("created_date"))
						
						.nameOfOrginalLandOner(rs.getString("nameOfOrginalLandOner"))
						.landHoldingOfAbove(rs.getString("landHoldingOfAbove"))
						.totalAreaInSqMetter(rs.getString("totalAreaInSqMetter"))
						.totalLandSoldInPart(totalLandSoldInPart)
						.explainTheReasonForVoilation(rs.getString("explainTheReasonForVoilation"))
						.dateOfSaleDeeds(rs.getString("dateOfSaleDeeds"))
						.anyOtherDoc(rs.getString("anyOtherDoc"))
					.build();
			});
			if(compositionOfUrban!=null&&!compositionOfUrban.isEmpty()) {
				compositionOfUrbanList=compositionOfUrban;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return compositionOfUrbanList;
	}
	
}
