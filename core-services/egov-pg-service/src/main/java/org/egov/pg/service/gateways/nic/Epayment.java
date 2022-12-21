package org.egov.pg.service.gateways.nic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.egov.pg.models.Transaction;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Epayment {
	private String applicationNumber;
	private String GRN;
	private String status;
	private Date validUpto;
	private String paymentType;
	private String amount;
	private String CIN;
	private String bankCode;
	private Date transactionDate;
	
	
	
	

}
