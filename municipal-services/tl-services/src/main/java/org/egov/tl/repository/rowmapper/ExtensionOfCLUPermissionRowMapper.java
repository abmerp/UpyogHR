package org.egov.tl.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ExtensionOfCLUPermission;
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
public class ExtensionOfCLUPermissionRowMapper implements ResultSetExtractor<List<ExtensionOfCLUPermission>>{
	
	
	
	@Autowired
	private ObjectMapper mapper;

    @Override
	public List<ExtensionOfCLUPermission> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub

		List<ExtensionOfCLUPermission> extensionOfCLUPermissionList = new ArrayList<>();
		while (rs.next()) {
			ExtensionOfCLUPermission extensionOfCLUPermission = new ExtensionOfCLUPermission();
			
	
//			 additional_details, "", comment, , status)
			

			extensionOfCLUPermission.setId(rs.getString("id"));
			extensionOfCLUPermission.setLicenseNo(rs.getString("license_no"));
			extensionOfCLUPermission.setCaseNo(rs.getString("case_no"));
			extensionOfCLUPermission.setApplicationNo(rs.getString("application_no"));
			extensionOfCLUPermission.setNaturePurpose(rs.getString("nature_purpose"));
			extensionOfCLUPermission.setTotalAreaSq(rs.getString("total_area_sq"));
			extensionOfCLUPermission.setCluDate(rs.getString("clu_date"));
			extensionOfCLUPermission.setExpiryClu(rs.getString("expiry_clu"));
			extensionOfCLUPermission.setStageConstruction(rs.getString("stage_construction"));
			extensionOfCLUPermission.setApplicantName(rs.getString("applicant_name"));
			extensionOfCLUPermission.setMobile(rs.getString("mobile"));
			extensionOfCLUPermission.setEmailAddress(rs.getString("email_address"));
			extensionOfCLUPermission.setAddress(rs.getString("address"));
			extensionOfCLUPermission.setVillage(rs.getString("village"));
			extensionOfCLUPermission.setTehsil(rs.getString("tehsil"));
			extensionOfCLUPermission.setPinCode(rs.getString("pin_code"));
			extensionOfCLUPermission.setReasonDelay(rs.getString("reason_delay"));
			extensionOfCLUPermission.setBuildingPlanApprovalStatus(rs.getString("building_plan_approval_status"));
			extensionOfCLUPermission.setZoningPlanApprovalDate(rs.getString("zoning_plan_approval_date"));
			extensionOfCLUPermission.setDateOfSanctionBuildingPlan(rs.getString("date_of_sanction_building_plan"));
			extensionOfCLUPermission.setAppliedFirstTime(rs.getString("applied_first_time"));
			extensionOfCLUPermission.setUploadbrIIIfileUrl(rs.getString("upload_brIIIfile_url"));
			extensionOfCLUPermission.setCluPermissionLetterfileUrl(rs.getString("clu_permission_letterfile_url"));
			extensionOfCLUPermission.setUploadPhotographsfileUrl(rs.getString("upload_photographsfile_url"));
			extensionOfCLUPermission.setReceiptApplicationfileUrl(rs.getString("receipt_applicationfile_url"));
			extensionOfCLUPermission.setUploadBuildingPlanfileUrl(rs.getString("upload_building_planfile_url"));
			extensionOfCLUPermission.setIndemnityBondfileUrl(rs.getString("indemnity_bondfile_url"));
			extensionOfCLUPermission.setTenantId(rs.getString("tenant_id"));
			extensionOfCLUPermission.setBusinessService(rs.getString("businessService"));
			extensionOfCLUPermission.setComment(rs.getString("comment"));
			extensionOfCLUPermission.setWorkflowCode(rs.getString("workflow_code"));
			extensionOfCLUPermission.setStatus(rs.getString("status"));
			

			PGobject pgObj = (PGobject) rs.getObject("additional_details");
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
				extensionOfCLUPermission.setAdditionalDetails(additionalDetail);
			}

			AuditDetails auditDetails = new AuditDetails();

			AuditDetails auditDetails_build = auditDetails.builder().createdBy(rs.getString("created_by"))
					.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("lastModified_by"))
					.lastModifiedTime(rs.getLong("lastModified_time")).build();
			extensionOfCLUPermission.setAuditDetails(auditDetails_build);
			extensionOfCLUPermissionList.add(extensionOfCLUPermission);
		}

		return extensionOfCLUPermissionList;
	}


}
