package org.egov.lndcalculator.web.controllers;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.lndcalculator.service.BPABillingSlabService;
import org.egov.lndcalculator.service.BillingslabService;
import org.egov.lndcalculator.utils.ResponseInfoFactory;
import org.egov.lndcalculator.validator.BillingslabValidator;
import org.egov.lndcalculator.web.models.BillingSlab;
import org.egov.lndcalculator.web.models.BillingSlabReq;
import org.egov.lndcalculator.web.models.BillingSlabRes;
import org.egov.lndcalculator.web.models.BillingSlabSearchCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.egov.lndcalculator.utils.TLCalculatorConstants.businessService_BPA;
import static org.egov.lndcalculator.utils.TLCalculatorConstants.businessService_TL;

import java.util.Collections;

@Controller
@RequestMapping("/billingslab")
public class BillingslabController {

	@Autowired
	private BillingslabValidator billingslabValidator;

	@Autowired
	private BillingslabService service;

	@Autowired
	private BPABillingSlabService bpaBillingSlabService;

	@Autowired
	private ResponseInfoFactory factory;
	/**
	 * Creates Billing Slabs for TradeLicense
	 * @param billingSlabReq
	 * @return
	 */
	@RequestMapping(value = "/_create", method = RequestMethod.POST)
	public ResponseEntity<BillingSlabRes> billingslabCreatePost(@Valid @RequestBody BillingSlabReq billingSlabReq) {
		billingslabValidator.validateCreate(billingSlabReq);
		BillingSlabRes response = service.createSlabs(billingSlabReq);
		return new ResponseEntity<BillingSlabRes>(response, HttpStatus.OK);
	}

	/**
	 * Updates Billing Slabs of TradeLicense
	 * @param billingSlabReq
	 * @return
	 */
	@RequestMapping(value = "/_update", method = RequestMethod.POST)
	public ResponseEntity<BillingSlabRes> billingslabUpdatePost(@Valid @RequestBody BillingSlabReq billingSlabReq) {
		billingslabValidator.validateUpdate(billingSlabReq);
		BillingSlabRes response = service.updateSlabs(billingSlabReq);
		return new ResponseEntity<BillingSlabRes>(response, HttpStatus.OK);
	}

	/**
	 * Searches Billing Slabs belonging TradeLicense based on criteria
	 * @param billingSlabSearchCriteria
	 * @param requestInfo
	 * @return
	 */
    @RequestMapping(value = {"/{servicename}/_search", "/_search"}, method = RequestMethod.POST)
    public ResponseEntity<BillingSlabRes> billingslabSearchPost(@ModelAttribute @Valid BillingSlabSearchCriteria billingSlabSearchCriteria,
                                                                @Valid @RequestBody RequestInfo requestInfo,@PathVariable(required = false) String servicename) {
		if(servicename==null)
			servicename = businessService_TL;

		BillingSlabRes response = null;
		switch(servicename)
		{
			case businessService_TL:
				response = service.searchSlabs(billingSlabSearchCriteria, requestInfo);
				break;

			case businessService_BPA:
				BillingSlab billingSlab = bpaBillingSlabService.search(billingSlabSearchCriteria, requestInfo);
				response = BillingSlabRes.builder().responseInfo(factory.createResponseInfoFromRequestInfo(requestInfo, true))
						.billingSlab(Collections.singletonList(billingSlab)).build();
				break;

			default:
				throw new CustomException("UNKNOWN_BUSINESSSERVICE", " Business Service not supported");
		}
        return new ResponseEntity<BillingSlabRes>(response, HttpStatus.OK);
    }

}
