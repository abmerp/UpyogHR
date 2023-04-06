package org.egov.tl.service;

import static org.egov.tl.util.TLConstants.businessService_BPA;
import static org.egov.tl.util.TLConstants.businessService_TL;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tl.abm.repo.ChangeBeneficialRepo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.IdGenRepository;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.service.repo.LicenseServiceRepo;
import org.egov.tl.util.LandUtil;
import org.egov.tl.validator.LandMDMSValidator;
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.ChangeBeneficialResponse;
import org.egov.tl.web.models.Document;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.ResponseTransaction;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.UserResponse;
import org.egov.tl.web.models.UserSearchCriteria;
import org.egov.tl.web.models.Idgen.IdResponse;
import org.egov.tl.web.models.calculation.CalulationCriteria;
import org.egov.tl.workflow.ChangeBeneficialWorkflowIntegrator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

@Slf4j
@Service
public class ChangeBeneficialService {
	
	private static final String CHANGE_BENEFICIAL_WORKFLOWCODE = "CBIWF";
	private static final String WFTENANTID = "hr";

	
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
	
	@Autowired
	private ChangeBeneficialWorkflowIntegrator workflowIntegrator;
	
	@Autowired
	private IdGenRepository idGenRepository;
	
	@Value("${egov.tl.calculator.host}")
	private String guranteeHost;
	
	@Value("${egov.tl.calculator.access.calculate.endpoint}")
	private String accessCalculatorEndPoint;
	
	
	@Value("${egov.billingservice.host}")
    private String billingHost;
	
	@Value("${egov.bill.gen.endpoint}")
	private String fetchBillEndpoint;
	   
		
	String  licenseFee = "0.0";
	private final String JDAMR_DEVELOPER_STATUS="JDAMR";

	public ChangeBeneficialResponse createChangeBeneficial(ChangeBeneficialRequest beneficialRequest){
		String applicationNumber=beneficialRequest.getChangeBeneficial().get(0).getApplicationNumber();
		
		ChangeBeneficial applicationNumberChangeBeneficial=changeBeneficialRepo.getUdatedBeneficialForNest(applicationNumber);
		ChangeBeneficial changeBeneficialCheck=changeBeneficialRepo.getUdatedBeneficial(applicationNumber);
		applicationNumber=changeBeneficialCheck!=null&&applicationNumber.contains("HRCB")?(applicationNumberChangeBeneficial.getApplicationNumber()!=null?applicationNumberChangeBeneficial.getApplicationNumber():applicationNumber):applicationNumber;
		boolean applicationNumberCheck=changeBeneficialRepo.checkIsValidApplicationNumber(applicationNumber);
		ChangeBeneficialResponse changeBeneficialResponse = null;
		if(applicationNumberCheck) {
			List<TradeLicense> tradeLicense = changeBeneficialRepo.getLicenseByApplicationNo(applicationNumber,beneficialRequest.getRequestInfo().getUserInfo().getId());
			if(changeBeneficialCheck!=null) {
				    
					if(changeBeneficialCheck.getApplicationStatus()==1) {
						List<ChangeBeneficial> changeBeneficial = (List<ChangeBeneficial>) beneficialRequest.getChangeBeneficial()
								.stream().map(changebeneficial -> {
									changebeneficial.setCbApplicationNumber(changeBeneficialCheck.getCbApplicationNumber());
									changebeneficial.setWorkFlowCode(CHANGE_BENEFICIAL_WORKFLOWCODE);
									if(changebeneficial.getIsDraft()==null) {
										changebeneficial.setIsDraft("0");	
									}else {
										changebeneficial.setIsDraft("1");
									}
									
									if (!changebeneficial.getDeveloperServiceCode().equals(JDAMR_DEVELOPER_STATUS)) {
										changebeneficial.setAreaInAcres(changebeneficial.getAreaInAcres() == null ? ("0.0")
												: (changebeneficial.getAreaInAcres()));
										changebeneficial.setFullPaymentDone(false);
										changebeneficial.setApplicationStatus(1);
									}else {
										changebeneficial.setFullPaymentDone(true);
										changebeneficial.setApplicationStatus(3);
									}
									return changebeneficial;
								}).collect(Collectors.toList());
						
						beneficialRequest.setChangeBeneficial(changeBeneficial);
						changeBeneficialRepo.update(beneficialRequest);
						if(!changeBeneficial.get(0).getDeveloperServiceCode().equals(JDAMR_DEVELOPER_STATUS)) {
							changeBeneficialBillDemandCreation(beneficialRequest.getRequestInfo(),applicationNumber,beneficialRequest.getChangeBeneficial().get(0).getDeveloperServiceCode(),1,1);
						}
						changeBeneficialResponse = ChangeBeneficialResponse.builder()
								.changeBeneficial(beneficialRequest.getChangeBeneficial()).requestInfo(beneficialRequest.getRequestInfo()).message("Records has been updated Successfully.").status(true).build();
					}else if(changeBeneficialCheck.getApplicationStatus()==2) {
					    changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(null)
							.requestInfo(null).message("This Application Number already taken and 2nd part payment is in pending").status(false).build();
					}else {
					   	changeBeneficialResponse=createNewChangeBeneficial(beneficialRequest, tradeLicense, changeBeneficialResponse, applicationNumber);
					}
			    }else if(tradeLicense==null||tradeLicense.size()==0) {
			    	changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(null)
							.requestInfo(null).message("This Application Number has expaired or Application Number is not existing").status(false).build();
			    }else if(tradeLicense.get(0).getTradeLicenseDetail().getLicenseFeeCharges()==null) {
			    	changeBeneficialResponse = ChangeBeneficialResponse.builder()
							.changeBeneficial(null).requestInfo(null).message("licence fees is null.").status(false).build();
			    } else {
			    	changeBeneficialResponse=createNewChangeBeneficial(beneficialRequest, tradeLicense, changeBeneficialResponse, applicationNumber);
			    }
		
	      }else {
	    	  	changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(null)
						.requestInfo(null).message("Application Number is not existing").status(false).build();
		  }
		return changeBeneficialResponse;

	}
	
