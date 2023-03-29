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
	
	String getLicenseQuery="select eg_tl_tradelicense.*  from eg_user inner join eg_tl_tradelicense on eg_user.uuid=eg_tl_tradelicense.createdBy "
			+ "inner join eg_tl_tradelicensedetail on eg_tl_tradelicensedetail.tradelicenseid = eg_tl_tradelicense.id "
			+ "where to_timestamp(eg_tl_tradelicense.validfrom / 1000)<CURRENT_TIMESTAMP(0) and CURRENT_TIMESTAMP(0)<to_timestamp(eg_tl_tradelicense.validto / 1000) ";//and eg_tl_tradelicense.status!='INITIATED'";// and eg_user.id=:userId";

	String applicationNoQuery="select eg_tl_tradelicense.*  from eg_user inner join eg_tl_tradelicense on eg_user.uuid=eg_tl_tradelicense.createdBy "
			+ "inner join eg_tl_tradelicensedetail on eg_tl_tradelicensedetail.tradelicenseid = eg_tl_tradelicense.id "
			+ "where to_timestamp(eg_tl_tradelicense.validfrom / 1000)<CURRENT_TIMESTAMP(0) and CURRENT_TIMESTAMP(0)<to_timestamp(eg_tl_tradelicense.validto / 1000) and eg_tl_tradelicense.applicationnumber=:applicationNumber";//and  eg_tl_tradelicense.status!='INITIATED'  //and eg_user.id=:userId

	String getUpdateBeneficialId="select * from public.eg_tl_change_beneficial where application_number=:applicationNumber and is_full_payment_done = 'false'\r\n"
			+ " order by created_at desc limit 1";
	
	String getUpdateQuery="select * from public.eg_tl_change_beneficial where application_number=:applicationNumber and application_status = 1 \r\n"
			+ " order by created_at desc limit 1";
	
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
	
	public int getLicenseByApplicationNo(String applicationNumber,long userId) {
		int size=0;
		try {
			List<Object> preparedStmtList = new ArrayList<>();
			List<TradeLicense> licenses = jdbcTemplate.query(applicationNoQuery.replace(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->TradeLicense.builder()
//					.validFrom(Long.parseLong(rs.getString("validFrom").toString()))
					.build());
			size=licenses.size();
			}catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}
	
	public List<TradeLicense> getTradeLicense(long userId)
			throws JsonProcessingException {
		List<Object> preparedStmtList = new ArrayList<>();
		List<TradeLicense> licenses = jdbcTemplate.query(getLicenseQuery, preparedStmtList.toArray(),  (rs, rowNum) ->TradeLicense.builder()
				.validFrom(Long.parseLong(rs.getString("validFrom").toString()))
				.validTo(Long.parseLong(rs.getString("validTo").toString()))
				.build());
				return licenses;
	}
	
	
	public ChangeBeneficial getBeneficialByApplicationNumber(String applicationNumber)
			throws JsonProcessingException {
		
		ChangeBeneficial cahngeBeneficial=null;
		List<Object> preparedStmtList = new ArrayList<>();
		List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(getUpdateBeneficialId.replace(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
				.id(rs.getString("id").toString())
				.developerServiceCode(rs.getString("developerServiceCode").toString())
				.cbApplicationNumber(rs.getString("cb_application_number").toString())
				.build());
		if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
			cahngeBeneficial=changeBeneficial.get(0);
		}
	   
		return cahngeBeneficial;
	}
	
	public ChangeBeneficial getUdatedBeneficial(String applicationNumber)
			throws JsonProcessingException {
		
		ChangeBeneficial cahngeBeneficial=null;
		List<Object> preparedStmtList = new ArrayList<>();
		List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(getUpdateQuery.replace(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
				.id(rs.getString("id").toString())
				.developerServiceCode(rs.getString("developerServiceCode").toString())
				.cbApplicationNumber(rs.getString("cb_application_number").toString())
				.applicationStatus(rs.getInt("application_status"))
				.build());
		if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
			cahngeBeneficial=changeBeneficial.get(0);
		}
	   
		return cahngeBeneficial;
	}
	
	public ChangeBeneficial getBeneficialDetailsByApplicationNumber(String applicationNumber)
			throws JsonProcessingException {
		
		ChangeBeneficial cahngeBeneficial=null;
		List<Object> preparedStmtList = new ArrayList<>();
		List<ChangeBeneficial> changeBeneficial = jdbcTemplate.query(getUpdateBeneficialId.replace(":applicationNumber", "'"+applicationNumber+"'"), preparedStmtList.toArray(),  (rs, rowNum) ->ChangeBeneficial.builder()
				.id(rs.getString("id").toString())
				.developerServiceCode(rs.getString("developerServiceCode").toString())
				.cbApplicationNumber(rs.getString("cb_application_number").toString())
				.paidAmount(rs.getString("paid_beneficial_change_amount").toString())
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
				.applicationStatus(rs.getInt("applicationStatus"))
				.createdDate(rs.getString("created_at").toString())
				.build());
		if(changeBeneficial!=null&&!changeBeneficial.isEmpty()) {
			cahngeBeneficial=changeBeneficial.get(0);
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
