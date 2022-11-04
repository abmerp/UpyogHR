package org.egov.pg.service.gateways.nic;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ManualPayment {
	private int ApplicationNumber;
	private int GRN;
	private String Status;
	private Date Valid_Upto;
	private String Paymenttype;

}
