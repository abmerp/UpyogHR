package org.egov.pg.service.gateways.hdfc;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HdfcResponse {
	@JsonProperty("ApplicationNumber")
	private String ApplicationNumber;

	@JsonProperty("TXNID")
	private String txnId;

	@JsonProperty("BANKTXNID")
	private String bankTxnid;

	@JsonProperty("TXNAMOUNT")
	private String txnAmount;

	@JsonProperty("CURRENCY")
	private String currency;

	@JsonProperty("STATUS")
	private String status;

	@JsonProperty("RESPCODE")
	private String respCode;

	@JsonProperty("RESPMSG")
	private String respMsg;

	@JsonProperty("TXNDATE")
	private String txnDate;

	@JsonProperty("GATEWAYNAME")
	private String gatewayName;

	@JsonProperty("BANKNAME")
	private String bankName;

	@JsonProperty("PAYMENTMODE")
	private String paymentMode;

	@JsonProperty("TXNTYPE")
	private String txnType;

	@JsonProperty("REFUNDAMT")
	private String refundAmt;

}
