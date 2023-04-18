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
public class ConstructionOfCommunityRepo {

	@Autowired
	private Producer producer;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private TLConfiguration tlConfiguration;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	String querybyLicenseNumber="select * from public.eg_tl_construction_Of_community where license_number=:licenseNumber and application_status IN(1,2,3) \r\n"
			+ " order by created_date desc limit 1";

	String querybyApplicationNumber="select * from public.eg_tl_construction_Of_community where application_number=:applicationNumber and application_status IN(1,2,3) \r\n"
			+ " order by created_date desc limit 1";
	
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
//					.id(rs.getString("id"))
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
				
				return ConstructionOfCommunity.builder()
//					.id(rs.getString("id"))
					.applicationStatus(rs.getInt("application_status"))
					.applicationNumber(rs.getString("application_number"))
					.workFlowCode(rs.getString("work_flow_code"))
					.auditDetails(auditDetails)
					.isDraft(rs.getString("is_draft"))
//					.tranactionId(rs.getString("transaction_id"))
					.licenseNumber(rs.getString("license_number"))
					.createdDate(rs.getTimestamp("created_date"))
					
//					.licenseValidTill(rs.getString("license_valid_till"))
//					.edcIdcBgValid(rs.getString("edc_idc_bg_valid"))
//					.statusOfComplainsIfAny(rs.getString("status_of_complains_if_any"))
//					.statusOfTotalComunity(rs.getString("status_of_total_comunity"))
//					.statusOfNplPlot(rs.getString("status_of_npl_plot"))
//					.statusOfHandlingOver(rs.getString("status_of_handling_over"))
//					.statusOfReadingHandlingOver(rs.getString("status_of_reading_handling_over"))
//					.handlingOverComunitySite(rs.getString("handling_over_comunity_site"))
//					.isCompleteProfileLessThen(rs.getInt("is_complete_profile_less_then"))
//					.caCertificate(rs.getString("ca_certificate"))
//					.iacAplicable(rs.getString("iac_aplicable"))
//					.statusOfComplainsForRules(rs.getString("status_of_complains_for_rules"))
//					.statusOfEDCisFullyPaid(rs.getString("status_ofedcis_fully_paid"))
//					.statusOfSIDCisFullyPaid(rs.getString("status_ofsidcis_fully_paid"))
//					.bgOnAccountTillValid(rs.getString("bg_on_account_till_valid"))
//					.paymentType(rs.getInt("payment_type"))
//					.applicationStatus(rs.getInt("application_status"))
//					.paidAmount(rs.getString("paid_amount"))
//					.isFullPaymentDone(rs.getBoolean("is_full_payment_done"))
//					.totalCompletionCertificateCharge(rs.getString("total_completion_certificate_charge"))
//					.copyApprovalServicePlan(rs.getString("copy_approval_service_plan"))
//					.electricServicePlan(rs.getString("electric_service_plan"))
//					.transferOfLicenseCertificate(rs.getString("transfer_of_license_certificate"))
//					.occupationCertificate(rs.getString("occupation_certificate"))
//					.updatedComplianceWithRules(rs.getString("updated_compliance_with_rules"))
//					.paymentAugmentationCharges(rs.getString("payment_augmentation_charges"))
//					.caCertificateRegarding15Percentage(rs.getString("ca_certificate_regarding15percentage"))
//					.statusOfDevelopmentWork(rs.getString("status_of_development_work"))
//					.completionApprovalLayoutPlan(rs.getString("completion_approval_layout_plan"))
//					.nocFromMOEF(rs.getString("noc_frommoef"))
//					.nocFromFairSafety(rs.getString("noc_from_fair_safety"))
//					.complianceOfRules(rs.getString("compliance_of_rules"))
//					.affidavitNoUnauthorized(rs.getString("affidavit_no_unauthorized"))
//					.complainsDetails(rs.getString("complains_details"))
//					.accessPermissionFromNHAI(rs.getString("access_permission_fromnhai"))
//					.tranactionId(rs.getString("transaction_id"))
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
