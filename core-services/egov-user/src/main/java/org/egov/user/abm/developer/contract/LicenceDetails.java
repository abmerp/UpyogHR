package org.egov.user.abm.developer.contract;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenceDetails {
	private String name;
    private String mobileNumber;
   
	private String village;
	private String tehsil;
	private String state;
	private String district;
    private String email;
    private String dob;
    private String PanNumber;
    private String addressLineOne;
    private String addressLineTwo;
    private String addressLineThree;
    private String addressLineFour;
    private String city;
    private String pincode;
    private String addressSameAsPermanent;
    private String addressLineOneCorrespondence;
    private String addressLineTwoCorrespondence;
    private String addressLineThreeCorrespondence;
    private String addressLineFourCorrespondence;
    private String cityCorrespondence;
    private String pincodeCorrespondence;
    private List<Gender> gender;

}
