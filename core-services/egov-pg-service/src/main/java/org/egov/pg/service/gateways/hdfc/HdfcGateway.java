package org.egov.pg.service.gateways.hdfc;

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
public class HdfcGateway implements Gateway {

	private static final String GATEWAY_NAME = "HDFC";
	private String MID;
	private String WORKING_KEY;
	private String ACCESS_CODE;
	private String REDIRECT_URL;
	private boolean ACTIVE;

	@Autowired
	private RestTemplate restTemplate;

	public HdfcGateway(RestTemplate restTemplate, Environment environment) {
		this.restTemplate = restTemplate;

		ACTIVE = Boolean.valueOf(environment.getRequiredProperty("hdfc.active"));
		MID = environment.getRequiredProperty("hdfc.merchant.id");
		WORKING_KEY = environment.getRequiredProperty("hdfc.working.Key");
		ACCESS_CODE = environment.getRequiredProperty("hdfc.access.code");
		REDIRECT_URL = environment.getRequiredProperty("hdfc.redirect.URL");

	}

	@Override
	public URI generateRedirectURI(Transaction transaction) {
		TreeMap<String, String> paramMap = new TreeMap<>();
		paramMap.put("CCAVENUEmerchantid", MID);
		paramMap.put("CCAVENUEredirectURL", transaction.getCallbackUrl());
		paramMap.put("CCAVENUEworkingKey", WORKING_KEY);
		paramMap.put("CCAVENUEaccessCode", ACCESS_CODE);
		paramMap.put("CCAVENUEredirectURL", REDIRECT_URL);		

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		paramMap.forEach((key, value) -> params.put(key, Collections.singletonList(value)));

		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(REDIRECT_URL).queryParams(params).build()
				.encode();

		return uriComponents.toUri();

	}

	@Override
	public Transaction fetchStatus(Transaction currentStatus, Map<String, String> params) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("CCAVENUEmerchantid", MID);

		try {
			/*
			 * String checkSum =
			 * CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(WORKING_KEY,
			 * treeMap); treeMap.put("CHECKSUMHASH", checkSum);
			 */

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());

			HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(treeMap, httpHeaders);

			ResponseEntity<HdfcResponse> response = restTemplate.postForEntity(REDIRECT_URL, httpEntity,
					HdfcResponse.class);
			return transformRawResponse(response.getBody(), currentStatus);
		} catch (RestClientException e) {
			log.error("Unable to fetch status from Paytm gateway", e);
			throw new CustomException("UNABLE_TO_FETCH_STATUS", "Unable to fetch status from Paytm gateway");
		} catch (Exception e) {
			log.error("Paytm Checksum generation failed", e);
			throw new CustomException("CHECKSUM_GEN_FAILED",
					"Hash generation failed, gateway redirect URI cannot be generated");
		}

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
		return "CCAVENUEmerchantid";
	}


	private Transaction transformRawResponse(HdfcResponse resp, Transaction currentStatus) {

		Transaction.TxnStatusEnum status = Transaction.TxnStatusEnum.PENDING;

		if (resp.getStatus().equalsIgnoreCase("TXN_SUCCESS"))
			status = Transaction.TxnStatusEnum.SUCCESS;
		else if (resp.getStatus().equalsIgnoreCase("TXN_FAILURE"))
			status = Transaction.TxnStatusEnum.FAILURE;

		return Transaction.builder().txnId(currentStatus.getTxnId())
				.txnAmount(Utils.formatAmtAsRupee(resp.getTxnAmount())).txnStatus(status).gatewayTxnId(resp.getTxnId())
				.gatewayPaymentMode(resp.getPaymentMode()).gatewayStatusCode(resp.getRespCode())
				.gatewayStatusMsg(resp.getRespMsg()).responseJson(resp).build();

	}

	@Override
	public String generateRedirectFormData(Transaction transaction) {
		// TODO Auto-generated method stub
		return null;
	}

}
