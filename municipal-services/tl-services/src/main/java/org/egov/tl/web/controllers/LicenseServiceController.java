package org.egov.tl.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;

import org.egov.tl.config.TLConfiguration;
import org.egov.tl.service.GenerateTcpNumbers;
import org.egov.tl.service.LicenseService;
import org.egov.tl.service.ThirPartyAPiCall;
import org.egov.tl.service.dao.LicenseServiceDao;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.LicenseServiceRequest;

import org.egov.tl.web.models.LicenseServiceResponse;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseResponse;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.Transaction;
import org.egov.tl.web.models.TransactionResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

@RestController
@RequestMapping("new")
public class LicenseServiceController {

	@Autowired
	GenerateTcpNumbers generateTcpNumbers;

	@Autowired
	LicenseService newServiceInfoService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	private RestTemplate rest;
	@Autowired
	private TLConfiguration config;

	private static final ObjectMapper SORTED_MAPPER = new ObjectMapper();

	static {
		SORTED_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
	}

	@PostMapping(value = "_create")
	public ResponseEntity<LicenseServiceResponse> createNewService(@RequestBody LicenseServiceRequest newService)
			throws JsonProcessingException {

		LicenseServiceResponseInfo newServiceInfo = newServiceInfoService.createNewServic(newService);

		List<LicenseServiceResponseInfo> newServiceInfoList = new ArrayList<>();
		newServiceInfoList.add(newServiceInfo);
		LicenseServiceResponse newServiceResponseInfo = LicenseServiceResponse.builder()
				.newServiceInfo(newServiceInfoList)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(newService.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(newServiceResponseInfo, HttpStatus.OK);
	}

	@GetMapping("/licenses/_get")
	public ResponseEntity<LicenseServiceResponseInfo> getNewServicesDetailById(
			@RequestParam("applicationNumber") String applicationNumber,
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return new ResponseEntity<>(
				newServiceInfoService.getNewServicesInfoById(applicationNumber, requestInfoWrapper.getRequestInfo()),
				HttpStatus.OK);
	}

	@GetMapping("/licenses/_getall")
	public List<LicenseServiceDao> getNewServicesDetailAll() {

		return newServiceInfoService.getNewServicesInfoAll();
	}

	@GetMapping("/licenses/_applicants")
	public List<String> getApplicantsNumber() {
		return newServiceInfoService.getApplicantsNumber();
	}

	/* FOR FLATE JSON FOR JSON TO PDF */
	@GetMapping("/licenses/object/_get")
	public Map<String, String> getJsonSingleFormate(@RequestParam("applicationNumber") String applicationNumber,
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) throws JsonProcessingException {

		LicenseServiceResponseInfo licenseServiceResponseInfo = newServiceInfoService
				.getNewServicesInfoById(applicationNumber, requestInfoWrapper.getRequestInfo());
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(licenseServiceResponseInfo);

		JsonNode node = mapper.readValue(json, JsonNode.class);

		((ObjectNode) node.get("newServiceInfoData").get(0).get("DetailsofAppliedLand")).remove("dgpsDetails");

		Map<String, ValueNode> map1 = new LinkedHashMap<>();
		// flattenJson(node, null, map1);
		flattenJson(node.get("newServiceInfoData").get(0), null, map1);

		HashMap<String, String> map2 = new HashMap<>();

		for (Entry<String, ValueNode> entry : map1.entrySet()) {

			System.out.println("key======>" + entry.getKey() + " =========" + entry.getValue());
			if (entry.getKey().contains(".")) {
				map2.put(entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1), convertNode(entry.getValue()));

			}
		}
		return map2;
	}

	@PostMapping("/licenses/object/_getByApplicationNumber")
	public ResponseEntity<LicenseServiceResponseInfo> getJsonSingleFormate(
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationNumber") String applicationNumber) {

		return new ResponseEntity<>(
				newServiceInfoService.getNewServicesInfoById(applicationNumber, requestInfoWrapper.getRequestInfo()),
				HttpStatus.OK);
	}

	public static void flattenJson(JsonNode node, String parent, Map<String, ValueNode> map) {
		if (node instanceof ValueNode) {
			map.put(parent, (ValueNode) node);
		} else {
			String prefix = parent == null ? "" : parent + ".";
			if (node instanceof ArrayNode) {
				ArrayNode arrayNode = (ArrayNode) node;
				for (int i = 0; i < arrayNode.size(); i++) {
					flattenJson(arrayNode.get(i), prefix + i, map);
				}
			} else if (node instanceof ObjectNode) {
				ObjectNode objectNode = (ObjectNode) node;
				for (Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields(); it.hasNext();) {
					Map.Entry<String, JsonNode> field = it.next();
					flattenJson(field.getValue(), prefix + field.getKey(), map);
				}
			} else {
				throw new RuntimeException("unknown json node");
			}
		}
	}

	private String convertNode(final JsonNode node) throws JsonProcessingException {
		final Object obj = SORTED_MAPPER.treeToValue(node, Object.class);
		String json = SORTED_MAPPER.writeValueAsString(obj);
		System.out.println("fnal json string ======> " + json);
		return json;
	}

	@PostMapping(value = "/transaction/v1/_redirect", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<Object> method(@RequestBody MultiValueMap<String, String> formData) {

		return newServiceInfoService.postTransactionDeatil(formData);
	}
	/* FLAT JSON CODE END */

	@PostMapping(value = "/getDeptToken")
	public ResponseEntity<TradeLicenseResponse> departmentAuthToken(
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam("applicationNumber") String applicationNo) {
		Map<String, Object> map = new HashMap<String, Object>();

		List<TradeLicense> dispatchtoken = newServiceInfoService.generateLoiNumber(map, requestInfoWrapper,
				applicationNo);
		TradeLicenseResponse response = TradeLicenseResponse.builder().licenses(dispatchtoken).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping(value = "/_generateTcpNumbers")
	public Map<String, Object> generateTcpNumbers(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@ModelAttribute TradeLicenseSearchCriteria criteria) {

		Map<String, Object> generateNumbers = generateTcpNumbers.tcpNumbers(criteria,
				requestInfoWrapper.getRequestInfo());

//		ResponseGenerateNumbers response = ResponseGenerateNumbers.builder().servicePlanRequest(generate).responseInfo(
//				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
//				.build();
		return generateNumbers;
	}

}