	private ChangeBeneficialResponse createNewChangeBeneficial(ChangeBeneficialRequest beneficialRequest,List<TradeLicense> tradeLicense,ChangeBeneficialResponse changeBeneficialResponse,String applicationNumber) {
	
		  RequestInfo requestInfo = beneficialRequest.getRequestInfo();
			List<ChangeBeneficial> changeBeneficial = (List<ChangeBeneficial>) beneficialRequest.getChangeBeneficial()
					.stream().map(changebeneficial -> {
						changebeneficial.setDeveloperId(requestInfo.getUserInfo().getId());
						changebeneficial.setCbApplicationNumber(getGenIds(WFTENANTID, requestInfo, businessService_TL, 1));
						changebeneficial.setWorkFlowCode(CHANGE_BENEFICIAL_WORKFLOWCODE);
						changebeneficial.setTotalChangeBeneficialCharge(tradeLicense.get(0).getTradeLicenseDetail().getLicenseFeeCharges().toString());
						if(changebeneficial.getIsDraft()==null) {
							changebeneficial.setIsDraft("0");	
						}else {
							changebeneficial.setIsDraft("1");
						}
						
						if (!changebeneficial.getDeveloperServiceCode().equals("JDAMR")) {
							changebeneficial.setAreaInAcres(changebeneficial.getAreaInAcres() == null ? ("0.0")
									: (changebeneficial.getAreaInAcres()));
							changebeneficial.setFullPaymentDone(false);
							changebeneficial.setApplicationStatus(1);
						}else {
							changebeneficial.setFullPaymentDone(true);
							changebeneficial.setApplicationStatus(3);
						}
						return changebeneficial;
					}).collect(Collectors.toList());
			beneficialRequest.setChangeBeneficial(changeBeneficial);
			changeBeneficialRepo.save(beneficialRequest);
			if(!changeBeneficial.get(0).getDeveloperServiceCode().equals(JDAMR_DEVELOPER_STATUS)) {
			   changeBeneficialBillDemandCreation(requestInfo,applicationNumber,changeBeneficial.get(0).getDeveloperServiceCode(),1,1);
			}
			changeBeneficialResponse = ChangeBeneficialResponse.builder()
					.changeBeneficial(beneficialRequest.getChangeBeneficial()).requestInfo(requestInfo).message("Records has been inserted Successfully.").status(true).build();
		return changeBeneficialResponse;
	}
	
