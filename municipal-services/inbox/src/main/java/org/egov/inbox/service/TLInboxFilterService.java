package org.egov.inbox.service;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.inbox.repository.ServiceRequestRepository;
import org.egov.inbox.util.BpaConstants;
import org.egov.inbox.web.model.InboxSearchCriteria;
import org.egov.inbox.web.model.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.egov.inbox.util.BpaConstants.BPAREG;
import static org.egov.inbox.util.BpaConstants.MOBILE_NUMBER_PARAM;
import static org.egov.inbox.util.TLConstants.*;

@Slf4j
@Service
public class TLInboxFilterService {

	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	@Value("${egov.searcher.host}")
	private String searcherHost;

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
	
//	SURREND OF LICENSE
	@Value("${egov.searcher.tl.Surrend.of.license.search.path}")
	private String SurrendOfLicenseSearcherEndPoint;
	@Value("${egov.searcher.tl.Surrend.of.license.count.path}")
	private String SurrendOfLicenseSearcherCountEndPoint;
	@Value("${egov.searcher.tl.Surrend.of.license.search.desc.path}")
	private String SurrendOfLicenseSearcherDescEndPoint;
	private static final String BUSINESSSERVICE_SURRENDOFLICENSE = "SURREND_OF_LICENSE";
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	private static final String BUSINESSSERVICE_CHANGEBENEFICIAL = "CHANGE_OF_BENEFICIAL";
	private static final String BUSINESSSERVICE_COMPLETION_CERTIFICATE = "COMPLETION_CERTIFICATE";
	
	private static final String BUSINESSSERVICE_TRANSFER = "TRANSFER_OF_LICIENCE";
	private static final String BUSINESSSERVICE_RENEWAL = "RENWAL_OF_LICENCE";
	private static final String BUSINESSSERVICE_REVISED = "REVISED_LAYOUT_PLAN";
	private static final String BUSINESSSERVICE_NEWTL = "NewTL";
	private static final String BUSINESSSERVICE_BG_NEW = "BG_NEW";
	private static final String BUSINESSSERVICE_BG_MORTGAGE = "BG_MORTGAGE";

	private static final String BUSINESSSERVICE_SERVICE_PLAN = "SERVICE_PLAN";

	private static final String BUSINESSSERVICE_ELECTRICAL_PLAN = "ELECTRICAL_PLAN";

	private static final String BUSINESSSERVICE_SERVICE_PLAN_DEMACATION = "SERVICE_PLAN_DEMARCATION";
	private static final String BUSINESSSERVICE_APPROVAL_OF_STANDARD = "APPROVAL_OF_STANDARD";

