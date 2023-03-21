package org.egov.tl.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.repo.ChangeBeneficialRepo;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.web.models.CalculatorRequest;
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.ChangeBeneficialResponse;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.calculation.CalulationCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChangeBeneficialService {

	
	@Autowired
	private ChangeBeneficialRepo changeBeneficialRepo;
	
	@Autowired
	private LicenseService licenseService;
	
	@Autowired
	private TradeLicenseService tradeLicensesService;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Value("${egov.tl.calculator.host}")
	private String guranteeHost;
	@Value("${egov.tl.calculator.calculate.endpoint}")
	private String calculatorEndPoint;
	
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

	public ChangeBeneficialResponse createChangeBeneficialPay(RequestInfo requestInfo,String applicationNumber,int isIntialPayment)
			throws JsonProcessingException {
		ChangeBeneficialResponse changeBeneficialResponse = null;
		ChangeBeneficial changeBeneficiaDetails = null;
		
		try {
			changeBeneficiaDetails=changeBeneficialRepo.getBeneficialByApplicationNumber(applicationNumber);
			
			if(changeBeneficiaDetails!=null) {
			   Double payableAmount=getAdministrativeChargeForChangeBeneficial(requestInfo, applicationNumber, changeBeneficiaDetails, isIntialPayment);
			   if(!payableAmount.equals(0.0)){
				  TradeLicense tradeLicenses=tradeLicensesService.getLicensesWithOwnerInfo(TradeLicenseSearchCriteria.builder().applicationNumber(applicationNumber).tenantId("hr").build(), requestInfo).get(0);
				  String data = mapper.writeValueAsString(tradeLicenses);
				  System.out.println("data :----------------------"+data);
				  // --------------------------calculation--------------------------------//
						
				        StringBuilder calculatorUrl = new StringBuilder(guranteeHost);
						calculatorUrl.append(calculatorEndPoint);
					
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
						Object responseCalculator = serviceRequestRepository.fetchResult(calculatorUrl, calculatorMap);
							
						
					
				 
			   }else {
				   changeBeneficialResponse = ChangeBeneficialResponse.builder().changeBeneficial(null)
							.requestInfo(null).message("Your license fee invalid data coming or version mismatch of tradelicense and tradelicensedetails").status(false).build();
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
	
	private Double getAdministrativeChargeForChangeBeneficial(RequestInfo requestInfo,String applicationNumber,ChangeBeneficial changeBeneficialDetails,int type) {
	
		LicenseServiceResponseInfo licenseServiceResponseInfo=licenseService.getNewServicesInfoById(applicationNumber, requestInfo);
	    Double administrativeCharge=0.0;
	    Boolean isIntial=(type==1?true:false);   
	    try { //COD , JDAMR, CISP, TOL
	    	  String developerServiceCode=changeBeneficialDetails.getDeveloperServiceCode();
	 	      licenseFee = "3123133";//Optional.ofNullable(licenseServiceResponseInfo.getNewServiceInfoData().get(0).getFeesAndCharges().getLicenseFee()).orElseThrow(RuntimeException::new);
	  			switch (developerServiceCode) {
				
					case "COD":
						administrativeCharge=Double.parseDouble(licenseFee)*0.25*(isIntial?(0.4):(0.6));
						break;
					case "JDAMR":
							// adminestrive charge has levied	
					    break;
					case "CISP":
						administrativeCharge=Double.parseDouble(licenseFee)*0.25*(isIntial?(0.4):(0.6));
						break;
					case "TOL":
						administrativeCharge=Double.parseDouble(licenseFee)*0.25*(isIntial?(0.4):(0.6))+(isIntial?(Double.parseDouble(licenseFee)*0.1):(0.0));
			        	break;
					default:
						break;
				}
	  			
			   log.info("Administrative Charge for change beneficial : "+administrativeCharge);
			}catch (Exception e) {
				e.printStackTrace();
			  log.error("Exception : "+e.getMessage());
			}
	        
	        if(licenseFee.equals(0.0)) {
	    	    administrativeCharge=0.0;
				log.warn("Your License Fees is very low : "+licenseFee);
			}
	      
		return administrativeCharge;
		}

}
