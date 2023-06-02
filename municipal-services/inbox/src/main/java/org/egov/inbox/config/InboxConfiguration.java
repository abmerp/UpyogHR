package org.egov.inbox.config;

import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
@Component
public class InboxConfiguration {

	@Value("${app.timezone}")
	private String timeZone;

	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	@Value("${workflow.host}")
	private String workflowHost;

	@Value("${workflow.process.search.path}")
	private String processSearchPath;

	@Value("${workflow.businessservice.search.path}")
	private String businessServiceSearchPath;

	@Value("#{${service.search.mapping}}")
	private Map<String, Map<String, String>> serviceSearchMapping;

	@Value("${workflow.process.count.path}")
	private String processCountPath;

	@Value("${workflow.process.statuscount.path}")
	private String processStatusCountPath;

	@Value("${egov.searcher.host}")
	private String searcherHost;

	@Value("${egov.searcher.fsm.dsoid.path}")
	private String fsmInboxDSoIDEndpoint;
	
	@Value("${egov.vehicle.host}")
	private String vehicleHost;
	
	@Value("${vehicle.search.path}")
	private String vehicleSearchPath;
	
	@Value("${vehicle.fetchApplicationStatusCount.path}")
	private String vehicleApplicationStatusCountPath;
	
	@Value("${vehicle.searchTrip.path}")
	private String vehicleSearchTripPath;
	
	@Value("${egov.fsm.host}")
	private String fsmHost;

	@Value("${egov.fsm.fetchApplicationIds.path}")
	private String fetchApplicationIds;
	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

//	@Value("${egov.searcher.host}")
//	private String searcherHost;

	@Value("${egov.searcher.tl.search.path}")
	private String tlInboxSearcherEndpoint;

	@Value("${egov.searcher.tl.search.desc.path}")
	private String tlInboxSearcherDescEndpoint;

	@Value("${egov.searcher.tl.count.path}")
	private String tlInboxSearcherCountEndpoint;

	@Value("${egov.searcher.tl.bgnew.search.path}")
	private String newBankGuaranteeSearcherEndpoint;

	@Value("${egov.searcher.tl.bgnew.count.path}")
	private String newBankGuaranteeSearcherCountEndpoint;

	@Value("${egov.searcher.tl.bgnew.search.desc.path}")
	private String newBankGuaranteeSearcherDescEndpoint;

	@Value("${egov.searcher.tl.SP.search.path}")
	private String servicePlanSearcherEndpoint;

	@Value("${egov.searcher.tl.SP.count.path}")
	private String servicePlanSearcherCountEndpoint;

	@Value("${egov.searcher.tl.SP.search.desc.path}")
	private String servicePlaneSearcherDescEndpoint;

	@Value("${egov.searcher.tl.EP.search.path}")
	private String electricPlanSearcherEndpoint;

	@Value("${egov.searcher.tl.EP.count.path}")
	private String electricPlanSearcherCountEndpoint;

	@Value("${egov.searcher.tl.EP.search.desc.path}")
	private String electricPlaneSearcherDescEndpoint;
	@Value("${egov.searcher.tl.AS.search.path}")
	private String approvalStandardSearcherEndPoint;
	@Value("${egov.searcher.tl.AS.count.path}")
	private String approvalStandardSearcherCountEndPoint;
	@Value("${egov.searcher.tl.AS.search.desc.path}")
	private String approvalStandardSearcherDescEndPoint;
	@Value("${egov.searcher.tl.RL.search.path}")
	private String renewalOfLicenceSearcherEndPoint;
	@Value("${egov.searcher.tl.RL.count.path}")
	private String renewalOfLicenceSearcherCountEndPoint;
	@Value("${egov.searcher.tl.RL.search.desc.path}")
	private String renewalOfLicenceSearcherDescEndPoint;
	@Value("${egov.searcher.tl.RLP.search.path}")
	private String revisedLayoutPlanSearcherEndPoint;
	@Value("${egov.searcher.tl.RLP.count.path}")
	private String revisedLayoutPlanSearcherCountEndPoint;
	@Value("${egov.searcher.tl.RLP.search.desc.path}")
	private String revisedLayoutPlanSearcherDescEndPoint;
	@Value("${egov.searcher.tl.TRANSFER.search.path}")
	private String transferOfLicenceSearcherEndPoint;
	@Value("${egov.searcher.tl.TRANSFER.count.path}")
	private String transferOfLicenceSearcherCountEndPoint;
	@Value("${egov.searcher.tl.TRANSFER.search.desc.path}")
	private String transferOfLicenceSearcherDescEndPoint;

//	CHANGE OF BENEFICIAL
	@Value("${egov.searcher.tl.change.beneficial.search.path}")
	private String changeOfBeneficialSearcherEndPoint;
	@Value("${egov.searcher.tl.change.beneficial.count.path}")
	private String changeOfBeneficialSearcherCountEndPoint;
	@Value("${egov.searcher.tl.change.beneficial.search.desc.path}")
	private String changeOfBeneficialSearcherDescEndPoint;

//	CHANGE OF BENEFICIAL
	@Value("${egov.searcher.tl.completion.certificate.search.path}")
	private String completionCertificateSearcherEndPoint;
	@Value("${egov.searcher.tl.completion.certificate.count.path}")
	private String completionCertificateSearcherCountEndPoint;
	@Value("${egov.searcher.tl.completion.certificate.search.desc.path}")
	private String completionCertificateSearcherDescEndPoint;

	@Value("${egov.searcher.tl.constructionofcommunity.search.path}")
	private String constructionOfCommunitySearcherEndPoint;
	@Value("${egov.searcher.tl.constructionofcommunity.count.path}")
	private String constructionOfCommunitySearcherCountEndPoint;
	@Value("${egov.searcher.tl.constructionofcommunity.search.desc.path}")
	private String constructionOfCommunitySearcherDescEndPoint;

//	SURREND OF LICENSE
	@Value("${egov.searcher.tl.Surrend.of.license.search.path}")
	private String SurrendOfLicenseSearcherEndPoint;
	@Value("${egov.searcher.tl.Surrend.of.license.count.path}")
	private String SurrendOfLicenseSearcherCountEndPoint;
	@Value("${egov.searcher.tl.Surrend.of.license.search.desc.path}")
	private String SurrendOfLicenseSearcherDescEndPoint;

	@Value("${egov.searcher.tl.Extension.of.CLU.Permission.search.path}")
	private String ExtensionOfCLUPermissionSearcherEndPoint;
	@Value("${egov.searcher.tl.Extension.of.CLU.Permission.count.path}")
	private String ExtensionOfCLUPermissionSearcherCountEndPoint;
	@Value("${egov.searcher.tl.Extension.of.CLU.Permission.search.desc.path}")
	private String ExtensionOfCLUPermissionSearcherDescEndPoint;
	@Value("${egov.searcher.tl.TP.search.path}")
	private String technicalPrpfessionalSearchPath;
	@Value("${egov.searcher.tl.TP.count.path}")
	private String technicalPrpfessionalCountPath;
	@Value("${egov.searcher.tl.TP.search.desc.path}")
	private String technicalPrpfessionalSearchDescPath;
}