	public List<String> fetchApplicationNumbersFromSearcher(InboxSearchCriteria criteria,
			HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
		List<String> acknowledgementNumbers = new ArrayList<>();
		HashMap moduleSearchCriteria = criteria.getModuleSearchCriteria();
		ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
		Boolean isSearchResultEmpty = false;
		Boolean isMobileNumberPresent = false;
		List<String> userUUIDs = new ArrayList<>();
		moduleSearchCriteria = setUserWhenMobileNoIsEmptyForStakeholderRegOfCitizen(criteria, requestInfo,
				moduleSearchCriteria, processCriteria);
		if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)) {
			isMobileNumberPresent = true;
		}
		if (isMobileNumberPresent) {
			String tenantId = criteria.getTenantId();
			String mobileNumber = String.valueOf(moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
			userUUIDs = fetchUserUUID(mobileNumber, requestInfo, tenantId);
			Boolean isUserPresentForGivenMobileNumber = CollectionUtils.isEmpty(userUUIDs) ? false : true;
			isSearchResultEmpty = !isMobileNumberPresent || !isUserPresentForGivenMobileNumber;
			if (isSearchResultEmpty) {
				return new ArrayList<>();
			}
		}

		if (!isSearchResultEmpty) {
			Object result = null;

			Map<String, Object> searcherRequest = new HashMap<>();
			Map<String, Object> searchCriteria = new HashMap<>();

			searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
			searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

			// Accomodating module search criteria in searcher request
			if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM) && !CollectionUtils.isEmpty(userUUIDs)) {
				searchCriteria.put(USERID_PARAM, userUUIDs);
			}
			if (moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
				searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
			}
			if (moduleSearchCriteria.containsKey(LICENSE_NUMBER_PARAM)) {
				searchCriteria.put(LICENSE_NUMBER_PARAM, moduleSearchCriteria.get(LICENSE_NUMBER_PARAM));
			}
			if (moduleSearchCriteria.containsKey(APPLICATION_NUMBER_PARAM)) {
				searchCriteria.put(APPLICATION_NUMBER_PARAM, moduleSearchCriteria.get(APPLICATION_NUMBER_PARAM));
			}

			// Accomodating process search criteria in searcher request
			if (!ObjectUtils.isEmpty(processCriteria.getAssignee())) {
				searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
			}
			if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
				searchCriteria.put(STATUS_PARAM, processCriteria.getStatus());
			} else {
				if (StatusIdNameMap.values().size() > 0) {
					if (CollectionUtils.isEmpty(processCriteria.getStatus())) {
						searchCriteria.put(STATUS_PARAM, StatusIdNameMap.keySet());
					}
				}
			}

			// Paginating searcher results
			searchCriteria.put(OFFSET_PARAM, criteria.getOffset());
			searchCriteria.put(NO_OF_RECORDS_PARAM, criteria.getLimit());
			moduleSearchCriteria.put(LIMIT_PARAM, criteria.getLimit());

			searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
			searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

			StringBuilder uri = new StringBuilder();
			log.info("*******criteria*****" + criteria.getProcessSearchCriteria().getBusinessService());
			// switch case:
			if (Objects.nonNull(criteria.getProcessSearchCriteria().getBusinessService())
					&& !criteria.getProcessSearchCriteria().getBusinessService().isEmpty()) {
				// assumption: only one businessService will be sent in searcher as multiple
				// will have different search endpoints
				String businessService = criteria.getProcessSearchCriteria().getBusinessService().get(0);
				switch (businessService) {
				case BUSINESSSERVICE_SURRENDOFLICENSE:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(SurrendOfLicenseSearcherDescEndPoint);
					} else {
						uri.append(searcherHost).append(SurrendOfLicenseSearcherEndPoint);
						log.info("search for application no url" + uri);
					}
					break;
				case BUSINESSSERVICE_CHANGEBENEFICIAL:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(changeOfBeneficialSearcherDescEndPoint);
					} else {
						uri.append(searcherHost).append(changeOfBeneficialSearcherEndPoint);
						log.info("search for application no url" + uri);
					}
					break;	
				case BUSINESSSERVICE_COMPLETION_CERTIFICATE:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(completionCertificateSearcherDescEndPoint);
					} else {
						uri.append(searcherHost).append(completionCertificateSearcherEndPoint);
						log.info("search for application no url" + uri);
					}
					break;	
				case BUSINESSSERVICE_TRANSFER:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(transferOfLicenceSearcherDescEndPoint);
					} else {
						uri.append(searcherHost).append(transferOfLicenceSearcherEndPoint);
						log.info("search for application no url" + uri);
					}
					break;
				case BUSINESSSERVICE_REVISED:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(revisedLayoutPlanSearcherDescEndPoint);
					} else {
						uri.append(searcherHost).append(revisedLayoutPlanSearcherEndPoint);
						log.info("search for application no url" + uri);
					}
					break;
				case BUSINESSSERVICE_RENEWAL:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(renewalOfLicenceSearcherDescEndPoint);
					} else {
						uri.append(searcherHost).append(renewalOfLicenceSearcherEndPoint);
						log.info("search for application no url" + uri);
					}
					break;
				case BUSINESSSERVICE_NEWTL:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(tlInboxSearcherDescEndpoint);
					} else {
						uri.append(searcherHost).append(tlInboxSearcherEndpoint);
						log.info("search for application no url" + uri);
					}
					break;
				case BUSINESSSERVICE_BG_NEW:
					break;
				case BUSINESSSERVICE_BG_MORTGAGE:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(newBankGuaranteeSearcherDescEndpoint);
					} else {
						uri.append(searcherHost).append(newBankGuaranteeSearcherEndpoint);
						log.info("search for application no url" + uri);
					}

					break;

				case BUSINESSSERVICE_SERVICE_PLAN:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(servicePlaneSearcherDescEndpoint);
					} else {
						uri.append(searcherHost).append(servicePlanSearcherEndpoint);
						log.info("search for application no url" + uri);
					}

					break;

				case BUSINESSSERVICE_SERVICE_PLAN_DEMACATION:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(servicePlaneSearcherDescEndpoint);
					} else {
						uri.append(searcherHost).append(servicePlanSearcherEndpoint);
						log.info("search for application no url" + uri);
					}

					break;

				case BUSINESSSERVICE_ELECTRICAL_PLAN:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(electricPlaneSearcherDescEndpoint);
					} else {
						uri.append(searcherHost).append(electricPlanSearcherEndpoint);
						log.info("search for application no url" + uri);
					}

					break;
				case BUSINESSSERVICE_APPROVAL_OF_STANDARD:
					if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
							&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
						uri.append(searcherHost).append(approvalStandardSearcherDescEndPoint);
					} else {
						uri.append(searcherHost).append(approvalStandardSearcherEndPoint);
						log.info("search for application no url" + uri);
					}
					break;
				}
			}

			result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);
			acknowledgementNumbers = JsonPath.read(result, "$.Licenses.*.applicationnumber");

		}
		return acknowledgementNumbers;
	}

	private HashMap setUserWhenMobileNoIsEmptyForStakeholderRegOfCitizen(InboxSearchCriteria criteria,
			RequestInfo requestInfo, HashMap moduleSearchCriteria, ProcessInstanceSearchCriteria processCriteria) {
		List<String> roles = requestInfo.getUserInfo().getRoles().stream().map(Role::getCode)
				.collect(Collectors.toList());
		if (processCriteria.getModuleName().equals(BPAREG) && roles.contains(BpaConstants.CITIZEN)) {
			if (moduleSearchCriteria == null || moduleSearchCriteria.isEmpty()
					|| !moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)) {
				moduleSearchCriteria = new HashMap<>();
				moduleSearchCriteria.put(MOBILE_NUMBER_PARAM, requestInfo.getUserInfo().getMobileNumber());
				criteria.setModuleSearchCriteria(moduleSearchCriteria);
			}
		}
		return moduleSearchCriteria;
	}

	public Integer fetchApplicationCountFromSearcher(InboxSearchCriteria criteria,
			HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
		Integer totalCount = 0;
		HashMap moduleSearchCriteria = criteria.getModuleSearchCriteria();
		ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
		Boolean isSearchResultEmpty = false;
		Boolean isMobileNumberPresent = false;
		List<String> userUUIDs = new ArrayList<>();
		moduleSearchCriteria = setUserWhenMobileNoIsEmptyForStakeholderRegOfCitizen(criteria, requestInfo,
				moduleSearchCriteria, processCriteria);
		if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)) {
			isMobileNumberPresent = true;
		}
		if (isMobileNumberPresent) {
			String tenantId = criteria.getTenantId();
			String mobileNumber = String.valueOf(moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
			userUUIDs = fetchUserUUID(mobileNumber, requestInfo, tenantId);
			Boolean isUserPresentForGivenMobileNumber = CollectionUtils.isEmpty(userUUIDs) ? false : true;
			isSearchResultEmpty = !isMobileNumberPresent || !isUserPresentForGivenMobileNumber;
			if (isSearchResultEmpty) {
				return 0;
			}
		}

		if (!isSearchResultEmpty) {
			Object result = null;

			Map<String, Object> searcherRequest = new HashMap<>();
			Map<String, Object> searchCriteria = new HashMap<>();

			searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());

			// Accomodating module search criteria in searcher request
			if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM) && !CollectionUtils.isEmpty(userUUIDs)) {
				searchCriteria.put(USERID_PARAM, userUUIDs);
			}
			if (moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
				searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
			}
			if (moduleSearchCriteria.containsKey(LICENSE_NUMBER_PARAM)) {
				searchCriteria.put(LICENSE_NUMBER_PARAM, moduleSearchCriteria.get(LICENSE_NUMBER_PARAM));
			}
			if (moduleSearchCriteria.containsKey(APPLICATION_NUMBER_PARAM)) {
				searchCriteria.put(APPLICATION_NUMBER_PARAM, moduleSearchCriteria.get(APPLICATION_NUMBER_PARAM));
			}

			// Accomodating process search criteria in searcher request
			if (!ObjectUtils.isEmpty(processCriteria.getAssignee())) {
				searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
			}
			if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
				searchCriteria.put(STATUS_PARAM, processCriteria.getStatus());
			} else {
				if (StatusIdNameMap.values().size() > 0) {
					if (CollectionUtils.isEmpty(processCriteria.getStatus())) {
						searchCriteria.put(STATUS_PARAM, StatusIdNameMap.keySet());
					}
				}
			}

			searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
			searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);
			StringBuilder uri = new StringBuilder();
			if (Objects.nonNull(criteria.getProcessSearchCriteria().getBusinessService())
					&& !criteria.getProcessSearchCriteria().getBusinessService().isEmpty()) {
				// assumption: only one businessService will be sent in searcher as multiple
				// will have different search endpoints
				String businessService = criteria.getProcessSearchCriteria().getBusinessService().get(0);

				switch (businessService) {
				case BUSINESSSERVICE_SURRENDOFLICENSE:
					uri.append(searcherHost).append(SurrendOfLicenseSearcherCountEndPoint);
					log.info("uri searcher\t" + uri);
					break;
				case BUSINESSSERVICE_CHANGEBENEFICIAL:
					uri.append(searcherHost).append(changeOfBeneficialSearcherCountEndPoint);
					log.info("uri searcher\t" + uri);
					break;
				case BUSINESSSERVICE_COMPLETION_CERTIFICATE:
					uri.append(searcherHost).append(completionCertificateSearcherCountEndPoint);
					log.info("uri searcher\t" + uri);
					break;
				case BUSINESSSERVICE_TRANSFER:
					uri.append(searcherHost).append(transferOfLicenceSearcherCountEndPoint);
					log.info("uri searcher\t" + uri);
					break;
				case BUSINESSSERVICE_REVISED:
					uri.append(searcherHost).append(revisedLayoutPlanSearcherCountEndPoint);
					log.info("uri searcher\t" + uri);
					break;
				case BUSINESSSERVICE_RENEWAL:
					uri.append(searcherHost).append(renewalOfLicenceSearcherCountEndPoint);
					log.info("uri searcher\t" + uri);

					break;

				case BUSINESSSERVICE_NEWTL:
					uri.append(searcherHost).append(tlInboxSearcherCountEndpoint);
					log.info("uri searcher\t" + uri);

					break;
				case BUSINESSSERVICE_BG_NEW:
					break;
				case BUSINESSSERVICE_BG_MORTGAGE:

					uri.append(searcherHost).append(newBankGuaranteeSearcherCountEndpoint);
					log.info("uri searcher\t" + uri);

					break;

				case BUSINESSSERVICE_SERVICE_PLAN:

					uri.append(searcherHost).append(servicePlanSearcherCountEndpoint);
					log.info("uri searcher\t" + uri);

					break;

				case BUSINESSSERVICE_SERVICE_PLAN_DEMACATION:

					uri.append(searcherHost).append(servicePlanSearcherCountEndpoint);
					log.info("uri searcher\t" + uri);

					break;

				case BUSINESSSERVICE_ELECTRICAL_PLAN:

					uri.append(searcherHost).append(electricPlanSearcherCountEndpoint);
					log.info("uri searcher\t" + uri);

					break;
				case BUSINESSSERVICE_APPROVAL_OF_STANDARD:
					uri.append(searcherHost).append(approvalStandardSearcherCountEndPoint);
					log.info("uri searcher\t" + uri);
					break;
				}
			}

			result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

			double count = JsonPath.read(result, "$.TotalCount[0].count");
			totalCount = new Integer((int) count);
		}
		return totalCount;
	}

	private List<String> fetchUserUUID(String mobileNumber, RequestInfo requestInfo, String tenantId) {
		StringBuilder uri = new StringBuilder();
		uri.append(userHost).append(userSearchEndpoint);
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("userType", "CITIZEN");
		userSearchRequest.put("mobileNumber", mobileNumber);
		List<String> userUuids = new ArrayList<>();
		try {
			Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
			if (null != user) {
				// log.info(user.toString());
				userUuids = JsonPath.read(user, "$.user.*.uuid");
			} else {
				log.error("Service returned null while fetching user for mobile number - " + mobileNumber);
			}
		} catch (Exception e) {
			log.error("Exception while fetching user for mobile number - " + mobileNumber);
			log.error("Exception trace: ", e);
		}
		return userUuids;
	}

}
