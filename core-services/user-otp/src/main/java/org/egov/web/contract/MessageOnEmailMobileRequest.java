package org.egov.web.contract;

import lombok.*;

import java.util.Map;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class MessageOnEmailMobileRequest {
   
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
    
	@Pattern(regexp = "^[0-9]{10,13}$",message="Invalid mobile number")
    private String mobileNumber;
   
    @Email(message="Invalid EmailId")
    @NotNull
    private String emailId;
    
    @NotNull
    private String emailSubject;
    
    @NotNull
    private String tenantId;
    
    @NotNull
    @Pattern(regexp = "^[0-4]{1,1}$",message="Invalid Input ,  Note: 0 for OTP, 1 for TRANSACTION, 2 for PROMOTION,3 for  NOTIFICATION,4 for OTHERS")
    private String category;
    
//    @NotNull
//    private String templateId;
    
    @NotNull
    private String type;
    
    @NotNull
    @Pattern(regexp = "^[1-3]{1,1}$",message="Invalid Input ,  Note: Message send on ,1 for Email, 2 for Mobile, 3 for Both")
    private String isMessageOnEmailMobile; // 1:-email,2:-mobile,3:-both
  
    @NotNull
    private String userType;
    
    @NotNull
    @JsonProperty("messageParameter")
    private Map<String,Object> messageParameter;
  
}


