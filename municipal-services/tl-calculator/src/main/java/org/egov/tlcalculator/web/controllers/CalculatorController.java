package org.egov.tlcalculator.web.controllers;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.tlcalculator.service.BPACalculationService;
import org.egov.tlcalculator.service.CalculationPurpose;
import org.egov.tlcalculator.service.CalculationService;
import org.egov.tlcalculator.service.DemandService;
import org.egov.tlcalculator.service.FeesCalculation;
import org.egov.tlcalculator.service.PaymentCalculationResponse;
import org.egov.tlcalculator.utils.ResponseInfoFactory;
import org.egov.tlcalculator.web.models.*;
import org.egov.tlcalculator.web.models.bankguarantee.BankGuaranteeCalculationCriteria;
import org.egov.tlcalculator.web.models.bankguarantee.BankGuaranteeCalculationResponse;
import org.egov.tlcalculator.web.models.demand.BillResponse;
import org.egov.tlcalculator.web.models.demand.GenerateBillCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.egov.tlcalculator.utils.TLCalculatorConstants.businessService_BPA;
import static org.egov.tlcalculator.utils.TLCalculatorConstants.businessService_TL;


@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-27T14:56:03.454+05:30")

@Controller
@RequestMapping("/v1")
public class CalculatorController {
	@Autowired
	FeesCalculation paymentCalculation;

	private ObjectMapper objectMapper;

	private HttpServletRequest request;

	private CalculationService calculationService;

	private DemandService demandService;

	private BPACalculationService bpaCalculationService;
	
	private ResponseInfoFactory responseInfoFactory;
	
	@Autowired
	CalculationPurpose calculationPurpose;


	@Autowired
	public CalculatorController(ObjectMapper objectMapper, HttpServletRequest request,
			CalculationService calculationService, DemandService demandService,
			BPACalculationService bpaCalculationService, ResponseInfoFactory responseInfoFactory) {
		this.objectMapper = objectMapper;
		this.request = request;
		this.calculationService = calculationService;
		this.demandService = demandService;
		this.bpaCalculationService = bpaCalculationService;
		this.responseInfoFactory = responseInfoFactory;
	}

	/**
	 * Calulates the tradeLicense fee and creates Demand
	 * @param calculationReq The calculation Request
	 * @return Calculation Response
	 */
	@RequestMapping(value = {"/{servicename}/_calculates","/_calculates"}, method = RequestMethod.POST)
	public ResponseEntity<CalculationRes> calculate(@Valid @RequestBody CalculationReq calculationReq,@PathVariable(required = false) String servicename) {

		if(servicename==null)
			servicename = businessService_TL;
		List<Calculation> calculations = null;
		switch(servicename)
		{
			case businessService_TL:
				calculations = calculationService.calculate(calculationReq, false);
				break;

			case businessService_BPA:
				calculations = bpaCalculationService.calculate(calculationReq);
				break;
			default:
				throw new CustomException("UNKNOWN_BUSINESSSERVICE", " Business Service not supported");
		}

		CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();
		return new ResponseEntity<CalculationRes>(calculationRes, HttpStatus.OK);
	}

	@RequestMapping(value = "/access/_calculator", method = RequestMethod.POST)
	public ResponseEntity<CalculationRes> accessCalculator(@Valid @RequestBody CalculationReq calculationReq
			,@RequestParam(value="apiServiceName",required = false) String apiServiceName
			,@RequestParam(value="calculationServiceName",required = false) String calculationServiceName
			,@RequestParam(value="calculationType",required = false) String calculationType
			,@RequestParam(value="isIntialCalculation",required = false) int isIntialCalculation
			) {
		if(apiServiceName==null)
			apiServiceName = businessService_TL;
		List<Calculation> calculations = null;
		switch(apiServiceName)
		{
			case businessService_TL:
				calculations = calculationService.accessCalculation(calculationReq,apiServiceName,calculationServiceName,calculationType,isIntialCalculation);
				break;

//			case businessService_BPA:
//				calculations = bpaCalculationService.accessCalculation(calculationReq,apiServiceName,calculationServiceName,calculationType,isIntialCalculation);
//			break;
		default:
				throw new CustomException("UNKNOWN_BUSINESSSERVICE", " Business Service not supported");
		}

		CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();
		return new ResponseEntity<CalculationRes>(calculationRes, HttpStatus.OK);
	}

