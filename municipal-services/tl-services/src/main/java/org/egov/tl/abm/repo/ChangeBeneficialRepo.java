package org.egov.tl.abm.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.TLRowMapper;
import org.egov.tl.util.ConvertUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.ChangeBeneficialRequest;
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
public class ChangeBeneficialRepo {

	@Autowired
	private Producer producer;

	@Autowired
	private ObjectMapper mapper;
//	
//	@Autowired
//	private EntityManager entityManager;

	@Autowired
	private TLConfiguration tlConfiguration;

	@Autowired
	private JdbcTemplate jdbcTemplate;

//	@Autowired
//	private TLRowMapper rowMapper;

	String getLicenseQuery = "select eg_tl_tradelicense.validFrom,eg_tl_tradelicense.validTo,eg_tl_tradelicensedetail.licensefeecharges  from eg_user inner join eg_tl_tradelicense on eg_user.uuid=eg_tl_tradelicense.createdBy "
			+ "inner join eg_tl_tradelicensedetail on eg_tl_tradelicensedetail.tradelicenseid = eg_tl_tradelicense.id "
			+ "where to_timestamp(eg_tl_tradelicense.validfrom / 1000)<CURRENT_TIMESTAMP(0) and CURRENT_TIMESTAMP(0)<to_timestamp(eg_tl_tradelicense.validto / 1000) ";// and
																																										// eg_tl_tradelicense.status!='INITIATED'";//
																																										// and
																																										// eg_user.id=:userId";

	String queryForgetTradeLicenseDetails = "select eg_tl_tradelicense.validFrom,eg_tl_tradelicense.validTo,eg_tl_tradelicensedetail.licensefeecharges,eg_tl_tradelicense.applicationnumber from eg_user inner join eg_tl_tradelicense on eg_user.uuid=eg_tl_tradelicense.createdBy "
			+ "inner join eg_tl_tradelicensedetail on eg_tl_tradelicensedetail.tradelicenseid = eg_tl_tradelicense.id "
			+ "where to_timestamp(eg_tl_tradelicense.validfrom / 1000)<CURRENT_TIMESTAMP(0) and CURRENT_TIMESTAMP(0)<to_timestamp(eg_tl_tradelicense.validto / 1000) and eg_tl_tradelicense.licensenumber=:licenseNumber";// and
																																																							// eg_tl_tradelicense.status!='INITIATED'
																																																							// //and
//	String getQueryById = "select * from public.eg_tl_change_beneficial where id=:id";
//																																																					// eg_user.id=:userId

	String queryForGetChangeBeneficial = "select * from public.eg_tl_change_beneficial where license_number IN(:licenseNumber) and application_status IN(1,2,3) order by created_at desc limit 1";

	String getUpdateBeneficialId = "select * from public.eg_tl_change_beneficial where application_number IN(:applicationNumber) and application_status IN(1,2,3) \r\n"
			+ " order by created_at desc";

	String querybyLicenseNumber = "select * from public.eg_tl_change_beneficial where license_number IN(:licenseNumber) and application_status IN(1,2,3) \r\n"
			+ " order by created_at desc limit 1";

//	String getDataQueryBycbApplicationNumber="select * from public.eg_tl_change_beneficial where (cb_application_number=:cbapplicationNumber or application_number=:applicationNumber) and application_status IN(1,2,3) \r\n"
//			+ " order by created_at desc limit 1";
//	
//	String getUpdateQuery="select * from public.eg_tl_change_beneficial where (application_number=:applicationNumber or cb_application_number=:applicationNumber) and application_status IN(1,2,3) \r\n"
//			+ " order by created_at desc limit 1";
//	
//	String getUpdateForNest="select * from public.eg_tl_change_beneficial where (application_number=:applicationNumber or cb_application_number=:applicationNumber) and application_status IN(3) and application_number NOT LIKE '%HRCB%'  \r\n"
//			+ " order by created_at desc limit 1";
//	
//	String getUpdateForNestrefresh="select * from public.eg_tl_change_beneficial where (application_number=:applicationNumber or cb_application_number=:applicationNumber) and application_status IN(2) and application_number NOT LIKE '%HRCB%'  \r\n"
//			+ " order by created_at desc limit 1";
//	
//	
//	String checkIsValidApplicationNumberQuery="select * from public.eg_tl_change_beneficial where application_number=:applicationNumber order by created_at desc limit 1";
//	
//	String getBeneficialDetails="select * from public.eg_tl_change_beneficial where id=:changeBeneficialId";
//	
//	
	String queryApplication = "select * from public.eg_tl_change_beneficial";

