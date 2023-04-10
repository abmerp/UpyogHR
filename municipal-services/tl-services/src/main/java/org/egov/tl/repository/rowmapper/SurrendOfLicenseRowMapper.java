package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.RevisedPlan;
import org.egov.tl.web.models.SurrendOfLicense;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SurrendOfLicenseRowMapper implements ResultSetExtractor<List<SurrendOfLicense>>{
	
	 @Autowired
	    private ObjectMapper mapper;
		@Override
		public List<SurrendOfLicense> extractData(ResultSet rs) throws SQLException, DataAccessException {
			// TODO Auto-generated method stub

			List<SurrendOfLicense> surrendOfLicenseList = new ArrayList<>();
			while (rs.next()) {
				SurrendOfLicense surrendOfLicense = new SurrendOfLicense();

				surrendOfLicense.setId(rs.getString("id"));surrendOfLicense.setLicenseNo(rs.getString("licenseNo"));
				surrendOfLicense.setSelectType(rs.getString("selectType"));
				surrendOfLicense.setAreaFallingUnder(rs.getString("areaFallingUnder"));
				surrendOfLicense.setThirdPartyRights(rs.getString("thirdPartyRights"));
				surrendOfLicense.setAreraRegistration(rs.getString("areraRegistration"));
				surrendOfLicense.setZoningLayoutPlanfileUrl(rs.getString("zoningLayoutPlanfileUrl"));
				surrendOfLicense.setLicenseCopyfileUrl(rs.getString("licenseCopyfileUrl"));
				surrendOfLicense.setEdcaVailedfileUrl(rs.getString("edcaVailedfileUrl"));
				surrendOfLicense.setDetailedRelocationSchemefileUrl(rs.getString("detailedRelocationSchemefileUrl"));
				surrendOfLicense.setGiftDeedfileUrl(rs.getString("giftDeedfileUrl"));
				surrendOfLicense.setMutationfileUrl(rs.getString("mutationfileUrl"));
				surrendOfLicense.setJamabandhifileUrl(rs.getString("jamabandhifileUrl"));
				surrendOfLicense.setJamabandhifileUrl(rs.getString("thirdPartyRightsDeclarationfileUrl"));
				surrendOfLicense.setAreaInAcres(rs.getString("areaInAcres"));
				
				surrendOfLicense.setApplicationNumber(rs.getString("application_number"));
				 PGobject pgObj = (PGobject) rs.getObject("additionaldetails");
				 if(pgObj!=null){
	                 JsonNode additionalDetail = null;
					try {
						additionalDetail = mapper.readTree(pgObj.getValue());
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					surrendOfLicense.setAdditionalDetails(additionalDetail);
	             }

				AuditDetails auditDetails = new AuditDetails();

				AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("createdby"))
						.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("lastmodifyby"))
						.lastModifiedTime(rs.getLong("lastmodifiedtime")).build();
				surrendOfLicense.setAuditDetails(auditDetails_build);
				surrendOfLicenseList.add(surrendOfLicense);
			}

			return surrendOfLicenseList;
		}


}