	/**
	 * Generates Bill for the given criteria
	 *
	 * @param requestInfoWrapper   Wrapper containg the requestInfo
	 * @param generateBillCriteria The criteria to generate bill
	 * @return The response of generate bill
	 */
	@RequestMapping(value = {"/{servicename}/_getbill","/_getbill"}, method = RequestMethod.POST)
	public ResponseEntity<BillAndCalculations> getBill(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
													   @ModelAttribute @Valid GenerateBillCriteria generateBillCriteria,
													   @PathVariable(required = false) String servicename) {
		if(servicename==null)
			servicename = businessService_TL;
		BillAndCalculations response = demandService.getBill(requestInfoWrapper.getRequestInfo(), generateBillCriteria, servicename);
		return new ResponseEntity<BillAndCalculations>(response, HttpStatus.OK);
	}


	/**
	 * Calulates the tradeLicense fee and returns estimate
	 * @param calculationReq The calculation Request
	 * @return Calculation Response
	 */
	@RequestMapping(value = {"/{servicename}/_estimate","/_estimate"}, method = RequestMethod.POST)
	public ResponseEntity<CalculationRes> estimate(@Valid @RequestBody CalculationReq calculationReq,@PathVariable(required = false) String servicename) {

		if(servicename==null)
			servicename = businessService_TL;
		List<Calculation> calculations = null;
		switch(servicename)
		{
			case businessService_TL:
				calculations = calculationService.calculate(calculationReq, true);
				break;

			default:
				throw new CustomException("UNKNOWN_BUSINESSSERVICE", " Business Service not supported");
		}

		CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();
		return new ResponseEntity<CalculationRes>(calculationRes, HttpStatus.OK);
	}
	@RequestMapping(value = {"/{servicename}/_calculator","/_calculator"}, method = RequestMethod.POST)
	public ResponseEntity<CalculationRes> calculator(@Valid @RequestBody CalculationReq calculationReq,@PathVariable(required = false) String servicename) {

		if(servicename==null)
			servicename = businessService_TL;
		List<Calculation> calculations = null;
		switch(servicename)
		{
			case businessService_TL:
				calculations = calculationService.calculate(calculationReq,false);
				break;

			case businessService_BPA:
				calculations = bpaCalculationService.calculate(calculationReq);
			break;
		default:
			//	throw new CustomException("UNKNOWN_BUSINESSSERVICE", " Business Service not supported");
		}

		CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();
		return new ResponseEntity<CalculationRes>(calculationRes, HttpStatus.OK);
	}
	
	
	@PostMapping("/guarantee/_estimate")
	public ResponseEntity<BankGuaranteeCalculationResponse> getEstimateForBankGuarantee(
			@RequestBody BankGuaranteeCalculationCriteria bankGuaranteeCalculationCriteria) {
		
		BankGuaranteeCalculationResponse bankGuaranteeCalculationResponse = calculationService
				.getEstimateForBankGuarantee(bankGuaranteeCalculationCriteria);
		bankGuaranteeCalculationResponse.setResponseInfo(responseInfoFactory
				.createResponseInfoFromRequestInfo(bankGuaranteeCalculationCriteria.getRequestInfo(), true));
		return new ResponseEntity<>(bankGuaranteeCalculationResponse, HttpStatus.OK);	
	}
	
	
	@PostMapping("/_getPaymentEstimate")
	public ResponseEntity<PaymentCalculationResponse> testing(@RequestBody @Valid RequestInfoWrapper requestInfoWrapper){
		
//		FeesCalculation paymentCalculation = new FeesCalculation();
		PaymentCalculationResponse response = new PaymentCalculationResponse();
		response.setFeesTypeCalculationDto(paymentCalculation.payment(requestInfoWrapper.getRequestInfo(), requestInfoWrapper.getApplicationNo()));
		
		return new ResponseEntity<>(response, HttpStatus.OK);	
	}

	@PostMapping("/_getPaymentPurpose")
	public ResponseEntity<PaymentCalculationResponse> testing1(@RequestBody @Valid RequestInfoWrapper requestInfoWrapper,@RequestParam String applicationNo){
		
//		FeesCalculation paymentCalculation = new FeesCalculation();
		PaymentCalculationResponse response = new PaymentCalculationResponse();
		response.setFeesTypeCalculationDto(calculationPurpose.paymentPurpose(requestInfoWrapper.getRequestInfo(), applicationNo));
		
		return new ResponseEntity<>(response, HttpStatus.OK);	
	}
}
