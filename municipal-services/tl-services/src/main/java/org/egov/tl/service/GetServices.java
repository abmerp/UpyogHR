package org.egov.tl.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.abm.repo.ChangeBeneficialRepo;
import org.egov.tl.abm.repo.CompletionCertificateRepo;
import org.egov.tl.abm.repo.ConstructionOfCommunityRepo;
import org.egov.tl.repository.TLRepository;
import org.egov.tl.repository.rowmapper.ApprovalStandardRowMapper;
import org.egov.tl.repository.rowmapper.EPRowMapper;
import org.egov.tl.repository.rowmapper.ExtensionOfCLUPermissionRowMapper;
import org.egov.tl.repository.rowmapper.RevisedLayoutPlanRowMapper;
import org.egov.tl.repository.rowmapper.SPRowMapper;
import org.egov.tl.repository.rowmapper.SurrendOfLicenseRowMapper;
import org.egov.tl.repository.rowmapper.TransferRowMapper;
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.CompletionCertificate;
import org.egov.tl.web.models.ConstructionOfCommunity;
import org.egov.tl.web.models.ElectricPlanRequest;
import org.egov.tl.web.models.ExtensionOfCLUPermission;
import org.egov.tl.web.models.RevisedPlan;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.SurrendOfLicense;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GetServices {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	TLRepository tLRepository;
	@Autowired
	RevisedPlanServices revisedPlanServices;
	@Autowired
	ServicePlanService servicePlanService;
	@Autowired
	ElectricPlanService electricPlanService;
	@Autowired
	TransferOfLicenseServices transferOfLicenseServices;
	@Autowired
	ApprovalStandardService approvalStandardService;
	@Autowired
	SurrendOfLicenseServices surrendOfLicenseServices;
	@Autowired
	ExtensionOfCLUPermissionServices extensionOfCLUPermissionServices;
	private static final String BUSINESS_NEWTL = "NewTL";
	private static final String BUSINESS_SERVICE_PLAN = "SERVICE_PLAN";
	private static final String BUSINESSSERVICE_EXTENSIONOFCLUPERMISSION = "EXTENSION_OF_CLU_PERMISSION";
	private static final String BUSINESSSERVICE_SURRENDOFLICENSE = "SURREND_OF_LICENSE";
	private static final String BUSINESSSERVICE_CHANGEBENEFICIAL = "CHANGE_OF_BENEFICIAL";
	private static final String BUSINESSSERVICE_COMPLETION_CERTIFICATE = "COMPLETION_CERTIFICATE";
	private static final String BUSINESSSERVICE_CONSTRUCTION_OF_COMMUNITY = "CONSTRUCTION_OF_COMMUNITY";
	private static final String BUSINESSSERVICE_TRANSFER = "TRANSFER_OF_LICIENCE";
//	private static final String BUSINESSSERVICE_RENEWAL = "RENWAL_OF_LICENCE";
	private static final String BUSINESSSERVICE_REVISED = "REVISED_LAYOUT_PLAN";
	private static final String BUSINESSSERVICE_ELECTRICAL_PLAN = "ELECTRICAL_PLAN";
	private static final String BUSINESSSERVICE_SERVICE_PLAN_DEMACATION = "SERVICE_PLAN_DEMARCATION";
	private static final String BUSINESSSERVICE_APPROVAL_OF_STANDARD = "APPROVAL_OF_STANDARD";

	public List<String> search(RequestInfo requestInfo, String type, String businessService) {

		List<TradeLicense> Result;
		String loiNum;
		String application;
		String licenceNum = null;
		List<String> finalResult = new ArrayList<>();
		List<ServicePlanRequest> ResultservicePlan = null;
		List<RevisedPlan> resultRevisedPlan = null;
		List<ElectricPlanRequest> resultElectricPlan = null;
		List<Transfer> resultTransfer = null;
		List<ApprovalStandardEntity> resultApproval = null;
		List<SurrendOfLicense> resultSurrender = null;
		List<ExtensionOfCLUPermission> resultExtensionOfCLUPermission = null;
		List<CompletionCertificate> resultCompletion = null;
		List<ConstructionOfCommunity> resultConstruction = null;
		List<ChangeBeneficial> resultChangeinBeneficial = null;

		String loiNumber = null;
		String applicationNumber = null;
		switch (businessService) {
		case BUSINESS_NEWTL:

			TradeLicenseSearchCriteria criteria = new TradeLicenseSearchCriteria();
			criteria.setUuid(requestInfo.getUserInfo().getUuid());
			List<TradeLicense> tradeLicence = tLRepository.getLicenses(criteria, requestInfo);
			log.info("tradeLicence" + tradeLicence);
			for (TradeLicense tradeLicense : tradeLicence) {
				application = tradeLicense.getTcpApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;
		case BUSINESS_SERVICE_PLAN:

			// builder = new StringBuilder(queryServicePlan);

			// ResultservicePlan = namedParameterJdbcTemplate.query(builder.toString(),
			// spRowMapper);
			List<ServicePlanRequest> servicePlanRequestSearch = servicePlanService.searchServicePlan(loiNumber,
					applicationNumber, requestInfo);
			for (ServicePlanRequest servicePlanRequest : servicePlanRequestSearch) {

				application = servicePlanRequest.getTcpApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;
		case BUSINESSSERVICE_SERVICE_PLAN_DEMACATION:

			List<ServicePlanRequest> servicePlanRequestSearch1 = servicePlanService.searchServicePlan(loiNumber,
					applicationNumber, requestInfo);
			for (ServicePlanRequest servicePlanRequest : servicePlanRequestSearch1) {
				application = servicePlanRequest.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;
		case BUSINESSSERVICE_REVISED:

//				builder = new StringBuilder(queryRevisedPlan);
//				resultRevisedPlan = namedParameterJdbcTemplate.query(builder.toString(), revisedLayoutPlanRowMapper);
			resultRevisedPlan = revisedPlanServices.search(requestInfo, applicationNumber, licenceNum);
			for (RevisedPlan RevisedPlan : resultRevisedPlan) {

				application = RevisedPlan.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;
		case BUSINESSSERVICE_ELECTRICAL_PLAN:

//				builder = new StringBuilder(queryServicePlan);
//				resultElectricPlan = namedParameterJdbcTemplate.query(builder.toString(), epRowMapper);
			resultElectricPlan = electricPlanService.searchElectricPlan(loiNumber, applicationNumber, requestInfo);
			for (ElectricPlanRequest electricPlanRequest : resultElectricPlan) {

				application = electricPlanRequest.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;
		case BUSINESSSERVICE_TRANSFER:

//				builder = new StringBuilder(queryTransfer);
//				resultTransfer = namedParameterJdbcTemplate.query(builder.toString(), transferRowMapper);
			resultTransfer = transferOfLicenseServices.search(requestInfo, loiNumber, applicationNumber);
			for (Transfer transfer : resultTransfer) {

				application = transfer.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);
			break;
		case BUSINESSSERVICE_APPROVAL_OF_STANDARD:

//				builder = new StringBuilder(queryAppprovalOfStandard);
//				resultApproval = namedParameterJdbcTemplate.query(builder.toString(), approvalStandardRowMapper);
			resultApproval = approvalStandardService.searchApprovalStandard(requestInfo, loiNumber, applicationNumber);
			for (ApprovalStandardEntity approvalStandardEntity : resultApproval) {

				application = approvalStandardEntity.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;

		case BUSINESSSERVICE_SURRENDOFLICENSE:

//				builder = new StringBuilder(querySurrenderOfLicence);
//				resultSurrender = namedParameterJdbcTemplate.query(builder.toString(), surrendOfLicenseRowMapper);
			resultSurrender = surrendOfLicenseServices.search(requestInfo, loiNumber, applicationNumber);
			for (SurrendOfLicense surrendOfLicense : resultSurrender) {

				application = surrendOfLicense.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;
		case BUSINESSSERVICE_COMPLETION_CERTIFICATE:

			// resultCompletion = completionCertificateRepo.getCompletionApplication();

			for (CompletionCertificate completionCertificate : resultCompletion) {

				application = completionCertificate.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;
		case BUSINESSSERVICE_EXTENSIONOFCLUPERMISSION:
			//
//				builder = new StringBuilder(queryExtensionClu);
//				resultExtensionOfCLUPermission = namedParameterJdbcTemplate.query(builder.toString(),
//						extensionOfCLUPermissionRowMapper);
			resultExtensionOfCLUPermission = extensionOfCLUPermissionServices.search(requestInfo, applicationNumber, applicationNumber);
			for (ExtensionOfCLUPermission extensionOfCLUPermission : resultExtensionOfCLUPermission) {

				application = extensionOfCLUPermission.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;

		case BUSINESSSERVICE_CONSTRUCTION_OF_COMMUNITY:

			// resultConstruction = constructionOfCommunityRepo.getApplicationNumber();
			for (ConstructionOfCommunity constructionOfCommunity : resultConstruction) {

				application = constructionOfCommunity.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;
		case BUSINESSSERVICE_CHANGEBENEFICIAL:
			// resultChangeinBeneficial = changeBeneficialRepo.getchangeInbeneficial();
			for (ChangeBeneficial changeBeneficial : resultChangeinBeneficial) {

				application = changeBeneficial.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;
		}

		return finalResult;
	}

}
