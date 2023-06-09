package org.egov.tl.abm.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.TLRowMapper;
import org.egov.tl.util.ConvertUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.CompletionCertificate;
import org.egov.tl.web.models.CompletionCertificateRequest;
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
public class CompletionCertificateRepo {

	@Autowired
	private Producer producer;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private TLConfiguration tlConfiguration;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	String queryForgetTradeLicenseDetails = "select eg_tl_tradelicense.validFrom,eg_tl_tradelicense.validTo,eg_tl_tradelicensedetail.licensefeecharges,eg_tl_tradelicense.applicationnumber from eg_user inner join eg_tl_tradelicense on eg_user.uuid=eg_tl_tradelicense.createdBy "
			+ "inner join eg_tl_tradelicensedetail on eg_tl_tradelicensedetail.tradelicenseid = eg_tl_tradelicense.id "
			+ "where to_timestamp(eg_tl_tradelicense.validfrom / 1000)<CURRENT_TIMESTAMP(0) and CURRENT_TIMESTAMP(0)<to_timestamp(eg_tl_tradelicense.validto / 1000) and eg_tl_tradelicense.licensenumber=:licenseNumber";// and
																																																							// eg_tl_tradelicense.status!='INITIATED'
																																																							// //and
																																																							// eg_user.id=:userId

	String querybyLicenseNumber = "select * from public.eg_tl_completion_certificate where license_number IN(:licenseNumber) and application_status IN(1,2,3) \r\n"
			+ " order by created_date desc limit 1";

	String querybyApplicationNumber = "select * from public.eg_tl_completion_certificate where application_number IN(:applicationNumber) and application_status IN(1,2,3) \r\n"
			+ " order by created_date desc";

	String queryTlApplicationNumber = "select * from public.eg_tl_tradelicense";
	String queryCompletionApplicationNumber = "select * from public.eg_tl_completion_certificate";

