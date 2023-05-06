package org.egov.tl.abm.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.TLRowMapper;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.CompletionCertificate;
import org.egov.tl.web.models.CompletionCertificateRequest;
import org.egov.tl.web.models.ConstructionOfCommunity;
import org.egov.tl.web.models.ConstructionOfCommunityRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ConstructionOfCommunityRepo {

	@Autowired
	private Producer producer;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private TLConfiguration tlConfiguration;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	String queryForgetTradeLicenseDetails="select eg_tl_tradelicense.validFrom,eg_tl_tradelicense.validTo,eg_tl_tradelicensedetail.licensefeecharges,eg_tl_tradelicense.applicationnumber from eg_user inner join eg_tl_tradelicense on eg_user.uuid=eg_tl_tradelicense.createdBy "
			+ "inner join eg_tl_tradelicensedetail on eg_tl_tradelicensedetail.tradelicenseid = eg_tl_tradelicense.id "
			+ "where eg_tl_tradelicense.licensenumber=:licenseNumber";//and  eg_tl_tradelicense.status!='INITIATED'  //and eg_user.id=:userId
	
	
	String querybyLicenseNumber="select * from public.eg_tl_construction_Of_community where license_number=:licenseNumber and application_status IN(1,2,3) \r\n"
			+ " order by created_date desc limit 1";

	String querybyApplicationNumber="select * from public.eg_tl_construction_Of_community where application_number=:applicationNumber and application_status IN(1,2,3) \r\n"
			+ " order by created_date desc limit 1";

	String queryApplicationNumber="select * from public.eg_tl_construction_Of_community";
	public void save(ConstructionOfCommunityRequest constructionOfCommunityRequest) {
		try {
	        producer.push(tlConfiguration.getSaveConstructionOfCommunityTopic(), constructionOfCommunityRequest);
		  }catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(ConstructionOfCommunityRequest constructionOfCommunityRequest) {
		try {
	     	producer.push(tlConfiguration.getUpdateConstructionOfCommunityTopic(), constructionOfCommunityRequest);
	  	}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<TradeLicense> getLicenseByLicenseNumber(String applicationNumber,long userId) {
		List<TradeLicense> licenses=null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			      licenses = jdbcTemplate.query(queryForgetTradeLicenseDetails.replace(":licenseNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->TradeLicense.builder()
					.validFrom(Long.parseLong(rs.getString("validFrom").toString()))
					.validTo(Long.parseLong(rs.getString("validTo").toString()))
					.applicationNumber(rs.getString("applicationnumber"))
					.tradeLicenseDetail(TradeLicenseDetail.builder().licenseFeeCharges(rs.getDouble("licensefeecharges")).build())
					.build());
			}catch (Exception e) {
			e.printStackTrace();
		}
		return licenses;
	}
	public List<ConstructionOfCommunity> getApplicationNumber() {
		List<ConstructionOfCommunity> licenses=null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			      licenses = jdbcTemplate.query(queryApplicationNumber, preparedStmtList.toArray(),  (rs, rowNum) ->ConstructionOfCommunity.builder()
					.licenseNumber(rs.getString("license_number"))
					.applicationNumber(rs.getString("application_number"))
					.build());
			}catch (Exception e) {
			e.printStackTrace();
		}
		return licenses;
	}
	
   public ConstructionOfCommunity getConstructionOfCommunityByLicenseNumber(String licenseNumber) {
		
	   ConstructionOfCommunity constructionOfCommunity=null;
		try {
		List<Object> preparedStmtList = new ArrayList<>();
		List<ConstructionOfCommunity> constructionOfCommunityList = jdbcTemplate.query(querybyLicenseNumber.replaceAll(":licenseNumber", "'"+licenseNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->{
			
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
			
			
			return ConstructionOfCommunity.builder()
				.id(rs.getString("id"))
				.applicationNumber(rs.getString("application_number"))
				.applicationStatus(rs.getInt("application_status"))
				.auditDetails(auditDetails)
				.build();});
		if(constructionOfCommunityList!=null&&!constructionOfCommunityList.isEmpty()) {
			constructionOfCommunity=constructionOfCommunityList.get(0);
		}
		}catch (Exception e) {
		  e.printStackTrace();
		}
	   
		return constructionOfCommunity;
	}
   
   public List<ConstructionOfCommunity> getAllRecords(){
 		String query=querybyLicenseNumber.split("where")[0];
 		return getConstructionOfCommunityList(query);
 	}

   public List<ConstructionOfCommunity> searcherConstructionOfCommunityDetailsByLicenceNumberList(String licenseNumber){
		String query=querybyLicenseNumber.replace(":licenseNumber", "'"+licenseNumber+"'");
		return getConstructionOfCommunityList(query);
	}
	
	public List<ConstructionOfCommunity> getConstructionOfCommunityDetailsByApplicationNumberList(String applicationNumber){
		String query=querybyApplicationNumber.replace(":applicationNumber", "'"+applicationNumber+"'");
		return getConstructionOfCommunityList(query);
	}
	
	
	private List<ConstructionOfCommunity> getConstructionOfCommunityList(String query){
		List<ConstructionOfCommunity> constructionOfCommunityList=null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			List<ConstructionOfCommunity> constructionOfCommunity = jdbcTemplate.query(query, preparedStmtList.toArray(),  (rs, rowNum) ->{
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
				PGobject pgObj1 = (PGobject) rs.getObject("newadditionaldetails");
				JsonNode additionalDetails = null;
				if (pgObj1 != null) {

					try {
						additionalDetails = mapper.readTree(pgObj1.getValue());
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				
				return ConstructionOfCommunity.builder()
					.id(rs.getString("id"))
					.applicationStatus(rs.getInt("application_status"))
					.applicationNumber(rs.getString("application_number"))
					.workFlowCode(rs.getString("work_flow_code"))
					.auditDetails(auditDetails)
					.isDraft(rs.getString("is_draft"))
					.licenseNumber(rs.getString("license_number"))
					.createdDate(rs.getTimestamp("created_date"))
					.appliedBy(rs.getString("applied_by"))
					.areaInAcers(rs.getString("area_in_acers"))
					.validUpTo(rs.getString("valid_up_to"))
					.applyedForExtentionPerioud(rs.getString("applyed_for_extention_perioud"))
					.typeOfCommunitySite(rs.getString("type_of_community_site"))
					
					.copyOfBoardResolution(rs.getString("copy_of_board_resolution"))
					.justificationForExtention(rs.getString("justification_for_extention"))
					.proofOfOwnershipOfCommunity(rs.getString("proof_of_ownership_of_community"))
					.proofOfOnlinePaymentOfExtention(rs.getString("proof_of_online_payment_of_extention"))
					.uploadRenewalLicenseCopy(rs.getString("upload_renewal_license_copy"))
					.explonatoryNotForExtention(rs.getString("explonatory_not_for_extention"))
					.locationOfApplied(rs.getString("location_of_applied"))
					.anyOtherDocumentByDirector(rs.getString("any_other_document_by_director"))
					.newAdditionalDetails(additionalDetails)			
					.build();
			});
			if(constructionOfCommunity!=null&&!constructionOfCommunity.isEmpty()) {
				constructionOfCommunityList=constructionOfCommunity;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return constructionOfCommunityList;
	}
}
