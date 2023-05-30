package org.egov.tl.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.abm.repo.ChangeBeneficialRepo;
import org.egov.tl.repository.TLRepository;
import org.egov.tl.web.models.ElectricPlanRequest;
import org.egov.tl.web.models.ExtensionOfCLUPermission;
import org.egov.tl.web.models.RevisedPlan;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.SurrendOfLicense;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.Transfer;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import static org.egov.tl.util.TLConstants.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GetServices {

	@Autowired
	ChangeBeneficialRepo changeBeneficialRepo;
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
		String tableName;

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
		case SPNAMEVALUE:

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
		case SPNAMEVALUE_DEMARCATION:

			List<ServicePlanRequest> servicePlanRequestSearch1 = servicePlanService.searchServicePlan(loiNumber,
					applicationNumber, requestInfo);
			for (ServicePlanRequest servicePlanRequest : servicePlanRequestSearch1) {
				application = servicePlanRequest.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;
		case businessService_Revised:

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
		case EPNAMEVALUE:

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
		case businessService_TRANSFER:

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
		case ASNAMEVALUE:

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

		case SURRENDER_OF_LICENSE:

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
		case COMPLETION_CERTIFICATE_WORKFLOWCODE:

			tableName = "public.eg_tl_completion_certificate";
			finalResult = changeBeneficialRepo.getTcpApplicationNumberListByUserUUID(tableName, requestInfo.getUserInfo().getUuid());

			log.info("applicationNumber:" + finalResult);

			break;
		case EXTENTION_OF_CLU_PERMISSION:

			resultExtensionOfCLUPermission = extensionOfCLUPermissionServices.search(requestInfo, applicationNumber,
					applicationNumber);
			for (ExtensionOfCLUPermission extensionOfCLUPermission : resultExtensionOfCLUPermission) {

				application = extensionOfCLUPermission.getApplicationNumber();
				if (application != null)
					finalResult.add(application);
			}
			log.info("applicationNumber:" + finalResult);

			break;

		case CONSTRUCTION_OF_COMMUNITY_WORKFLOWCODE:

			tableName = "public.eg_tl_construction_Of_community";
			finalResult = changeBeneficialRepo.getTcpApplicationNumberListByUserUUID(tableName, requestInfo.getUserInfo().getUuid());
			log.info("applicationNumber:" + finalResult);

			break;
		case CHANGE_BENEFICIAL_WORKFLOWCODE:
			tableName = "public.eg_tl_change_beneficial";
			finalResult = changeBeneficialRepo.getTcpApplicationNumberListByUserUUID(tableName, requestInfo.getUserInfo().getUuid());
			log.info("applicationNumber:" + finalResult);

			break;
		}

		return finalResult;
	}

}