	public void save(CompletionCertificateRequest CompletionCertificate) {
		try {
			producer.push(tlConfiguration.getSaveCompletionCertificateTopic(), CompletionCertificate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(CompletionCertificateRequest CompletionCertificate) {
		try {
			producer.push(tlConfiguration.getUpdateCompletionCertificateTopic(), CompletionCertificate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<TradeLicense> getLicenseByLicenseNumber(String applicationNumber, long userId) {
		List<TradeLicense> licenses = null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			licenses = jdbcTemplate
					.query(queryForgetTradeLicenseDetails.replace(":licenseNumber", "'" + applicationNumber + "'"),
							preparedStmtList.toArray(), (rs,
									rowNum) -> TradeLicense.builder()
											.validFrom(Long.parseLong(rs.getString("validFrom").toString()))
											.validTo(Long.parseLong(rs.getString("validTo").toString()))
											.applicationNumber(rs.getString("applicationnumber"))
											.tradeLicenseDetail(TradeLicenseDetail.builder()
													.licenseFeeCharges(rs.getDouble("licensefeecharges")).build())
											.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return licenses;
	}

	public CompletionCertificate getCompletionCertificateByLicenseNumber(String licenseNumber) {

		CompletionCertificate completionCertificate = null;
		try {
			String query=querybyLicenseNumber.replaceAll(":licenseNumber", "'" + licenseNumber + "'");
			List<CompletionCertificate> completionCertificateList = getCompletionCertificateList(query);
			if (completionCertificateList != null && !completionCertificateList.isEmpty()) {
				completionCertificate = completionCertificateList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return completionCertificate;
	}

//	public CompletionCertificate getBeneficialDetailsByApplicationNumber(String applicationNumber){
//		String query=querybyApplicationNumber.replace(":applicationNumber", "'"+applicationNumber+"'");
//		return formateCompletionCertificateData(query);
//	}
//	
//	public CompletionCertificate searcherCompletionCertificateByLicenceNumber(String licenseNumber){
//		String query=querybyLicenseNumber.replace(":licenseNumber", "'"+licenseNumber+"'");
//		return formateCompletionCertificateData(query);
//	}
//	

	private CompletionCertificate formateCompletionCertificateData(String query) {
		CompletionCertificate completionCertificate = null;
		try {
			List<CompletionCertificate> completionCertificateList = getCompletionCertificateList(query);
			if (completionCertificateList != null && !completionCertificateList.isEmpty()) {
				completionCertificate = completionCertificateList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return completionCertificate;
	}
	
	public List<CompletionCertificate> getAllRecords() {
		String query = querybyLicenseNumber.split("where")[0]+"  where action!='FINAL_APPROVAL'";
		return getCompletionCertificateList(query);
	}

	public List<CompletionCertificate> searcherBeneficialDetailsByLicenceNumberList(String licenseNumber) {
		String query = querybyLicenseNumber.replace(":licenseNumber", "'" + licenseNumber + "'");
		return getCompletionCertificateList(query);
	}

	public List<CompletionCertificate> getBeneficialDetailsByApplicationNumberList(String applicationNumber) {
		applicationNumber=ConvertUtil.splitAllApplicationNumber(applicationNumber);
		String query = querybyApplicationNumber.replace(":applicationNumber",applicationNumber);
		return getCompletionCertificateList(query);
	}

	private List<CompletionCertificate> getCompletionCertificateList(String query) {
		List<CompletionCertificate> completionCertificateList = null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			List<CompletionCertificate> completionCertificate = jdbcTemplate.query(query, preparedStmtList.toArray(),
					(rs, rowNum) -> {
						AuditDetails auditDetails = null;
						try {
							AuditDetails audit_details = new Gson().fromJson(rs.getString("audit_details").equals("{}")
									|| rs.getString("audit_details").equals("null") ? null
											: rs.getString("audit_details"),
									AuditDetails.class);
							System.out.println(audit_details);
							auditDetails = audit_details;
						} catch (Exception e) {
							e.printStackTrace();
						}
						JsonNode additionalDetails = null;
						
						try {
						PGobject pgObj1 = (PGobject) rs.getObject("newadditionaldetails");
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
						}catch (Exception e) {
							e.printStackTrace();
						}

						return CompletionCertificate.builder().id(rs.getString("id"))
								.applicationStatus(rs.getInt("application_status"))
								.applicationNumber(rs.getString("application_number"))
								.workFlowCode(rs.getString("work_flow_code")).auditDetails(auditDetails)
								.isDraft(rs.getString("is_draft")).tranactionId(rs.getString("transaction_id"))
								.licenseNumber(rs.getString("license_number"))
								.createdDate(rs.getTimestamp("created_date"))
								.licenseValidTill(rs.getString("license_valid_till"))
								.edcIdcBgValid(rs.getString("edc_idc_bg_valid"))
								.statusOfComplainsIfAny(rs.getString("status_of_complains_if_any"))
								.statusOfTotalComunity(rs.getString("status_of_total_comunity"))
								.statusOfNplPlot(rs.getString("status_of_npl_plot"))
								.statusOfHandlingOver(rs.getString("status_of_handling_over"))
								.statusOfReadingHandlingOver(rs.getString("status_of_reading_handling_over"))
								.handlingOverComunitySite(rs.getString("handling_over_comunity_site"))
								.isCompleteProfileLessThen(rs.getInt("is_complete_profile_less_then"))
								.caCertificate(rs.getString("ca_certificate"))
								.iacAplicable(rs.getString("iac_aplicable"))
								.statusOfComplainsForRules(rs.getString("status_of_complains_for_rules"))
								.statusOfEDCisFullyPaid(rs.getString("status_ofedcis_fully_paid"))
								.statusOfSIDCisFullyPaid(rs.getString("status_ofsidcis_fully_paid"))
								.bgOnAccountTillValid(rs.getString("bg_on_account_till_valid"))
								.paymentType(rs.getInt("payment_type"))
								.applicationStatus(rs.getInt("application_status"))
								.paidAmount(rs.getString("paid_amount"))
								.isFullPaymentDone(rs.getBoolean("is_full_payment_done"))
								.totalCompletionCertificateCharge(rs.getString("total_completion_certificate_charge"))
								.copyApprovalServicePlan(rs.getString("copy_approval_service_plan"))
								.electricServicePlan(rs.getString("electric_service_plan"))
								.transferOfLicenseCertificate(rs.getString("transfer_of_license_certificate"))
								.occupationCertificate(rs.getString("occupation_certificate"))
								.updatedComplianceWithRules(rs.getString("updated_compliance_with_rules"))
								.paymentAugmentationCharges(rs.getString("payment_augmentation_charges"))
								.caCertificateRegarding15Percentage(
										rs.getString("ca_certificate_regarding15percentage"))
								.statusOfDevelopmentWork(rs.getString("status_of_development_work"))
								.completionApprovalLayoutPlan(rs.getString("completion_approval_layout_plan"))
								.nocFromMOEF(rs.getString("noc_frommoef"))
								.nocFromFairSafety(rs.getString("noc_from_fair_safety"))
								.complianceOfRules(rs.getString("compliance_of_rules"))
								.affidavitNoUnauthorized(rs.getString("affidavit_no_unauthorized"))
								.complainsDetails(rs.getString("complains_details"))
								.accessPermissionFromNHAI(rs.getString("access_permission_fromnhai"))
								.tranactionId(rs.getString("transaction_id"))
								.newAdditionalDetails(additionalDetails)
								.action(rs.getString("action"))
								.tenantId(rs.getString("tenantid"))
								.businessService(rs.getString("businessservice"))
								.status(rs.getString("status"))
								
								.tcpApplicationNumber(rs.getString("tcp_application_number"))
								.tcpCaseNumber(rs.getString("tcp_case_number"))
								.tcpDairyNumber(rs.getString("tcp_dairy_number"))
								
								
								.build();
					});
			if (completionCertificate != null && !completionCertificate.isEmpty()) {
				completionCertificateList = completionCertificate;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return completionCertificateList;
	}

	// --------all service-----------//
	public List<TradeLicense> getApplication() {
		List<TradeLicense> licenses = null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			licenses = jdbcTemplate.query(queryTlApplicationNumber, preparedStmtList.toArray(),
					(rs, rowNum) -> TradeLicense.builder().applicationNumber(rs.getString("applicationnumber"))
							.licenseNumber(rs.getString("licensenumber")).tcpLoiNumber(rs.getString("tcploinumber"))
							.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return licenses;
	}

	public List<CompletionCertificate> getCompletionApplication() {
		List<CompletionCertificate> licenses = null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			licenses = jdbcTemplate.query(queryCompletionApplicationNumber, preparedStmtList.toArray(),
					(rs, rowNum) -> CompletionCertificate.builder()
							.applicationNumber(rs.getString("application_number"))
							.licenseNumber(rs.getString("license_number")).build());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return licenses;
	}

}
