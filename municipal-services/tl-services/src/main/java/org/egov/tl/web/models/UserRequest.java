package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import org.egov.tl.config.UserServiceConstants;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
	   private Long id;
	    
	    private Long parentid;
	    @SafeHtml
	    @Size(max = 64)
	    private String userName;

	    @SafeHtml
	    @Size(max = 5)
	    private String salutation;

	    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
	    @Size(max = 50)
	    private String name;

	    @Pattern(regexp = UserServiceConstants.PATTERN_GENDER)
	    @Size(max = 15)
	    private String gender;

	    @Pattern(regexp = UserServiceConstants.PATTERN_MOBILE)
	    private String mobileNumber;
	    
	    @Pattern(regexp = UserServiceConstants.PATTERN_MOBILE)
	    private String alternatemobilenumber;

	    @Email
	    @Size(max = 128)
	    private String emailId;

	    @SafeHtml
	    @Size(max = 50)
	    private String altContactNumber;

	    @SafeHtml
	    @Size(max = 10)
	    private String pan;

	    @SafeHtml
	    @Size(max = 20)
	    private String aadhaarNumber;

	    @SafeHtml
	    @Size(max = 300)
	    private String permanentAddress;

	    @SafeHtml
	    @Pattern(regexp = UserServiceConstants.PATTERN_CITY)
	    @Size(max = 50)
	    private String permanentCity;

	    @SafeHtml
	    @Pattern(regexp = UserServiceConstants.PATTERN_PINCODE)
	    @Size(max = 10)
	    private String permanentPinCode;

	    @SafeHtml
	    @Size(max = 300)
	    private String correspondenceAddress;

	    @Pattern(regexp = UserServiceConstants.PATTERN_CITY)
	    @Size(max = 50)
	    private String correspondenceCity;

	    @Pattern(regexp = UserServiceConstants.PATTERN_PINCODE)
	    @Size(max = 10)
	    private String correspondencePinCode;
	    private Boolean active;

	    @SafeHtml
	    @Size(max = 16)
	    private String locale;

	    private UserType type;
	    private Boolean accountLocked;
	    private Long accountLockedDate;

	    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
	    @Size(max = 50)
	    private String fatherOrHusbandName;
	    private GuardianRelation relationship;

	    @SafeHtml
	    @Size(max = 36)
	    private String signature;

	    @SafeHtml
	    @Size(max = 32)
	    private String bloodGroup;

	    @SafeHtml
	    @Size(max = 36)
	    private String photo;

	    @SafeHtml
	    @Size(max = 300)
	    private String identificationMark;
	    private Long createdBy;

	    @Size(max = 64)
	    private String password;

	    @SafeHtml
	    private String otpReference;
	    private Long lastModifiedBy;

	    @Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
	    @Size(max = 50)
	    private String tenantId;

	    @Value("roles")
	    private List<Role> roles;

	    @SafeHtml
	    @Size(max = 36)
	    private String uuid;


	    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	    private Date createdDate;
	    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	    private Date lastModifiedDate;
	    
	    @JsonFormat(pattern = "dd-MM-yyyy")
	    private Date dob;
	    
	    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	    private Date pwdExpiryDate;
}