package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.RevisedPlan;
import org.egov.tl.web.models.SurrendOfLicense;
import org.javers.common.collections.Arrays;
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
public class SurrendOfLicenseRowMapper implements ResultSetExtractor<List<SurrendOfLicense>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<SurrendOfLicense> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub

		List<SurrendOfLicense> surrendOfLicenseList = new ArrayList<>();
		while (rs.next()) {
			SurrendOfLicense surrendOfLicense = new SurrendOfLicense();

			surrendOfLicense.setLicenseNo(rs.getString("license_no"));
			surrendOfLicense.setId(rs.getString("id"));
//			surrendOfLicense.setLicenseNo(Arrays.asList(rs.getString("license_no")));

			surrendOfLicense.setSelectType(rs.getString("select_type"));
			surrendOfLicense.setAreaFallingUnder(rs.getString("area_falling_under"));
			surrendOfLicense.setThirdPartyRights(rs.getString("third_party_rights"));
			surrendOfLicense.setAreraRegistration(rs.getString("arera_registration"));
			surrendOfLicense.setZoningLayoutPlanfileUrl(rs.getString("zoning_layout_planfileurl"));
			surrendOfLicense.setLicenseCopyfileUrl(rs.getString("license_copyfileurl"));
			surrendOfLicense.setEdcaVailedfileUrl(rs.getString("edca_vailedfileurl"));
			surrendOfLicense.setDetailedRelocationSchemefileUrl(rs.getString("detailed_relocationSchemefileurl"));
			surrendOfLicense.setGiftDeedfileUrl(rs.getString("gift_deedfileurl"));
			surrendOfLicense.setMutationfileUrl(rs.getString("mutationfileurl"));
			surrendOfLicense.setJamabandhifileUrl(rs.getString("jamabandhifileurl"));
			surrendOfLicense
					.setThirdPartyRightsDeclarationfileUrl(rs.getString("third_partyrights_declarationfileurl"));
			surrendOfLicense.setAreaInAcres(rs.getString("areain_acres"));
			surrendOfLicense.setApplicationNumber(rs.getString("application_number"));
			surrendOfLicense.setWorkflowCode(rs.getString("workflowcode"));
			surrendOfLicense.setStatus(rs.getString("status"));
			surrendOfLicense.setBusinessService(rs.getString("businessservice"));
			surrendOfLicense.setTenantId(rs.getString("tenant_id"));
			surrendOfLicense.setDeclarationIDWWorksfileUrl(rs.getString("declarationi_dwworksfileurl"));
			surrendOfLicense.setRevisedLayoutPlanfileUrl(rs.getString("revised_layout_planfileurl"));
			surrendOfLicense.setAvailedEdcfileUrl(rs.getString("availed_edc_file_url"));
			surrendOfLicense.setAreaFallingUnderfileUrl(rs.getString("area_falling_underfileurl"));
			surrendOfLicense.setAreaFallingDividing(rs.getString("area_falling_dividing"));
			surrendOfLicense.setTcpApplicationNumber(rs.getString("tcpapplicationnumber"));
			surrendOfLicense.setTcpCaseNumber(rs.getString("tcpcasenumber"));
			surrendOfLicense.setTcpDairyNumber(rs.getString("tcpdairynumber"));
			PGobject pgObj = (PGobject) rs.getObject("additionaldetails");
			if (pgObj != null) {
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

			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("created_by"))
					.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("lastModified_by"))
					.lastModifiedTime(rs.getLong("lastModified_time")).build();
			surrendOfLicense.setAuditDetails(auditDetails_build);
			surrendOfLicenseList.add(surrendOfLicense);
		}

		return surrendOfLicenseList;
	}

}
