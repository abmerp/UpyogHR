package org.egov.tl.abm.repo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.TLRowMapper;
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ChangeBeneficialRepo {

	@Autowired
	private Producer producer;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private TLConfiguration tlConfiguration;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private TLRowMapper rowMapper;
	
	String getLicenseQuery="select eg_tl_tradelicense.validFrom,eg_tl_tradelicense.validTo,eg_tl_tradelicensedetail.licensefeecharges  from eg_user inner join eg_tl_tradelicense on eg_user.uuid=eg_tl_tradelicense.createdBy "
			+ "inner join eg_tl_tradelicensedetail on eg_tl_tradelicensedetail.tradelicenseid = eg_tl_tradelicense.id "
			+ "where to_timestamp(eg_tl_tradelicense.validfrom / 1000)<CURRENT_TIMESTAMP(0) and CURRENT_TIMESTAMP(0)<to_timestamp(eg_tl_tradelicense.validto / 1000) ";//and eg_tl_tradelicense.status!='INITIATED'";// and eg_user.id=:userId";

	String applicationNoQuery="select eg_tl_tradelicense.validFrom,eg_tl_tradelicense.validTo,eg_tl_tradelicensedetail.licensefeecharges  from eg_user inner join eg_tl_tradelicense on eg_user.uuid=eg_tl_tradelicense.createdBy "
			+ "inner join eg_tl_tradelicensedetail on eg_tl_tradelicensedetail.tradelicenseid = eg_tl_tradelicense.id "
			+ "where to_timestamp(eg_tl_tradelicense.validfrom / 1000)<CURRENT_TIMESTAMP(0) and CURRENT_TIMESTAMP(0)<to_timestamp(eg_tl_tradelicense.validto / 1000) and eg_tl_tradelicense.applicationnumber=:applicationNumber";//and  eg_tl_tradelicense.status!='INITIATED'  //and eg_user.id=:userId

	String getUpdateBeneficialId="select * from public.eg_tl_change_beneficial where application_number=:applicationNumber and application_status IN(1,2,3) \r\n"
			+ " order by created_at desc limit 1";
	
	String getDataQueryBycbApplicationNumber="select * from public.eg_tl_change_beneficial where (cb_application_number=:cbapplicationNumber or application_number=:applicationNumber) and application_status IN(1,2,3) \r\n"
			+ " order by created_at desc limit 1";
	
	String getUpdateQuery="select * from public.eg_tl_change_beneficial where (application_number=:applicationNumber or cb_application_number=:applicationNumber) and application_status IN(1,2,3) \r\n"
			+ " order by created_at desc limit 1";
	
	String getUpdateForNest="select * from public.eg_tl_change_beneficial where (application_number=:applicationNumber or cb_application_number=:applicationNumber) and application_status IN(3) and application_number NOT LIKE '%HRCB%'  \r\n"
			+ " order by created_at desc limit 1";
	
	String getUpdateForNestrefresh="select * from public.eg_tl_change_beneficial where (application_number=:applicationNumber or cb_application_number=:applicationNumber) and application_status IN(2) and application_number NOT LIKE '%HRCB%'  \r\n"
			+ " order by created_at desc limit 1";
	
	
	String checkIsValidApplicationNumberQuery="select * from public.eg_tl_change_beneficial where application_number=:applicationNumber order by created_at desc limit 1";
	
	String getBeneficialDetails="select * from public.eg_tl_change_beneficial where id=:changeBeneficialId";
	
	
	public void save(ChangeBeneficialRequest beneficialRequest) {
		try {
	        producer.push(tlConfiguration.getSaveChangreBeneficialTopic(), beneficialRequest);
		  }catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(ChangeBeneficialRequest beneficialRequest) {
		try {
	     	producer.push(tlConfiguration.getUpdateChangreBeneficialTopic(), beneficialRequest);
	  	}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updatePaymentDetails(ChangeBeneficialRequest beneficialRequest) {
		try {
	     	producer.push(tlConfiguration.getUpdatePaymentChangreBeneficialTopic(), beneficialRequest);
	  	}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<TradeLicense> getLicenseByApplicationNo(String applicationNumber,long userId) {
		List<TradeLicense> licenses=null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			      licenses = jdbcTemplate.query(applicationNoQuery.replace(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->TradeLicense.builder()
					.validFrom(Long.parseLong(rs.getString("validFrom").toString()))
					.validTo(Long.parseLong(rs.getString("validTo").toString()))
					.tradeLicenseDetail(TradeLicenseDetail.builder().licenseFeeCharges(rs.getDouble("licensefeecharges")).build())
					.build());
			}catch (Exception e) {
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
	
	
	public ChangeBeneficial getBeneficialByApplicationNumber(String applicationNumber){
		
		ChangeBeneficial cahngeBeneficial=null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(getUpdateBeneficialId.replace(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
					.id(rs.getString("id").toString())
					.developerServiceCode(rs.getString("developerServiceCode").toString())
					.cbApplicationNumber(rs.getString("cb_application_number").toString())
					.applicationNumber(rs.getString("application_number").toString())
					.applicationStatus(rs.getInt("application_status"))
					.build());
			if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
				cahngeBeneficial=changeBeneficial.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return cahngeBeneficial;
	}
	
	public ChangeBeneficial getUdatedBeneficial(String applicationNumber) {
		
		ChangeBeneficial cahngeBeneficial=null;
		try {
		List<Object> preparedStmtList = new ArrayList<>();
		List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(getUpdateQuery.replaceAll(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
				.id(rs.getString("id").toString())
				.developerServiceCode(rs.getString("developerServiceCode").toString())
				.cbApplicationNumber(rs.getString("cb_application_number").toString())
				.applicationNumber(rs.getString("application_number"))
				.applicationStatus(rs.getInt("application_status"))
				.build());
		if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
			cahngeBeneficial=changeBeneficial.get(0);
		}
		}catch (Exception e) {
		  e.printStackTrace();
		}
	   
		return cahngeBeneficial;
	}
	
  public ChangeBeneficial getUdatedBeneficialForNest(String applicationNumber) {
		
		ChangeBeneficial cahngeBeneficial=null;
		try {
		List<Object> preparedStmtList = new ArrayList<>();
		List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(getUpdateForNest.replaceAll(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
				.id(rs.getString("id").toString())
				.developerServiceCode(rs.getString("developerServiceCode").toString())
				.cbApplicationNumber(rs.getString("cb_application_number").toString())
				.applicationNumber(rs.getString("application_number"))
				.applicationStatus(rs.getInt("application_status"))
				.build());
		if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
			cahngeBeneficial=changeBeneficial.get(0);
		}
		}catch (Exception e) {
		  e.printStackTrace();
		}
	   
		return cahngeBeneficial;
	}
  
  public ChangeBeneficial getUdatedBeneficialForNestRefrsh(String applicationNumber) {
		
		ChangeBeneficial cahngeBeneficial=null;
		try {
		List<Object> preparedStmtList = new ArrayList<>();
		List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(getUpdateForNestrefresh.replaceAll(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
				.id(rs.getString("id").toString())
				.developerServiceCode(rs.getString("developerServiceCode").toString())
				.cbApplicationNumber(rs.getString("cb_application_number").toString())
				.applicationNumber(rs.getString("application_number"))
				.applicationStatus(rs.getInt("application_status"))
				.build());
		if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
			cahngeBeneficial=changeBeneficial.get(0);
		}
		}catch (Exception e) {
		  e.printStackTrace();
		}
	   
		return cahngeBeneficial;
	}
  
  public boolean checkIsValidApplicationNumber(String applicationNumber) {
		boolean isValid=true;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(checkIsValidApplicationNumberQuery.replaceAll(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
					.id(rs.getString("id").toString())
					.build());
			if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
				if(changeBeneficial.get(0).getApplicationStatus()==3) {
				  isValid=false;
				}
			}
		}catch (Exception e) {
		  e.printStackTrace();
		}
	   
		return isValid;
	}

	public ChangeBeneficial getBeneficialDetailsBycbApplicationNumber(String cbApplicationNumber){
		String query=getDataQueryBycbApplicationNumber.replace(":cbapplicationNumber", "'"+cbApplicationNumber+"'").replace(":applicationNumber", "'"+cbApplicationNumber+"'");
		return formateChangeBeneficialData(query);
	}
	
	
	public ChangeBeneficial getBeneficialDetailsByApplicationNumber(String applicationNumber){
		String query=getUpdateBeneficialId.replace(":applicationNumber", "'"+applicationNumber+"'");
		return formateChangeBeneficialData(query);
	}
	
	private ChangeBeneficial formateChangeBeneficialData(String query){
		ChangeBeneficial cahngeBeneficial=null;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(query, preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
					.id(rs.getString("id").toString())
					.developerServiceCode(rs.getString("developerServiceCode").toString())
					.cbApplicationNumber(rs.getString("cb_application_number").toString())
					.paidAmount(rs.getString("paid_amount")!=null?rs.getString("paid_amount").toString():"0.0")
					.areaInAcres(rs.getString("areaInAcres").toString())
					.noObjectionCertificate(rs.getString("noObjectionCertificate").toString())
					.consentLetter(rs.getString("consentLetter").toString())
					.justificationCertificate(rs.getString("justificationCertificate").toString())
					.thirdPartyRightsCertificate(rs.getString("thirdPartyRightsCertificate").toString())
					.jointDevelopmentCertificate(rs.getString("jointDevelopmentCertificate").toString())
					.aministrativeChargeCertificate(rs.getString("aministrativeChargeCertificate").toString())
					.boardResolutionExisting(rs.getString("boardResolutionExisting").toString())
					.boardResolutionNewEntity(rs.getString("boardResolutionNewEntity").toString())
					.shareholdingPatternCertificate(rs.getString("shareholdingPatternCertificate").toString())
					.reraRegistrationCertificate(rs.getString("reraRegistrationCertificate").toString())
					.fiancialCapacityCertificate(rs.getString("fiancialCapacityCertificate").toString())
					.applicationStatus(rs.getInt("application_status"))
					.applicationNumber(rs.getString("application_number"))
					.createdDate(rs.getString("created_at").toString())
					.build());
			if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
				cahngeBeneficial=changeBeneficial.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return cahngeBeneficial;
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
