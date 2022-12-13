package org.egov.user.abm.developer.contract;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter	
@Setter
public class AddInfo {

//	private String CIN_Number;
//	private String companyName;
//	private String dateOfCorporation;
//	private String registeredAddress;
//	private String email;
//	private String mobileNumber;
//	private String GST_Number;
//	private List<FinancialCapacity> financialCapacity;
//	private List<ShareholdingPattens> shareHoldingPatterens;
//	private List<DirectorsInformation> directorsInformation;
	private String showDevTypeFields;
	private String name;
	private String mobileNumberUser;
    private String cin_Number;
    private String companyName;
    private String incorporationDate;
    private String registeredAddress;
    private String email;
    private String registeredContactNo;
    private String gst_Number;

	private List<DirectorsInformation> directorsInformation;
	private List<ShareholdingPattens> shareHoldingPatterens;
	
	private String existingColonizer;
	
	private ExistingColonizerData existingColonizerData;
	
	private Other other;
	
    

}
