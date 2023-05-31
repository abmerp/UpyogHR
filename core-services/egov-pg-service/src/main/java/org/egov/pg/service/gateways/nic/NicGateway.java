package org.egov.pg.service.gateways.nic;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.paytm.pg.merchant.CheckSumServiceHelper;
import com.paytm.pg.merchant.*;
import lombok.extern.slf4j.Slf4j;
import org.egov.pg.models.Transaction;
import org.egov.pg.service.Gateway;
import org.egov.pg.utils.Utils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.time.Month;

@Service
@Slf4j
public class NicGateway implements Gateway {

	private static final String GATEWAY_NAME = "EGRASS";
	private String DTO;
	private String STO;
	private String DDO;
	private String DeptCode;
	private String SchemeName;
	private String SchemeCount;
	private String UUrl_Debit;
	private String UUrl_Status;
	private String OfficeName;

	private final RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	private final boolean ACTIVE;

	@Autowired
	public NicGateway(RestTemplate restTemplate, Environment environment, ObjectMapper objectMapper) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;

		ACTIVE = Boolean.valueOf(environment.getRequiredProperty("nic.active"));
		DTO = environment.getRequiredProperty("nic.DTO");
		STO = environment.getRequiredProperty("nic.STO");
		DDO = environment.getRequiredProperty("nic.DDO");
		DeptCode = environment.getRequiredProperty("nic.DeptCode");
		OfficeName = environment.getRequiredProperty("nic.OfficeName");
		SchemeName = environment.getRequiredProperty("nic.SchemeName");
		SchemeCount = environment.getRequiredProperty("nic.SchemeCount");
		UUrl_Debit = environment.getRequiredProperty("nic.Uurl.debit");

	}

	@Override
	public URI generateRedirectURI(Transaction transaction) {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String validUpto = df.format(new Date());

		// todo challan year
		TreeMap<String, String> paramMap = new TreeMap<>();

		paramMap.put("DTO", DTO);
		paramMap.put("STO", STO);
		paramMap.put("DDO", DDO);
		paramMap.put("Deptcode", DeptCode);
		paramMap.put("Applicationnumber", transaction.getTxnId());
		paramMap.put("ORDER_ID", transaction.getTxnId());
		paramMap.put("Fullname", transaction.getUser().getName());
		paramMap.put("cityname", transaction.getCityName());
		paramMap.put("address", transaction.getAddress());
		paramMap.put("PINCODE", transaction.getPinCode());
		paramMap.put("officename", OfficeName);
		paramMap.put("TotalAmount", Utils.formatAmtAsRupee(transaction.getTxnAmount()));
		paramMap.put("ChallanYear", "2223");
		paramMap.put("UURL", transaction.getCallbackUrl());
		paramMap.put("ptype", transaction.getPtype().equalsIgnoreCase("103") ? "M" : "N");
		paramMap.put("bank", transaction.getBank());
		paramMap.put("remarks", transaction.getRemarks());
		paramMap.put("securityemail", transaction.getUser().getEmailId());
		paramMap.put("securityphone", transaction.getUser().getMobileNumber());
		paramMap.put("valid_upto", validUpto);
		paramMap.put("SCHEMENAME", SchemeName);
		paramMap.put("SCHEMECOUNT", SchemeCount);
		paramMap.put("FEEAMOUNT1", Utils.formatAmtAsRupee(transaction.getTxnAmount()));

		try {

			/*
			 * String checkSum =
			 * CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(DeptCode,
			 * paramMap); paramMap.put("CHECKSUMHASH", checkSum);
			 */

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			paramMap.forEach((key, value) -> params.put(key, Collections.singletonList(value)));

			UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(UUrl_Debit).queryParams(params).build()
					.encode();

			return uriComponents.toUri();
		} catch (Exception e) {
			// log.error("Paytm Checksum generation failed", e);
			throw new CustomException("CHECKSUM_GEN_FAILED",
					"Hash generation failed, gateway redirect URI cannot be generated");
		}
	}

	@Override
	public Transaction fetchStatus(Transaction currentStatus, Map<String, String> params) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		// treeMap.put("ApplicationNumber", ApplicationNumber);
		treeMap.put("ApplicationNumber", currentStatus.getTxnId());
		//treeMap.put("ORDER_ID", currentStatus.getTxnId());
		try {
			/*
			 * String checkSum =
			 * CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(DeptCode,
			 * treeMap); treeMap.put("CHECKSUMHASH", checkSum);
			 */

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());

			HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(treeMap, httpHeaders);

			ResponseEntity<NicResponse> response = restTemplate.postForEntity(UUrl_Status, httpEntity,
					NicResponse.class);

			return transformRawResponse(response.getBody(), currentStatus);

		} catch (RestClientException e) {
			// log.error("Unable to fetch status from Paytm gateway", e);
			throw new CustomException("UNABLE_TO_FETCH_STATUS", "Unable to fetch status from Paytm gateway");
		} /*
			 * catch (Exception e) { // log.error("Paytm Checksum generation failed", e);
			 * throw new CustomException("CHECKSUM_GEN_FAILED",
			 * "Hash generation failed, gateway redirect URI cannot be generated"); }
			 */
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return ACTIVE;
	}

	@Override
	public String gatewayName() {
		// TODO Auto-generated method stub
		return GATEWAY_NAME;
	}

	@Override
	public String transactionIdKeyInResponse() {
		// TODO Auto-generated method stub
		return "ORDERID";
	}

	private Transaction transformRawResponse(NicResponse resp, Transaction currentStatus) {

		Transaction.TxnStatusEnum status = Transaction.TxnStatusEnum.PENDING;

		if (resp.getStatus().equalsIgnoreCase("TXN_SUCCESS"))
			status = Transaction.TxnStatusEnum.SUCCESS;
		else if (resp.getStatus().equalsIgnoreCase("TXN_FAILURE"))
			status = Transaction.TxnStatusEnum.FAILURE;

		return Transaction.builder().txnId(currentStatus.getTxnId())
				.txnAmount(Utils.formatAmtAsRupee(resp.getTxnAmount())).txnStatus(status)
				.gatewayPaymentMode(resp.getPaymentMode()).gatewayStatusCode(resp.getRespCode())
				.gatewayStatusMsg(resp.getRespMsg()).responseJson(resp).build();

	}

	@Override
	public String generateRedirectFormData(Transaction transaction) {
		// TODO Auto-generated method stub
		return null;
	}

}