	public void save(ChangeBeneficialRequest beneficialRequest) {
		try {
			producer.push(tlConfiguration.getSaveChangreBeneficialTopic(), beneficialRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(ChangeBeneficialRequest beneficialRequest) {
		try {
			producer.push(tlConfiguration.getUpdateChangreBeneficialTopic(), beneficialRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateWorflow(ChangeBeneficialRequest beneficialRequest) {
		try {
			producer.push(tlConfiguration.getUpdateWorkFlowChangreBeneficialTopic(), beneficialRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updatePaymentDetails(ChangeBeneficialRequest beneficialRequest) {
		try {
			producer.push(tlConfiguration.getUpdatePaymentChangreBeneficialTopic(), beneficialRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getApplicationNumber(String tableName,String createdByUUid) {
		List<String> licenses = null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			String query="select * from "+tableName+" where audit_details ->> 'createdBy'='"+createdByUUid+"'";
		    System.out.println(query);
			licenses = jdbcTemplate.query(query, preparedStmtList.toArray(),
					(rs, rowNum) -> rs.getString("tcp_application_number"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return licenses;
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

	public List<ChangeBeneficial> getchangeInbeneficial() {
		List<ChangeBeneficial> licenses = null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			licenses = jdbcTemplate.query(queryApplication, preparedStmtList.toArray(),
					(rs, rowNum) -> ChangeBeneficial.builder().applicationNumber(rs.getString("application_number"))
							.licenseNumber(rs.getString("license_number")).build());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return licenses;
	}

//	public List<TradeLicense> getTradeLicense(long userId) {
//		List<TradeLicense> licenses=null;
//		try {
//		List<Object> preparedStmtList = new ArrayList<>();
//		       licenses = jdbcTemplate.query(getLicenseQuery, preparedStmtList.toArray(),  (rs, rowNum) ->TradeLicense.builder()
//				.validFrom(Long.parseLong(rs.getString("validFrom").toString()))
//				.validTo(Long.parseLong(rs.getString("validTo").toString()))
//				.tradeLicenseDetail(TradeLicenseDetail.builder().licenseFeeCharges(rs.getDouble("licensefeecharges")).build())
//				.build());
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return licenses;
//	}
//	
//	
//	public ChangeBeneficial getBeneficialByApplicationNumber(String applicationNumber){
//		
//		ChangeBeneficial cahngeBeneficial=null;
//		try {
//			List<Object> preparedStmtList = new ArrayList<>();
//			List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(getUpdateBeneficialId.replace(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
//					.id(rs.getString("id").toString())
//					.developerServiceCode(rs.getString("developerServiceCode").toString())
//					.cbApplicationNumber(rs.getString("cb_application_number").toString())
//					.applicationNumber(rs.getString("application_number").toString())
//					.applicationStatus(rs.getInt("application_status"))
//					.build());
//			if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
//				cahngeBeneficial=changeBeneficial.get(0);
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return cahngeBeneficial;
//	}

	public ChangeBeneficial getBeneficialByLicenseNumber(String licenseNumber) {

		ChangeBeneficial cahngeBeneficial = null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(
					queryForGetChangeBeneficial.replaceAll(":licenseNumber", "'" + licenseNumber + "'"),
					preparedStmtList.toArray(), (rs, rowNum) -> {

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

						return ChangeBeneficial.builder().id(rs.getString("id").toString())
								.developerServiceCode(rs.getString("developerServiceCode").toString())
								.applicationNumber(rs.getString("application_number"))
								.applicationStatus(rs.getInt("application_status")).auditDetails(auditDetails).build();
					});
			if (changeBeneficial != null && !changeBeneficial.isEmpty()) {
				cahngeBeneficial = changeBeneficial.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cahngeBeneficial;
	}

//	public ChangeBeneficial getUdatedBeneficial(String applicationNumber) {
//		
//		ChangeBeneficial cahngeBeneficial=null;
//		try {
//		List<Object> preparedStmtList = new ArrayList<>();
//		List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(getUpdateQuery.replaceAll(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
//				.id(rs.getString("id").toString())
//				.developerServiceCode(rs.getString("developerServiceCode").toString())
//				.cbApplicationNumber(rs.getString("cb_application_number").toString())
//				.applicationNumber(rs.getString("application_number"))
//				.applicationStatus(rs.getInt("application_status"))
//				.build());
//		if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
//			cahngeBeneficial=changeBeneficial.get(0);
//		}
//		}catch (Exception e) {
//		  e.printStackTrace();
//		}
//	   
//		return cahngeBeneficial;
//	}

//  public ChangeBeneficial getUdatedBeneficialForNest(String applicationNumber) {
//		
//		ChangeBeneficial cahngeBeneficial=null;
//		try {
//		List<Object> preparedStmtList = new ArrayList<>();
//		List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(getUpdateForNest.replaceAll(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
//				.id(rs.getString("id").toString())
//				.developerServiceCode(rs.getString("developerServiceCode").toString())
//				.cbApplicationNumber(rs.getString("cb_application_number").toString())
//				.applicationNumber(rs.getString("application_number"))
//				.applicationStatus(rs.getInt("application_status"))
//				.build());
//		if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
//			cahngeBeneficial=changeBeneficial.get(0);
//		}
//		}catch (Exception e) {
//		  e.printStackTrace();
//		}
//	   
//		return cahngeBeneficial;
//	}

//  public ChangeBeneficial getUdatedBeneficialForNestRefrsh(String applicationNumber) {
//		
//		ChangeBeneficial cahngeBeneficial=null;
//		try {
//		List<Object> preparedStmtList = new ArrayList<>();
//		List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(getUpdateForNestrefresh.replaceAll(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
//				.id(rs.getString("id").toString())
//				.developerServiceCode(rs.getString("developerServiceCode").toString())
//				.cbApplicationNumber(rs.getString("cb_application_number").toString())
//				.applicationNumber(rs.getString("application_number"))
//				.applicationStatus(rs.getInt("application_status"))
//				.build());
//		if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
//			cahngeBeneficial=changeBeneficial.get(0);
//		}
//		}catch (Exception e) {
//		  e.printStackTrace();
//		}
//	   
//		return cahngeBeneficial;
//	}

//  public boolean checkIsValidApplicationNumber(String applicationNumber) {
//		boolean isValid=true;
//		try {
//			List<Object> preparedStmtList = new ArrayList<>();
//			List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(checkIsValidApplicationNumberQuery.replaceAll(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
//					.id(rs.getString("id").toString())
//					.build());
//			if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
//				if(changeBeneficial.get(0).getApplicationStatus()==3) {
//				  isValid=false;
//				}
//			}
//		}catch (Exception e) {
//		  e.printStackTrace();
//		}
//	   
//		return isValid;
//	}
//
//	public ChangeBeneficial getBeneficialDetailsBycbApplicationNumber(String cbApplicationNumber){
//		String query=getDataQueryBycbApplicationNumber.replace(":cbapplicationNumber", "'"+cbApplicationNumber+"'").replace(":applicationNumber", "'"+cbApplicationNumber+"'");
//		return formateChangeBeneficialData(query);
//	}

	public ChangeBeneficial getBeneficialDetailsByApplicationNumber(String applicationNumber) {
		applicationNumber=ConvertUtil.splitAllApplicationNumber(applicationNumber);
		String query = getUpdateBeneficialId.replace(":applicationNumber",applicationNumber);
		return formateChangeBeneficialData(query);
	}

	public ChangeBeneficial searcherBeneficialDetailsByLicenceNumber(String licenseNumber) {
		String query = querybyLicenseNumber.replace(":licenseNumber", "'" + licenseNumber + "'");
		return formateChangeBeneficialData(query);
	}

	private ChangeBeneficial formateChangeBeneficialData(String query) {
		ChangeBeneficial cahngeBeneficial = null;
		try {
			List<ChangeBeneficial> changeBeneficial = getChangeBeneficialList(query);
			if (changeBeneficial != null && !changeBeneficial.isEmpty()) {
				cahngeBeneficial = changeBeneficial.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cahngeBeneficial;
	}

	public List<ChangeBeneficial> getAllRecords() {
		String query = querybyLicenseNumber.split("where")[0]+" where action!='FINAL_APPROVAL'";
		return getChangeBeneficialList(query);
	}
	
	public List<ChangeBeneficial> searcherBeneficialDetailsByLicenceNumberList(String licenseNumber) {
		String query = querybyLicenseNumber.replace(":licenseNumber", "'" + licenseNumber + "'");
		return getChangeBeneficialList(query);
	}

	public List<ChangeBeneficial> getBeneficialDetailsByApplicationNumberList(String applicationNumber) {
		applicationNumber=ConvertUtil.splitAllApplicationNumber(applicationNumber);
		String query = getUpdateBeneficialId.replace(":applicationNumber",applicationNumber);
		return getChangeBeneficialList(query);
	}
	
//	public List<ChangeBeneficial> getBeneficialDetailsById(String id) {
//		String query = getQueryById.replace(":id", "'" + id + "'");
//		return getChangeBeneficialList(query);
//	}

	private List<ChangeBeneficial> getChangeBeneficialList(String query) {
		List<ChangeBeneficial> cahngeBeneficialList = null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(query, preparedStmtList.toArray(),
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
						}catch(Exception e) {
							e.printStackTrace();
						}
						return ChangeBeneficial.builder()
								.id(rs.getString("id").toString())
								.developerServiceCode(rs.getString("developerServiceCode"))
								.paidAmount(rs.getString("paid_amount") != null ? rs.getString("paid_amount") : "0.0")
								.areaInAcres(rs.getString("areaInAcres"))
								.noObjectionCertificate(rs.getString("noObjectionCertificate"))
								.consentLetter(rs.getString("consentLetter"))
								.justificationCertificate(rs.getString("justificationCertificate"))
								.thirdPartyRightsCertificate(rs.getString("thirdPartyRightsCertificate"))
								.jointDevelopmentCertificate(rs.getString("jointDevelopmentCertificate"))
								.aministrativeChargeCertificate(rs.getString("aministrativeChargeCertificate"))
								.boardResolutionExisting(rs.getString("boardResolutionExisting"))
								.boardResolutionNewEntity(rs.getString("boardResolutionNewEntity"))
								.shareholdingPatternCertificate(rs.getString("shareholdingPatternCertificate"))
								.reraRegistrationCertificate(rs.getString("reraRegistrationCertificate"))
								.fiancialCapacityCertificate(rs.getString("fiancialCapacityCertificate"))
								.applicationStatus(rs.getInt("application_status"))
								.applicationNumber(rs.getString("application_number"))
								.workFlowCode(rs.getString("workflowcode"))
//								.diaryNumber(rs.getString("diary_number"))
								.auditDetails(auditDetails)
								.isDraft(rs.getString("is_draft"))
								.tranactionId(rs.getString("transaction_id"))
								.licenseNumber(rs.getString("license_number"))
								.createdDate(rs.getString("created_at"))
								.action(rs.getString("action"))
								.tenantId(rs.getString("tenantid"))
								.businessService(rs.getString("businessservice"))
								.status(rs.getString("status"))
								
								.tcpApplicationNumber(rs.getString("tcp_application_number"))
								.tcpCaseNumber(rs.getString("tcp_case_number"))
								.tcpDairyNumber(rs.getString("tcp_dairy_number"))
								
								.newAdditionalDetails(additionalDetails).build();

					});
			if (changeBeneficial != null && !changeBeneficial.isEmpty()) {
				cahngeBeneficialList = changeBeneficial;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cahngeBeneficialList;
	}
	
//	
//	public ChangeBeneficial getChangeBeneficial(String changeBeneficialId) {
//		ChangeBeneficial changeBeneficial=null;
//		try {
//			List<Object> preparedStmtList = new ArrayList<>();
//			List<ChangeBeneficial> changeBeneficialList = jdbcTemplate.query(getBeneficialDetails.replace(":changeBeneficialId", changeBeneficialId), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
//					.developerServiceCode(rs.getString("developerServiceCode").toString())
//					.paid_beneficial_change_amount(rs.getString("paid_beneficial_change_amount").toString())
//					.build());
//			if(changeBeneficialList!=null&&!changeBeneficialList.isEmpty()) {
//				changeBeneficial=changeBeneficialList.get(0);
//			}
//			log.info("ChangeBeneficial Data has Got : "+changeBeneficial);
//		}catch (Exception e) {
//			log.error("Exception: "+e.getMessage());
//		}
//		return changeBeneficial;
//	}

}
