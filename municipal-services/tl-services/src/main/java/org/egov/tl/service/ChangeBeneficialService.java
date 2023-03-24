package org.egov.tl.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tl.abm.repo.ChangeBeneficialRepo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.service.repo.LicenseServiceRepo;
import org.egov.tl.util.LandUtil;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.validator.LandMDMSValidator;
import org.egov.tl.web.models.CalculatorRequest;
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.ChangeBeneficialResponse;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.ResponseTransaction;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.UserResponse;
import org.egov.tl.web.models.UserSearchCriteria;
import org.egov.tl.web.models.calculation.CalulationCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.ibm.icu.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChangeBeneficialService {
	
	@Value("${tcp.payment.host}")
	private String paymentHost;
	
	@Value("${citizen.payment.success}")
	private String paymentSuccess;
	@Value("${egov.pg-service.host}")
	private String pgHost;
	@Value("${egov.pg-service.path}")
	private String pgpath;
	@Value("${egov.pg-service.search.path}")
	private String pgSearchPath;
	@Value("${egov.tl.calculator.gurantee.endpoint}")
	private String guranteeEndPoint;
	
	@Value("${egov.user.host}")
	private String userHost;
	@Value("${egov.user.search.path}")
	private String userSearchPath;

	@Value("${egov.pg-service.path}")
	private String updatePath;
	
	@Value("${egov.pg-service.create.transaction.path}")
	private String transactionCreatePath;

	@Autowired
	LandUtil landUtil;

	@Autowired
	RestTemplate rest;

	@Autowired
	TLConfiguration config;

	@Autowired
	LandMDMSValidator valid;

	@Autowired
	private ThirPartyAPiCall thirPartyAPiCall;
	@Autowired
	LicenseServiceRepo newServiceInfoRepo;
	
	@Autowired
	EntityManager em;
	
	@Autowired
	TradeLicenseService tradeLicenseService;

	@Autowired
	SimpleUrlBrowser simpleUrlBrowser;
	
	@Autowired
	BankGuaranteeService bankGuaranteeService;
	@Autowired
	ServicePlanService servicePlanService;
	
	
	@Autowired
	private ChangeBeneficialRepo changeBeneficialRepo;
	
	
	@Autowired
	private TradeLicenseService tradeLicensesService;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Value("${egov.tl.calculator.host}")
	private String guranteeHost;
	
	@Value("${egov.tl.calculator.access.calculate.endpoint}")
	private String accessCalculatorEndPoint;
	
	
	@Value("${egov.billingservice.host}")
    private String billingHost;
	
	@Value("${egov.bill.gen.endpoint}")
	private String fetchBillEndpoint;
	   
		
	String  licenseFee = "0.0";
	

	public ChangeBeneficialResponse createChangeBeneficial(ChangeBeneficialRequest beneficialRequest,
			String applicationNumber) throws JsonProcessingException {
		ChangeBeneficialResponse changeBeneficialResponse = null;
		if(changeBeneficialRepo.getBeneficialByApplicationNumber(applicationNumber)!=null) {
			changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(null)
					.requestInfo(null).message("This Application Number already taken and payment is in pending").status(false).build();
		}else if (changeBeneficialRepo.getLicenseByApplicationNo(applicationNumber,beneficialRequest.getRequestInfo().getUserInfo().getId()) > 0) {
			RequestInfo requestInfo = beneficialRequest.getRequestInfo();
			List<ChangeBeneficial> changeBeneficial = (List<ChangeBeneficial>) beneficialRequest.getChangeBeneficial()
					.stream().map(changebeneficial -> {
						changebeneficial.setDeveloperId(requestInfo.getUserInfo().getId());
						if (!changebeneficial.getDeveloperServiceCode().equals("JDAMR")) {
							changebeneficial.setAreaInAcres(changebeneficial.getAreaInAcres() == null ? ("0.0")
									: (changebeneficial.getAreaInAcres()));
						}
						return changebeneficial;
					}).collect(Collectors.toList());
			beneficialRequest.setChangeBeneficial(changeBeneficial);
			changeBeneficialRepo.save(beneficialRequest);
			changeBeneficialResponse = ChangeBeneficialResponse.builder()
					.changeBeneficial(beneficialRequest.getChangeBeneficial()).requestInfo(requestInfo).message("Success").status(true).build();
		} else {
			changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(null)
					.requestInfo(null).message("Application Number is not existing").status(false).build();
		}
		return changeBeneficialResponse;

	}

	public ChangeBeneficialResponse createChangeBeneficialPay(RequestInfo requestInfo,String applicationNumber,String calculationServiceName,int calculationType,int isIntialPayment)
			throws JsonProcessingException {
		ChangeBeneficialResponse changeBeneficialResponse = null;
		ChangeBeneficial changeBeneficiaDetails = null;
		
		try {
			changeBeneficiaDetails=changeBeneficialRepo.getBeneficialByApplicationNumber(applicationNumber);
			
			if(changeBeneficiaDetails!=null) {
				  TradeLicense tradeLicenses=tradeLicensesService.getLicensesWithOwnerInfo(TradeLicenseSearchCriteria.builder().applicationNumber(applicationNumber).tenantId("hr").build(), requestInfo).get(0);
				  String data = mapper.writeValueAsString(tradeLicenses);
				  System.out.println("data :----------------------"+data);
				  // --------------------------calculation start--------------------------------//
				        StringBuilder calculatorUrl = new StringBuilder(guranteeHost);
						calculatorUrl.append(accessCalculatorEndPoint);
						calculatorUrl.append("?calculationServiceName="+calculationServiceName+"&calculationType="+calculationType+"&isIntialCalculation="+isIntialPayment);
					    System.out.println("url:---"+calculatorUrl);
						CalulationCriteria calulationCriteriaRequest = new CalulationCriteria();
						calulationCriteriaRequest.setTenantId(tradeLicenses.getTenantId());
						calulationCriteriaRequest.setTradelicense(tradeLicenses);
						java.util.List<CalulationCriteria> calulationCriteria = Arrays.asList(calulationCriteriaRequest);

						CalculatorRequest calculator = new CalculatorRequest();
						calculator.setApplicationNumber(applicationNumber);
						
						Map<String, Object> calculatorMap = new HashMap<>();
						calculatorMap.put("CalulationCriteria", calulationCriteria);
						calculatorMap.put("CalculatorRequest", calculator);
						calculatorMap.put("RequestInfo", requestInfo);
						HashMap responseCalculator = serviceRequestRepository.fetchResultJSON(calculatorUrl, calculatorMap);
						List<HashMap> calculatorReqData=(List<HashMap>) responseCalculator.get("Calculations");
						List<HashMap> taxHeadEstimates=(List<HashMap>) calculatorReqData.get(0).get("taxHeadEstimates");
						BigDecimal estimateAmount= (BigDecimal) taxHeadEstimates.get(0).get("estimateAmount");
						
						
						HashMap tradeTypeBillingIds=(HashMap) calculatorReqData.get(0).get("tradeTypeBillingIds");
						List<String> billingSlabIds=(List<String>) tradeTypeBillingIds.get("billingSlabIds");
						String billingId=billingSlabIds.get(0);
						createTranaction(requestInfo,requestInfo.getUserInfo().getId().toString(),tradeLicenses.getTenantId(),estimateAmount,applicationNumber,billingId,"");
								
			}else {
				changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(null)
						.requestInfo(null).message("You have not changed any beneficial status ").status(false).build();
						
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return changeBeneficialResponse;
	}
	
	private UserResponse getUserInfo(String... userId) {
		UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
		userSearchCriteria.setId(Arrays.asList(Long.parseLong(userId[0])));

		userSearchCriteria.setTenantId("hr");

		StringBuilder url = new StringBuilder(userHost);
		url.append(userSearchPath);

		Object searchUser = serviceRequestRepository.fetchResult(url, userSearchCriteria);
		String data1 = null;

		try {
			data1 = mapper.writeValueAsString(searchUser);
		} catch (JsonProcessingException e) { // TODO Auto-generated catch block
			 log.error("JsonProcessingException : "+e.getMessage());
		}
		UserResponse userData = null;
		ObjectReader readerData = mapper.readerFor(new TypeReference<UserResponse>() {
		});
		try {
			userData = readerData.readValue(data1);
		} catch (IOException e) {
		    log.error("IOException : "+e.getMessage());
		}catch (Exception e) {
			log.error("IOException : "+e.getMessage());
    	}
		return userData;

	}
	
	
	public void createTranaction(RequestInfo requestInfo,String userId,String tenantId,BigDecimal amount,String consumerCode,String billId,String callbackUrl) {
		
		UserResponse UserResponse=getUserInfo(userId);
		org.egov.tl.web.models.User user=UserResponse.getUser().get(0);
		StringBuilder url = new StringBuilder(userHost);
		url.append(transactionCreatePath);
		Map<String, Object> taxAndPayments = new HashMap<>();
		taxAndPayments.put("billId",billId);
		taxAndPayments.put("amountPaid",amount);
		Map<String, Object> additionalDetails = new HashMap<>();
		additionalDetails.put("isWhatsapp",false);
		
		Map<String, Object> transaction = new HashMap<>();
		transaction.put("tenantId", tenantId);
		transaction.put("txnAmount", amount);
		transaction.put("bank", "0300997");
		transaction.put("ptype", "101");
		transaction.put("remarks", "Pay");
		transaction.put("address", "haryana");
		transaction.put("pinCode", "160005");
		transaction.put("cityName", "haryana");
		transaction.put("module", "TL");
		transaction.put("billId", billId);
		transaction.put("consumerCode", consumerCode);
		transaction.put("productInfo", "Change Beneficial Payment");
		transaction.put("gateway", "NIC");
		transaction.put("callbackUrl", callbackUrl);
	
		transaction.put("user", user);
		transaction.put("additionalDetails", additionalDetails);
		transaction.put("taxAndPayments", Arrays.asList(taxAndPayments));

		Map<String, Object> transactionReq = new HashMap<>();
		transactionReq.put("Transaction", transaction);
		transactionReq.put("RequestInfo", requestInfo);
		
		HashMap transactionRes=serviceRequestRepository.fetchResultJSON(url, transactionReq);
		if(transactionRes.get("status")!=null&&transactionRes.get("status").toString().toUpperCase().equals("SUCCESSFUL")) {
			simpleUrlBrowser.browse(transactionRes.get("redirectUrl").toString());
		}else {
			simpleUrlBrowser.browse(transactionRes.get("callbackUrl").toString());
		}
				
	}
	
	public ResponseEntity<Object> postTransactionDeatil(MultiValueMap<String, String> requestParam) {

		String transactionId = requestParam.get(new String("Applicationnumber")).get(0);
		String grn = requestParam.get(new String("GRN")).get(0);
		String status = requestParam.get(new String("status")).get(0);
		String CIN = requestParam.get(new String("CIN")).get(0);
		String tdate = requestParam.get(new String("tdate")).get(0);
		String paymentType = requestParam.get(new String("payment_type")).get(0);
		String bankcode = requestParam.get(new String("bankcode")).get(0);
		String amount = requestParam.get(new String("amount")).get(0);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		LocalDateTime localDateTime = LocalDateTime.now();
		String date = formatter.format(localDateTime);
		String dairyNumber;
		String caseNumber;
		String tcpApplicationNmber;
		String saveTransaction;
		String returnURL = "";
		RequestInfo info = new RequestInfo();

		// --------------payment-----------//
		Map<String, Object> request = new HashMap<>();
		request.put("txnId", transactionId);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, httpHeaders);
		Object paymentSearch = null;

		String uri = pgHost + pgSearchPath;
		paymentSearch = rest.postForObject(uri, entity, Map.class);
		log.info("search payment data" + paymentSearch);

		String data = null;
		try {
			data = mapper.writeValueAsString(paymentSearch);
		} catch (JsonProcessingException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		ResponseTransaction transaction = null;
		ObjectReader objectReader = mapper.readerFor(new TypeReference<ResponseTransaction>() {
		});
		try {
			transaction = objectReader.readValue(data);
		} catch (IOException e) {

			e.printStackTrace();
		}

		log.info("transaction" + transaction);
		String txnId = transaction.getTransaction().get(0).getTxnId();
		String applicationNumber = transaction.getTransaction().get(0).getConsumerCode();
		String uuid = transaction.getTransaction().get(0).getUser().getUuid();
		String tennatId = transaction.getTransaction().get(0).getUser().getTenantId();
		String userName = transaction.getTransaction().get(0).getUser().getUserName();

		String paymentUrl;
		String returnPaymentUrl;
		MultiValueMap<String, String> params1 = new LinkedMultiValueMap<>();

		params1.put("eg_pg_txnid", Collections.singletonList(txnId));

		// ------------failure----------------//
		if (!status.isEmpty() && status.equalsIgnoreCase("Success")) {

			paymentUrl = paymentHost + paymentSuccess + "TL" + "/" + applicationNumber + "/" + "hr";
			returnPaymentUrl = paymentUrl + "?" + params1;
			log.info("returnPaymentUrl" + returnPaymentUrl);
			httpHeaders.setLocation(
					UriComponentsBuilder.fromHttpUrl(returnPaymentUrl.toString()).build().encode().toUri());
			return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);

			// --------------success------------------------//
		} else if (!status.isEmpty() && status.equalsIgnoreCase("Failure")) {

			// ------------user search---------------//
			UserSearchCriteria userSearchCriteria = new UserSearchCriteria();

			userSearchCriteria.setUserName(userName);
			userSearchCriteria.setTenantId(tennatId);

			StringBuilder url = new StringBuilder(userHost);
			url.append(userSearchPath);

			Object searchUser = serviceRequestRepository.fetchResult(url, userSearchCriteria);

			log.info("searchUsers" + searchUser);
			String data1 = null;

			try {
				data1 = mapper.writeValueAsString(searchUser);
			} catch (JsonProcessingException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
			UserResponse userData = null;
			ObjectReader readerData = mapper.readerFor(new TypeReference<UserResponse>() {
			});
			try {
				userData = readerData.readValue(data1);
			} catch (IOException e) {

				e.printStackTrace();
			}
			log.info("userData" + userData);

			String type = userData.getUser().get(0).getType().toString();
			String email = userData.getUser().get(0).getEmailId();
			Long userId = userData.getUser().get(0).getId();
			String mobNo = userData.getUser().get(0).getMobileNumber();

			List<Role> roles = new ArrayList<>();
			int length = userData.getUser().get(0).getRoles().size();
			for (int i = 0; i < length; i++) {
				Role role = new Role();
				try {
					role.setCode(userData.getUser().get(0).getRoles().get(i).getCode());
					role.setTenantId(userData.getUser().get(0).getRoles().get(i).getTenantId());
					role.setName(userData.getUser().get(0).getRoles().get(i).getName());
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				roles.add(role);
			}

			User user = new User();

			user.setType(type);
			user.setUuid(uuid);
			user.setTenantId(tennatId);
			user.setRoles(roles);
			info.setUserInfo(user);

			// ------------------user search end----------------//
			TradeLicenseSearchCriteria tradeLicenseRequest = new TradeLicenseSearchCriteria();
			tradeLicenseRequest.setApplicationNumber(applicationNumber);
			List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(tradeLicenseRequest, info);

			for (TradeLicense tradeLicense : tradeLicenses) {

				ObjectReader reader = mapper.readerFor(new TypeReference<List<LicenseDetails>>() {
				});

				Map<String, Object> authtoken = new HashMap<String, Object>();
				authtoken.put("UserId", "39");
				authtoken.put("TpUserId", userId);
				authtoken.put("EmailId", "mkthakur84@gmail.com");

				List<LicenseDetails> newServiceInfoData = null;
				try {
					newServiceInfoData = reader.readValue(tradeLicense.getTradeLicenseDetail().getAdditionalDetail());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (LicenseDetails newobj : newServiceInfoData) {

					if (newobj.getVer() == tradeLicense.getTradeLicenseDetail().getCurrentVersion()) {

						LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeId = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
								.mDMSCallPurposeCode(info, tradeLicense.getTenantId(),
										newobj.getApplicantPurpose().getPurpose());

						Map<String, List<String>> mdmsData;
						mdmsData = valid.getAttributeValues(mDMSCallPurposeId);

						List<Map<String, Object>> msp = (List) mdmsData.get("Purpose");

						int purposeId = 0;

						for (Map<String, Object> mm : msp) {

							purposeId = Integer.valueOf(String.valueOf(mm.get("purposeId")));
							log.info("purposeId" + purposeId);

						}

						Map<String, Object> mapDNo = new HashMap<String, Object>();

						mapDNo.put("Village",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
						mapDNo.put("DiaryDate", date);
						mapDNo.put("ReceivedFrom", userName);
						mapDNo.put("UserId", "1265");
						mapDNo.put("DistrictCode",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDistrict());
						mapDNo.put("UserLoginId", "39");
						dairyNumber = thirPartyAPiCall.generateDiaryNumber(mapDNo, authtoken).getBody().get("Value")
								.toString();
						tradeLicense.setTcpDairyNumber(dairyNumber);

						/****************
						 * End Here
						 ***********/
						// case number
						Map<String, Object> mapCNO = new HashMap<String, Object>();
						mapCNO.put("DiaryNo", dairyNumber);
						mapCNO.put("DiaryDate", date);
						mapCNO.put("DeveloperId", "2");
						mapCNO.put("PurposeId", purposeId);
						mapCNO.put("StartDate", date);
						mapCNO.put("DistrictCode",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDistrict());
						mapCNO.put("Village",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
						mapCNO.put("ChallanAmount", newobj.getFeesAndCharges().getPayableNow());
						mapCNO.put("UserId", "2");
						mapCNO.put("UserLoginId", "39");
						caseNumber = thirPartyAPiCall.generateCaseNumber(mapCNO, authtoken).getBody().get("Value")
								.toString();
						tradeLicense.setTcpCaseNumber(caseNumber);

						/****************
						 * End Here
						 ***********/
						// application number
						Map<String, Object> mapANo = new HashMap<String, Object>();
						mapANo.put("DiaryNo", dairyNumber);
						mapANo.put("DiaryDate", date);
						mapANo.put("TotalArea", newobj.getApplicantPurpose().getTotalArea());
						mapANo.put("Village",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
						mapANo.put("PurposeId", purposeId);
						mapANo.put("NameofOwner",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getLandOwner());
						mapANo.put("DateOfHearing", date);
						mapANo.put("DateForFilingOfReply", date);
						mapANo.put("UserId", "2");
						mapANo.put("UserLoginId", "39");
						tcpApplicationNmber = thirPartyAPiCall.generateApplicationNumber(mapANo, authtoken).getBody()
								.get("Value").toString();
						tradeLicense.setTcpApplicationNumber(tcpApplicationNmber);

						/****************
						 * End Here
						 ***********/
						/****************
						 * starttransaction data
						 ********/
						Map<String, Object> map3 = new HashMap<String, Object>();
						map3.put("UserName", userName);
						map3.put("EmailId", email);
						map3.put("MobNo", mobNo);
						map3.put("TxnNo", grn);
						map3.put("TxnAmount", newobj.getFeesAndCharges().getPayableNow());
						map3.put("NameofOwner",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getLandOwner());
						map3.put("LicenceFeeNla", newobj.getFeesAndCharges().getLicenseFee());
						map3.put("ScrutinyFeeNla", newobj.getFeesAndCharges().getScrutinyFee());
						map3.put("UserId", "2");
						map3.put("UserLoginId", "39");
						map3.put("TpUserId", userId);
						map3.put("PaymentMode", paymentType);
						map3.put("PayAgreegator", bankcode);
						map3.put("LcApplicantName", userName);
						map3.put("LcPurpose", newobj.getApplicantPurpose().getPurpose());
						map3.put("LcDevelopmentPlan",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDevelopmentPlan());
						map3.put("LcDistrict",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDistrict());
						saveTransaction = thirPartyAPiCall.saveTransactionData(map3, authtoken).getBody().get("Value")
								.toString();
						tradeLicense.setTcpSaveTransactionNumber(saveTransaction);

						/****************
						 * End Here
						 ***********/

						tradeLicense.setAction("PAID");
						tradeLicense.setWorkflowCode("NewTL");
						// tradeLicense.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));
						tradeLicense.setAssignee(Arrays
								.asList(servicePlanService.assignee("CTP_HR", tradeLicense.getTenantId(), true, info)));

						TradeLicenseRequest tradeLicenseRequests = new TradeLicenseRequest();

						tradeLicenseRequests.addLicensesItem(tradeLicense);
						tradeLicenseRequests.setRequestInfo(info);
						
//						tradeLicenseService.update(tradeLicenseRequests, "TL");

						// -----------------payment----------------------//
						// ----------payment update--------//

						Map<String, Object> rP = new HashMap<>();
						rP.put("RequestInfo", info);
						StringBuilder url1 = new StringBuilder(pgHost);
						url1.append(updatePath);
						url1.append("?txnId=" + transactionId);
						url1.append("&txnStatus=" + status);
						Object paymentUpdate = serviceRequestRepository.fetchResult(url1, rP);
						log.info("paymentUpdate\t" + paymentUpdate);
						// -------------------payment update end-----------//
						paymentUrl = paymentHost + paymentSuccess + tradeLicense.getBusinessService() + "/"
								+ applicationNumber + "/" + tradeLicense.getTenantId();
						returnPaymentUrl = paymentUrl + "?" + params1;
						log.info("returnPaymentUrl" + returnPaymentUrl);
						httpHeaders.setLocation(
								UriComponentsBuilder.fromHttpUrl(returnPaymentUrl.toString()).build().encode().toUri());

						return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);

						// ------------------finish--------------------//

					}
				}
			}

		} else if (!status.isEmpty() && status.equalsIgnoreCase("Error")) {

		}

		MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString(returnURL).build().getQueryParams();

		HttpHeaders httpHeaders1 = new HttpHeaders();

		StringBuilder redirectURL = new StringBuilder();
		redirectURL.append(returnURL);

		httpHeaders1.setLocation(UriComponentsBuilder.fromHttpUrl(redirectURL.toString()).queryParams(requestParam)
				.build().encode().toUri());
		return new ResponseEntity<>(httpHeaders1, HttpStatus.FOUND);

	}

}