	public ChangeBeneficialResponse getChangeBeneficial(RequestInfo requestInfo,String applicationNumber){
		ChangeBeneficialResponse changeBeneficialResponse = null;
		ChangeBeneficial changeBeneficiaDetails = null;
		
		try {
			changeBeneficiaDetails=changeBeneficialRepo.getBeneficialDetailsByApplicationNumber(applicationNumber);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(changeBeneficiaDetails!=null) {
		    changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(Arrays.asList(changeBeneficiaDetails))
				.requestInfo(requestInfo).message("Fetched success").status(true).build();
		}else {
		    changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(null)
					.requestInfo(null).message("Record not found").status(false).build();
		}
		
		return changeBeneficialResponse;
	}
	
	public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

	/**
	 * Sets the ApplicationNumber for given TradeLicenseRequest
	 *
	 * @param request TradeLicenseRequest which is to be created
	 */
	private String getGenIds(String tenantId,RequestInfo requestInfo,String businessService,int requestSize) {
		if (businessService == null)
			businessService = businessService_TL;
		List<String> applicationNumbers = null;
		switch (businessService) {
		case businessService_TL:
			applicationNumbers = getIdList(requestInfo, tenantId, config.getApplicationNumberIdgenNameTL(),
					config.getApplicationNumberIdgenFormatTL(), requestSize);
			break;

		case businessService_BPA:
			applicationNumbers = getIdList(requestInfo, tenantId, config.getApplicationNumberIdgenNameBPA(),
					config.getApplicationNumberIdgenFormatBPA(), requestSize);
			break;
		}
		ListIterator<String> itr = applicationNumbers.listIterator();

		Map<String, String> errorMap = new HashMap<>();
		if (applicationNumbers.size() != requestSize) {
			errorMap.put("IDGEN ERROR ",
					"The number of LicenseNumber returned by idgen is not equal to number of TradeLicenses");
		}

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
		return itr.next().replace("TL", "CB");
	}
	
	public String generateApplicationNumber(String userId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		LocalDateTime localDateTime = LocalDateTime.now();
		String date = formatter.format(localDateTime);
		
		Map<String, Object> authtoken = new HashMap<String, Object>();
		authtoken.put("UserId", "39");
		authtoken.put("TpUserId", userId);
		authtoken.put("EmailId", "mkthakur84@gmail.com");
		
		Map<String, Object> mapDNo = new HashMap<String, Object>();
		mapDNo.put("Village","");
		mapDNo.put("DiaryDate", date);
		mapDNo.put("ReceivedFrom", "1758");
		mapDNo.put("UserId", "1265");
		mapDNo.put("DistrictCode","");
		mapDNo.put("UserLoginId", "39");
		String dairyNumber = thirPartyAPiCall.generateDiaryNumber(mapDNo, authtoken).getBody().get("Value").toString();
		System.out.println("dairyNumber:---"+dairyNumber);
		
		Map<String, Object> mapANo = new HashMap<String, Object>();
		mapANo.put("DiaryNo", dairyNumber);
		mapANo.put("DiaryDate",date);
		mapANo.put("TotalArea", 0.0);
		mapANo.put("Village","Abc");
		mapANo.put("PurposeId", "74");
		mapANo.put("NameofOwner","Abc");
		mapANo.put("DateOfHearing", date);
		mapANo.put("DateForFilingOfReply", date);
		mapANo.put("UserId", "2");
		mapANo.put("UserLoginId", "39");
		String tcpApplicationNmber = thirPartyAPiCall.generateApplicationNumber(mapANo, authtoken).getBody().get("Value").toString();
        System.out.println("tcpApplicationNmber:---"+tcpApplicationNmber);
		return tcpApplicationNmber;
	}
//	
//	
//	private String setWorkFlowAndgetCode() {
//		JSONObject workFlowRequest = new JSONObject();
//		
//		rest.postForObject(config.getWfHost().concat(config.getWfTransitionPath()), workFlowRequest, String.class);
//		
//		
//		return null;
//	}
	public void changeBeneficialBillDemandCreation(RequestInfo requestInfo,String applicationNumber,String calculationServiceName,int calculationType,int isIntialPayment) {
   
	  TradeLicense tradeLicenses=tradeLicensesService.getLicensesWithOwnerInfo(TradeLicenseSearchCriteria.builder().applicationNumber(applicationNumber).tenantId("hr").build(), requestInfo).get(0);
//			  try {
//				String data = mapper.writeValueAsString(tradeLicenses);
//			} catch (JsonProcessingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
	  // --------------------------calculation start--------------------------------//
	        StringBuilder calculatorUrl = new StringBuilder(guranteeHost);
			calculatorUrl.append(accessCalculatorEndPoint);
			calculatorUrl.append("?calculationServiceName="+calculationServiceName+"&calculationType="+calculationType+"&isIntialCalculation="+isIntialPayment);
		    System.out.println("url:---"+calculatorUrl);
			CalulationCriteria calulationCriteriaRequest = new CalulationCriteria();
			calulationCriteriaRequest.setTenantId(tradeLicenses.getTenantId());
			calulationCriteriaRequest.setTradelicense(tradeLicenses);
			java.util.List<CalulationCriteria> calulationCriteria = Arrays.asList(calulationCriteriaRequest);

//			CalculatorRequest calculator = new CalculatorRequest();
			Map<String, Object> calculator = new HashMap<>();
			calculator.put("applicationNumber", applicationNumber);
			
			Map<String, Object> calculatorMap = new HashMap<>();
			calculatorMap.put("CalulationCriteria", calulationCriteria);
			calculatorMap.put("CalculatorRequest", calculator);
			calculatorMap.put("RequestInfo", requestInfo);
			HashMap responseCalculator = null;
			try {
				responseCalculator = serviceRequestRepository.fetchResultJSON(calculatorUrl, calculatorMap);
			}catch (Exception e) {
				log.error("Exception :-- "+e.getMessage());
			}
	
	
   }
	
	public ChangeBeneficialResponse billAndDemandRefresh(RequestInfo requestInfo,String applicationNumber){
		ChangeBeneficialResponse changeBeneficialResponse = null;
		ChangeBeneficial changeBeneficiaDetails = null;
		
		try {
			
			ChangeBeneficial applicationNumberChangeBeneficial=changeBeneficialRepo.getUdatedBeneficialForNestRefrsh(applicationNumber);
			ChangeBeneficial changeBeneficialCheck=changeBeneficialRepo.getUdatedBeneficial(applicationNumber);
			String applicationNumbers=changeBeneficialCheck!=null&&applicationNumber.contains("HRCB")?(applicationNumberChangeBeneficial.getApplicationNumber()!=null?applicationNumberChangeBeneficial.getApplicationNumber():applicationNumber):applicationNumber;
			changeBeneficiaDetails=changeBeneficialRepo.getBeneficialByApplicationNumber(applicationNumber);
			if(changeBeneficiaDetails!=null) {
				
						try {
							 changeBeneficialBillDemandCreation(requestInfo,applicationNumbers,changeBeneficiaDetails.getDeveloperServiceCode(),0,0);
						} catch (Exception e) {
							log.error("Exception :--"+e.getMessage());
						}
				}else {
				changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(null)
						.requestInfo(null).message("You have not changed any beneficial status ").status(false).build();
						
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return changeBeneficialResponse;
	}
	
	public ChangeBeneficialResponse pay(RequestInfo requestInfo,String applicationNumber) {
		
		ChangeBeneficial applicationNumberChangeBeneficial=changeBeneficialRepo.getUdatedBeneficialForNest(applicationNumber);
		ChangeBeneficial changeBeneficialCheck=changeBeneficialRepo.getUdatedBeneficial(applicationNumber);
		String applicationNumberLicense=changeBeneficialCheck!=null&&applicationNumber.contains("HRCB")?(applicationNumberChangeBeneficial.getApplicationNumber()!=null?applicationNumberChangeBeneficial.getApplicationNumber():applicationNumber):applicationNumber;
			
		
		ChangeBeneficialResponse changeBeneficialResponse = null;
		ChangeBeneficial changeBeneficiaDetails = null;
		try {
			changeBeneficiaDetails=changeBeneficialRepo.getBeneficialDetailsBycbApplicationNumber(applicationNumber);
			if(changeBeneficiaDetails!=null) {
				try {
					 applicationNumber=changeBeneficiaDetails.getApplicationNumber();
					 StringBuilder faetchBillUrl = new StringBuilder(billingHost);
					 faetchBillUrl.append(fetchBillEndpoint);
					 faetchBillUrl.append("?tenantId=hr&businessService=TL&consumerCode="+applicationNumberLicense);
					 
					 Map<String, Object> faetchBillMap = new HashMap<>();
					 faetchBillMap.put("RequestInfo", requestInfo);
					 HashMap faetchBill = serviceRequestRepository.fetchResultJSON(faetchBillUrl, faetchBillMap);
					 List<HashMap> BillData=(List<HashMap>) faetchBill.get("Bill");
					 List<HashMap> billDetails=(List<HashMap>) BillData.get(0).get("billDetails");
					 String billId=billDetails.get(0).get("billId").toString();
					 List<HashMap> billAccountDetails=(List<HashMap>)billDetails.get(0).get("billAccountDetails");
					
					 Double am=0.0;
                     for (HashMap hashMap : billAccountDetails) {
                         am=am+Double.parseDouble(hashMap.get("amount").toString()); 
					 } 
				
					 BigDecimal estimateAmount= new BigDecimal(am);
					 String callBack="http://localhost:8075/tl-services/beneficial/transaction/v1/_redirect";
					 try {
					 HashMap<String, Object> trans= createTranaction(requestInfo,requestInfo.getUserInfo().getId().toString(),WFTENANTID,estimateAmount,applicationNumberLicense,billId,callBack,changeBeneficiaDetails);
					 changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(Arrays.asList(trans))
								.requestInfo(requestInfo).message("Transaction has been created successfully ").status(true).build();
					 }catch (Exception e) {
						 log.error("Exception :--"+e.getMessage());
						 changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(null)
									.requestInfo(null).message("You have already created Transaction").status(false).build();
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Exception :--"+e.getMessage());
					 changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(null)
								.requestInfo(null).message("Something went wrong").status(false).build();
			
				}								
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
	
	
	public HashMap<String, Object> createTranaction(RequestInfo requestInfo,String userId,String tenantId,BigDecimal amountFr,String applicationNumberLicense,String billId,String callbackUrl,ChangeBeneficial changeBeneficiaDetails) {
		
		
		String am=amountFr.toString();
		Double amount=0.0;
		
        try{
			amount=Double.parseDouble(am);
		}catch (NumberFormatException e) {
			e.printStackTrace();
		}
				
		UserResponse UserResponse=getUserInfo(userId);
		org.egov.tl.web.models.User user=UserResponse.getUser().get(0);
		StringBuilder url = new StringBuilder(userHost);
		url.append(transactionCreatePath);
		Map<String, Object> taxAndPayments = new HashMap<>();
		taxAndPayments.put("billId",billId);
		taxAndPayments.put("amountPaid",new BigDecimal(amount));
		Map<String, Object> additionalDetails = new HashMap<>();
		additionalDetails.put("isWhatsapp",false);
		
		Map<String, Object> transaction = new HashMap<>();
		transaction.put("tenantId", tenantId);
		transaction.put("txnAmount", new BigDecimal(amount));
		transaction.put("bank", "0300997");
		transaction.put("ptype", "101");
		transaction.put("remarks", "Pay");
		transaction.put("address", "haryana");
		transaction.put("pinCode", "160005");
		transaction.put("cityName", "haryana");
		transaction.put("module", "TL");
		transaction.put("billId", billId);
		transaction.put("consumerCode", applicationNumberLicense+","+changeBeneficiaDetails.getApplicationNumber());
		transaction.put("productInfo", "Change Beneficial Payment");
		transaction.put("gateway", "NIC");
		transaction.put("callbackUrl", callbackUrl);
		transaction.put("isInitial", changeBeneficiaDetails.getApplicationStatus()==1?0:1);
		
	
		Map<String, Object> userDEtails = new HashMap<>();
		userDEtails.put("userName", user.getMobileNumber());
		userDEtails.put("name", user.getName());
		userDEtails.put("mobileNumber", user.getMobileNumber());
		userDEtails.put("tenantId", tenantId);
		
		transaction.put("user", userDEtails);
		transaction.put("additionalDetails", additionalDetails);
		transaction.put("taxAndPayments", Arrays.asList(taxAndPayments));

		Map<String, Object> transactionReq = new HashMap<>();
		transactionReq.put("Transaction", transaction);
		transactionReq.put("RequestInfo", requestInfo);
		
		HashMap transactionRes=serviceRequestRepository.fetchResultJSON(url, transactionReq);
//		System.out.println(transactionRes);
		HashMap<String, Object> transactionResp=(HashMap<String, Object>) transactionRes.get("Transaction");
		log.info("Transaction Response : "+transactionResp);
//		if(transactionResp.get("txnStatus")!=null&&transactionResp.get("txnStatus").toString().toUpperCase().equals("SUCCESS")) {
//			simpleUrlBrowser.browse(transactionResp.get("redirectUrl").toString());
//		}else {
//			simpleUrlBrowser.browse(transactionRes.get("callbackUrl").toString());
//		}
		return transactionResp;		
	}
	
	public ResponseEntity<Object> postTransactionDeatil(MultiValueMap<String, String> requestParam) {
			
		String dairyNumber="";
		String tcpApplicationNumber="";
		String caseNumber="";
		
		
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
		String applicationNumberLicense = transaction.getTransaction().get(0).getConsumerCode().split(",")[0];
		String applicationNumber = transaction.getTransaction().get(0).getConsumerCode().split(",")[1];
		String uuid = transaction.getTransaction().get(0).getUser().getUuid();
		String tennatId = transaction.getTransaction().get(0).getUser().getTenantId();
		String userName = transaction.getTransaction().get(0).getUser().getUserName();

		String paymentUrl;
		String returnPaymentUrl;
		MultiValueMap<String, String> params1 = new LinkedMultiValueMap<>();

		params1.put("eg_pg_txnid", Collections.singletonList(txnId));

		// ------------failure----------------//
		if (!status.isEmpty() && status.equalsIgnoreCase("Success")) {

			paymentUrl = paymentHost + paymentSuccess + "TL" + "/" + applicationNumberLicense + "/" + "hr";
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
			tradeLicenseRequest.setApplicationNumber(applicationNumberLicense);
			
			List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(tradeLicenseRequest, info);
//			ChangeBeneficial changeBeneficial=null;
//		    try {
//		    	changeBeneficial=changeBeneficialRepo.getBeneficialByApplicationNumber(applicationNumber);
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
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
						tcpApplicationNumber = thirPartyAPiCall.generateApplicationNumber(mapANo, authtoken).getBody()
								.get("Value").toString();
						tradeLicense.setTcpApplicationNumber(tcpApplicationNumber);

						/****************
						 * End Here
						 ***********/
						/****************
						 * starttransaction data
						 ********/
//						Map<String, Object> map3 = new HashMap<String, Object>();
//						map3.put("UserName", userName);
//						map3.put("EmailId", email);
//						map3.put("MobNo", mobNo);
//						map3.put("TxnNo", grn);
//						map3.put("TxnAmount", newobj.getFeesAndCharges().getPayableNow());
//						map3.put("NameofOwner",
//								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getLandOwner());
//						map3.put("LicenceFeeNla", newobj.getFeesAndCharges().getLicenseFee());
//						map3.put("ScrutinyFeeNla", newobj.getFeesAndCharges().getScrutinyFee());
//						map3.put("UserId", "2");
//						map3.put("UserLoginId", "39");
//						map3.put("TpUserId", userId);
//						map3.put("PaymentMode", paymentType);
//						map3.put("PayAgreegator", bankcode);
//						map3.put("LcApplicantName", userName);
//						map3.put("LcPurpose", newobj.getApplicantPurpose().getPurpose());
//						map3.put("LcDevelopmentPlan",
//								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDevelopmentPlan());
//						map3.put("LcDistrict",
//								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDistrict());
//						saveTransaction = thirPartyAPiCall.saveTransactionData(map3, authtoken).getBody().get("Value")
//								.toString();
//						tradeLicense.setTcpSaveTransactionNumber(saveTransaction);
//
//						/****************
//						 * End Here
//						 ***********/
//
//						tradeLicense.setAction("PAID");
//						tradeLicense.setWorkflowCode("NewTL");
//						// tradeLicense.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));
//						tradeLicense.setAssignee(Arrays
//								.asList(servicePlanService.assignee("CTP_HR", tradeLicense.getTenantId(), true, info)));
//
//						TradeLicenseRequest tradeLicenseRequests = new TradeLicenseRequest();
//
//						tradeLicenseRequests.addLicensesItem(tradeLicense);
//						tradeLicenseRequests.setRequestInfo(info);
//						
//						tradeLicenseService.update(tradeLicenseRequests, "TL");
						
						
						
						ChangeBeneficial changeBeneficiaDetails=null;
						try {
							changeBeneficiaDetails=changeBeneficialRepo.getBeneficialByApplicationNumber(applicationNumber);
							ChangeBeneficialRequest changeBeneficialRequest=new ChangeBeneficialRequest();
							ChangeBeneficial changeBeneficialPayment=null;
							if(changeBeneficiaDetails.getApplicationStatus()==1) {
								changeBeneficialPayment=ChangeBeneficial.builder()
										.paidAmount(String.valueOf(requestParam.get("amount")))
										.isDraft("0")
										.applicationStatus(2)
										.isFullPaymentDone(false)
										.build();
							}else if(changeBeneficiaDetails.getApplicationStatus()==2) {
								changeBeneficialPayment=ChangeBeneficial.builder()
										.paidAmount(String.valueOf(requestParam.get("amount")))
										.isDraft("0")
										.applicationStatus(3)
										.isFullPaymentDone(true)
										.build();
							}
							
							changeBeneficialRequest.setChangeBeneficial(Arrays.asList(changeBeneficialPayment));
							changeBeneficialRepo.updatePaymentDetails(changeBeneficialRequest);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						/************************* Workflow start *****************************/
					Map<String ,Object> workFlowRequests=new HashMap<>();
					workFlowRequests.put("cbApplicationNumber",changeBeneficiaDetails.getCbApplicationNumber());
					workFlowRequests.put("workflowCode",CHANGE_BENEFICIAL_WORKFLOWCODE);
					workFlowRequests.put("workFlowRequestType","PERMENENT");
					workFlowRequests.put("action","INITIATE");
					workFlowRequests.put("comment","start process");
					workFlowRequests.put("wfTenantId",WFTENANTID);
					
					String businessServiceFromMDMS="TL";
					String assignees=servicePlanService.assignee("CTP_HR", WFTENANTID, true, info);
					List<Document> wfDocuments=new ArrayList<>();
					workflowIntegrator.callWorkFlow(Arrays.asList(workFlowRequests), businessServiceFromMDMS, info, wfDocuments, Arrays.asList(assignees));
					/************************* Workflow end *****************************/

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
								+ applicationNumberLicense + "/" + tradeLicense.getTenantId();
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
