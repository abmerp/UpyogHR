package org.egov.tl.web.models;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Entity
//@Table(name="Surrend_Of_License")
//@NamedQuery(name="SurrendOfLicense.findAll", query="SELECT e FROM SurrendOfLicense e")
public class SurrendOfLicense {

	private String id;

	private String action;
	@JsonProperty("licenseNo")
	private String licenseNo;

	private String selectType;

	private String areaFallingUnder;

	private String thirdPartyRights;

	private String areraRegistration;

	private String zoningLayoutPlanfileUrl;

	private String licenseCopyfileUrl;

	private String edcaVailedfileUrl;

	private String detailedRelocationSchemefileUrl;

	private String giftDeedfileUrl;

	private String mutationfileUrl;

	private String jamabandhifileUrl;

	private String thirdPartyRightsDeclarationfileUrl;

	private String areaInAcres;

	private String applicationNumber;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

	private JsonNode additionalDetails;

	private List<String> assignee;

	private String tenantId;

	private String businessService;

	private String comment;

	private String workflowCode;

	private String status;

	private String declarationIDWWorksfileUrl;
	private String revisedLayoutPlanfileUrl;
	private String availedEdcfileUrl;
	private String areaFallingUnderfileUrl;
	private String areaFallingDividing;
	@JsonProperty("tcpApplicationNumber")
	private String tcpApplicationNumber;

	@JsonProperty("tcpCaseNumber")
	private String tcpCaseNumber;

	@JsonProperty("tcpDairyNumber")
	private String tcpDairyNumber;

}
