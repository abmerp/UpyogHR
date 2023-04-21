package org.egov.tl.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.abm.repo.ChangeBeneficialRepo;
import org.egov.tl.abm.repo.CompletionCertificateRepo;
import org.egov.tl.abm.repo.ConstructionOfCommunityRepo;
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
import org.egov.tl.web.models.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GetServices {
	@Autowired
	private RevisedLayoutPlanRowMapper revisedLayoutPlanRowMapper;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	CompletionCertificateRepo completionCertificateRepo;
	@Autowired
	private SPRowMapper spRowMapper;

	@Autowired
	private EPRowMapper epRowMapper;
	@Autowired
	private TransferRowMapper transferRowMapper;
	@Autowired
	private ApprovalStandardRowMapper approvalStandardRowMapper;
	@Autowired
	private SurrendOfLicenseRowMapper surrendOfLicenseRowMapper;
	@Autowired
	private ExtensionOfCLUPermissionRowMapper extensionOfCLUPermissionRowMapper;
	@Autowired
	private ConstructionOfCommunityRepo constructionOfCommunityRepo;
	@Autowired
	private ChangeBeneficialRepo changeBeneficialRepo;
	
	
	private static final String APPLICATION_NUMBER = "APPLICATIONNUMBER";
	private static final String LICENCE_NUMBER = "LICENCENUMBER";
	private static final String LOI_NUMBER = "LOINUMBER";
	
	
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
		String licenceNum;
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
		String queryServicePlan = "select * from public.eg_service_plan";
		String queryRevisedPlan = "select * from public.eg_revised_layout_plan";
		String queryElectricPlan = "select * from public.eg_electric_plan";
		String queryTransfer = "select * from public.eg_transfer_of_licence_service";
		String queryAppprovalOfStandard = "select * from public.eg_approval_standard";
		String querySurrenderOfLicence = "select * from public.eg_surrend_of_license";
		String queryExtensionClu = "select * from public.eg_extension_of_clu_permission";
		StringBuilder builder;
		switch (businessService) {
		case BUSINESS_NEWTL:
			switch (type) {
			case APPLICATION_NUMBER:
				Result = completionCertificateRepo.getApplication();
				log.info("result:\t" + Result);

				for (TradeLicense tradeLicense : Result) {

					application = tradeLicense.getApplicationNumber();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;
			case LICENCE_NUMBER:
				Result = completionCertificateRepo.getApplication();
				log.info("result:\t" + Result);

				for (TradeLicense tradeLicense : Result) {

					licenceNum = tradeLicense.getLicenseNumber();
					if (licenceNum != null)
						finalResult.add(licenceNum);
				}
				log.info("licenceNumber:" + finalResult);
				break;
			case LOI_NUMBER:
				Result = completionCertificateRepo.getApplication();
				log.info("result:\t" + Result);

				for (TradeLicense tradeLicense : Result) {

					loiNum = tradeLicense.getTcpLoiNumber();
					if (loiNum != null)
						finalResult.add(loiNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;
		case BUSINESS_SERVICE_PLAN:
			switch (type) {
			case APPLICATION_NUMBER:

				builder = new StringBuilder(queryServicePlan);
				ResultservicePlan = namedParameterJdbcTemplate.query(builder.toString(), spRowMapper);

				for (ServicePlanRequest servicePlanRequest : ResultservicePlan) {

					application = servicePlanRequest.getApplicationNumber();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;

			case LOI_NUMBER:
				builder = new StringBuilder(queryServicePlan);
				ResultservicePlan = namedParameterJdbcTemplate.query(builder.toString(), spRowMapper);

				for (ServicePlanRequest servicePlanRequest : ResultservicePlan) {

					loiNum = servicePlanRequest.getLoiNumber();
					if (loiNum != null)
						finalResult.add(loiNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;
		case BUSINESSSERVICE_SERVICE_PLAN_DEMACATION:
			switch (type) {
			case APPLICATION_NUMBER:

				builder = new StringBuilder(queryServicePlan);
				ResultservicePlan = namedParameterJdbcTemplate.query(builder.toString(), spRowMapper);

				for (ServicePlanRequest servicePlanRequest : ResultservicePlan) {

					application = servicePlanRequest.getApplicationNumber();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;

			case LOI_NUMBER:
				builder = new StringBuilder(queryServicePlan);
				ResultservicePlan = namedParameterJdbcTemplate.query(builder.toString(), spRowMapper);

				for (ServicePlanRequest servicePlanRequest : ResultservicePlan) {

					loiNum = servicePlanRequest.getLoiNumber();
					if (loiNum != null)
						finalResult.add(loiNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;
		case BUSINESSSERVICE_REVISED:
			switch (type) {
			case APPLICATION_NUMBER:

				builder = new StringBuilder(queryRevisedPlan);
				resultRevisedPlan = namedParameterJdbcTemplate.query(builder.toString(), revisedLayoutPlanRowMapper);

				for (RevisedPlan RevisedPlan : resultRevisedPlan) {

					application = RevisedPlan.getApplicationNumber();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;

			case LICENCE_NUMBER:
				builder = new StringBuilder(queryRevisedPlan);
				resultRevisedPlan = namedParameterJdbcTemplate.query(builder.toString(), revisedLayoutPlanRowMapper);

				for (RevisedPlan RevisedPlan : resultRevisedPlan) {

					licenceNum = RevisedPlan.getLicenseNo();
					if (licenceNum != null)
						finalResult.add(licenceNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;
		case BUSINESSSERVICE_ELECTRICAL_PLAN:
			switch (type) {
			case APPLICATION_NUMBER:

				builder = new StringBuilder(queryServicePlan);
				resultElectricPlan = namedParameterJdbcTemplate.query(builder.toString(), epRowMapper);

				for (ElectricPlanRequest electricPlanRequest : resultElectricPlan) {

					application = electricPlanRequest.getApplicationNumber();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;

			case LOI_NUMBER:
				builder = new StringBuilder(queryElectricPlan);
				resultElectricPlan = namedParameterJdbcTemplate.query(builder.toString(), epRowMapper);

				for (ElectricPlanRequest electricPlanRequest : resultElectricPlan) {

					loiNum = electricPlanRequest.getLoiNumber();
					if (loiNum != null)
						finalResult.add(loiNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;
		case BUSINESSSERVICE_TRANSFER:
			switch (type) {
			case APPLICATION_NUMBER:

				builder = new StringBuilder(queryTransfer);
				resultTransfer = namedParameterJdbcTemplate.query(builder.toString(), transferRowMapper);

				for (Transfer transfer : resultTransfer) {

					application = transfer.getApplicationNumber();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;

			case LICENCE_NUMBER:
				builder = new StringBuilder(queryTransfer);
				resultTransfer = namedParameterJdbcTemplate.query(builder.toString(), transferRowMapper);

				for (Transfer transfer : resultTransfer) {

					licenceNum = transfer.getLicenseNo();
					if (licenceNum != null)
						finalResult.add(licenceNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;
		case BUSINESSSERVICE_APPROVAL_OF_STANDARD:
			switch (type) {
			case APPLICATION_NUMBER:

				builder = new StringBuilder(queryAppprovalOfStandard);
				resultApproval = namedParameterJdbcTemplate.query(builder.toString(), approvalStandardRowMapper);

				for (ApprovalStandardEntity approvalStandardEntity : resultApproval) {

					application = approvalStandardEntity.getApplicationNumber();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;

			case LICENCE_NUMBER:
				builder = new StringBuilder(queryAppprovalOfStandard);
				resultApproval = namedParameterJdbcTemplate.query(builder.toString(), approvalStandardRowMapper);

				for (ApprovalStandardEntity approvalStandardEntity : resultApproval) {

					licenceNum = approvalStandardEntity.getLicenseNo();
					if (licenceNum != null)
						finalResult.add(licenceNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;
		case BUSINESSSERVICE_SURRENDOFLICENSE:
			switch (type) {
			case APPLICATION_NUMBER:

				builder = new StringBuilder(querySurrenderOfLicence);
				resultSurrender = namedParameterJdbcTemplate.query(builder.toString(), surrendOfLicenseRowMapper);

				for (SurrendOfLicense surrendOfLicense : resultSurrender) {

					application = surrendOfLicense.getApplicationNumber();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;

			case LICENCE_NUMBER:
				builder = new StringBuilder(querySurrenderOfLicence);
				resultSurrender = namedParameterJdbcTemplate.query(builder.toString(), surrendOfLicenseRowMapper);

				for (SurrendOfLicense surrendOfLicense : resultSurrender) {

					licenceNum = surrendOfLicense.getLicenseNo();
					if (licenceNum != null)
						finalResult.add(licenceNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;
		case BUSINESSSERVICE_COMPLETION_CERTIFICATE:
			switch (type) {
			case APPLICATION_NUMBER:

				resultCompletion = completionCertificateRepo.getCompletionApplication();

				for (CompletionCertificate completionCertificate : resultCompletion) {

					application = completionCertificate.getApplicationNumber();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;

			case LICENCE_NUMBER:
				resultCompletion = completionCertificateRepo.getCompletionApplication();

				for (CompletionCertificate completionCertificate : resultCompletion) {

					licenceNum = completionCertificate.getLicenseNumber();
					if (licenceNum != null)
						finalResult.add(licenceNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;
		case BUSINESSSERVICE_EXTENSIONOFCLUPERMISSION:
			switch (type) {
			case APPLICATION_NUMBER:

				builder = new StringBuilder(queryExtensionClu);
				resultExtensionOfCLUPermission = namedParameterJdbcTemplate.query(builder.toString(),
						extensionOfCLUPermissionRowMapper);

				for (ExtensionOfCLUPermission extensionOfCLUPermission : resultExtensionOfCLUPermission) {

					application = extensionOfCLUPermission.getApplicationNo();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;

			case LICENCE_NUMBER:
				builder = new StringBuilder(queryExtensionClu);
				resultExtensionOfCLUPermission = namedParameterJdbcTemplate.query(builder.toString(),
						extensionOfCLUPermissionRowMapper);

				for (ExtensionOfCLUPermission extensionOfCLUPermission : resultExtensionOfCLUPermission) {

					licenceNum = extensionOfCLUPermission.getLicenseNo();
					if (licenceNum != null)
						finalResult.add(licenceNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;

		case BUSINESSSERVICE_CONSTRUCTION_OF_COMMUNITY:
			switch (type) {
			case APPLICATION_NUMBER:

				resultConstruction = constructionOfCommunityRepo.getApplicationNumber();
				for (ConstructionOfCommunity constructionOfCommunity : resultConstruction) {

					application = constructionOfCommunity.getApplicationNumber();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;

			case LICENCE_NUMBER:
				resultConstruction = constructionOfCommunityRepo.getApplicationNumber();
				for (ConstructionOfCommunity constructionOfCommunity : resultConstruction) {

					licenceNum = constructionOfCommunity.getLicenseNumber();
					if (licenceNum != null)
						finalResult.add(licenceNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;
		case BUSINESSSERVICE_CHANGEBENEFICIAL:
			switch (type) {
			case APPLICATION_NUMBER:

				resultChangeinBeneficial = changeBeneficialRepo.getchangeInbeneficial();
				for (ChangeBeneficial changeBeneficial : resultChangeinBeneficial) {

					application = changeBeneficial.getApplicationNumber();
					if (application != null)
						finalResult.add(application);
				}
				log.info("applicationNumber:" + finalResult);

				break;

			case LICENCE_NUMBER:
				resultChangeinBeneficial = changeBeneficialRepo.getchangeInbeneficial();
				for (ChangeBeneficial changeBeneficial : resultChangeinBeneficial) {

					licenceNum = changeBeneficial.getLicenseNumber();
					if (licenceNum != null)
						finalResult.add(licenceNum);
				}
				log.info("loiNumber:" + finalResult);
				break;
			}
			break;
		}

		return finalResult;
	}

}
