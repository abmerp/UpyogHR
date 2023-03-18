package org.egov.domain.service;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.egov.domain.exception.UserAlreadyExistInSystemException;
import org.egov.domain.exception.UserMobileNumberNotFoundException;
import org.egov.domain.exception.UserNotExistingInSystemException;
import org.egov.domain.exception.UserNotFoundException;
import org.egov.domain.model.OtpRequest;
import org.egov.domain.model.OtpRequestType;
import org.egov.domain.model.User;
import org.egov.persistence.repository.OtpEmailRepository;
import org.egov.persistence.repository.OtpRepository;
import org.egov.persistence.repository.OtpSMSRepository;
import org.egov.persistence.repository.UserRepository;
import org.egov.web.contract.MessageOnEmailMobileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
@Slf4j
public class OtpService {

    private OtpRepository otpRepository;
    private OtpSMSRepository otpSMSSender;
    private OtpEmailRepository otpEmailRepository;
    private UserRepository userRepository;

    @Autowired
    private LocalizationService localizationService;
    
    private String message="";
    
    @Autowired
    public OtpService(OtpRepository otpRepository, OtpSMSRepository otpSMSSender, OtpEmailRepository otpEmailRepository,
                      UserRepository userRepository) {
        this.otpRepository = otpRepository;
        this.otpSMSSender = otpSMSSender;
        this.otpEmailRepository = otpEmailRepository;
        this.userRepository = userRepository;
    }

    public void sendOtp(OtpRequest otpRequest) {
        otpRequest.validate();
        if (otpRequest.isRegistrationRequestType() || otpRequest.isLoginRequestType()) {
            sendOtpForUserRegistration(otpRequest);
        } else {
            sendOtpForPasswordReset(otpRequest);
        }
    }

    private void sendOtpForUserRegistration(OtpRequest otpRequest) {
        final User matchingUser = userRepository.fetchUser(otpRequest.getMobileNumber(), otpRequest.getTenantId(),
                otpRequest.getUserType());

        if (otpRequest.isRegistrationRequestType() && null != matchingUser)
            throw new UserAlreadyExistInSystemException();
        else if (otpRequest.isLoginRequestType() && null == matchingUser)
            throw new UserNotExistingInSystemException();

        final String otpNumber = otpRepository.fetchOtp(otpRequest);
        otpSMSSender.send(otpRequest, otpNumber);
    }

    private void sendOtpForPasswordReset(OtpRequest otpRequest) {
        final User matchingUser = userRepository.fetchUser(otpRequest.getMobileNumber(), otpRequest.getTenantId(),
                otpRequest.getUserType());
        if (null == matchingUser) {
            throw new UserNotFoundException();
        }
        if (null == matchingUser.getMobileNumber() || matchingUser.getMobileNumber().isEmpty())
            throw new UserMobileNumberNotFoundException();
        try {
            final String otpNumber = otpRepository.fetchOtp(otpRequest);
            otpRequest.setMobileNumber(matchingUser.getMobileNumber());
            otpSMSSender.send(otpRequest, otpNumber);
            otpEmailRepository.send(matchingUser.getEmail(), otpNumber);
        } catch (Exception e) {
            log.error("Exception while fetching otp: ", e);
        }
    }
    
  /********************************** message send on email and mobile start ******************************************/
    
    public void sendMessage(MessageOnEmailMobileRequest messageOnEmailMobileRequest) {
    	sendStatusMessage(messageOnEmailMobileRequest);
	   }
   
    private void sendStatusMessage(MessageOnEmailMobileRequest messageOnEmailMobileRequest) {
//       	Map<String, String> localisedMsgs = localizationService.getLocalisedMessages(messageOnEmailMobileRequest.getTenantId(), "en_IN", "egov-user");

    	String username=null;
    	if(messageOnEmailMobileRequest.getIsMessageOnEmailMobile().equals("1")) {
    		username=messageOnEmailMobileRequest.getEmailId();
    	}else {
    		username=messageOnEmailMobileRequest.getMobileNumber();
    	}
		final User matchingUser = userRepository.fetchUser(username, messageOnEmailMobileRequest.getTenantId(),
				messageOnEmailMobileRequest.getUserType());
		if (matchingUser==null) {
		    throw new UserNotExistingInSystemException();
		}

	 otpSMSSender.sendMessage(messageOnEmailMobileRequest,message);
     
    }
    public Map<String, String> isValidRequest(BindingResult bindingResult,MessageOnEmailMobileRequest messageOnEmailMobileRequest) {
    	 Map<String, Object> localisedMsgs = localizationService.getLocalisedMsg(messageOnEmailMobileRequest.getTenantId(), "en_IN", "egov-user",messageOnEmailMobileRequest.getMsgCode());
         	
    	   Map<String,Object> parameter=messageOnEmailMobileRequest.getMessageParameter();
	       if(localisedMsgs.get("msgCode")!=null&&"TCPMSG00013".equals(localisedMsgs.get("msgCode"))) {
	    	  OtpRequest otpRequest=OtpRequest.builder().mobileNumber(messageOnEmailMobileRequest.getMobileNumber()).tenantId("hr").type(OtpRequestType.LOGIN).userType(messageOnEmailMobileRequest.getUserType()).build();
		      final String otpNumber = otpRepository.fetchOtp(otpRequest);
		      parameter.put("otp", otpNumber);
		      parameter=parameter;
    	   }	
	    
	   
    	    Map<String, String> errors = new HashMap<>();
    	    if(bindingResult.hasErrors()) {
	    	    bindingResult.getAllErrors().forEach((error) -> {
	    	    	String fieldName = ((FieldError) error).getField();
    	    	    String errorMessage = error.getDefaultMessage();
			    	
	    	    	if(messageOnEmailMobileRequest.getIsMessageOnEmailMobile().equals("1")) {
	    	    		if(!fieldName.equals("mobileNumber")) {
	    	    			 errors.put(fieldName, errorMessage);
				    	}
		    	 	}else if(messageOnEmailMobileRequest.getIsMessageOnEmailMobile().equals("2")) {
		    	 		if(!fieldName.equals("emailId")) {
	    	    			 errors.put(fieldName, errorMessage);
				    	}else if(!fieldName.equals("emailSubject")) {
	    	    			 errors.put(fieldName, errorMessage);
				    	}
	    	    	}else{
	    	    		 errors.put(fieldName, errorMessage);
	    	    	}
	    	    	
	    	    });
    	    }
    	    if(localisedMsgs.isEmpty()||localisedMsgs.get("message")==null||localisedMsgs.get("msgCode")==null){
 	    	   errors.put("msgCode", "Invalid msgCode");
 	    	}else {
 	    		message=keepAllParemeterInMessage(localisedMsgs.get("message").toString(),parameter,localisedMsgs.get("msgCode").toString());
 	    	}
    	    return errors;
    	
    }
    
    public String keepAllParemeterInMessage(String msg,Map<String,Object> parameter,String msgCode) {
    	
    	if(msgCode.equals("TCPMSG0001")) {
    	   msg.replaceAll("#grant", parameter.get("grant").toString());
           msg.replaceAll("#applicationNo", parameter.get("applicationNo").toString());
        }else if(msgCode.equals("TCPMSG0002")) {
           msg.replaceAll("#userName", parameter.get("userName").toString());
        }else if(msgCode.equals("TCPMSG0003")) {
           msg.replace("#grant", parameter.get("grant").toString());
           msg.replace("#applicationNo", parameter.get("applicationNo").toString());
           msg.replace("#dated", parameter.get("dated").toString());
        }else if(msgCode.equals("TCPMSG0004")) {
    	   msg.replace("#grant", parameter.get("grant").toString());
           msg.replace("#applicationNo", parameter.get("applicationNo").toString());
           msg.replace("#dated", parameter.get("dated").toString());
        }else if(msgCode.equals("TCPMSG0005")) {
    	   msg.replace("#diaryNo", parameter.get("diaryNo").toString());
           msg.replace("#applicationNo", parameter.get("applicationNo").toString());
           msg.replace("#dated", parameter.get("dated").toString());
    	}else if(msgCode.equals("TCPMSG0006")) {
		   msg.replace("#diaryNo", parameter.get("diaryNo").toString());
	       msg.replace("#applicationNo", parameter.get("applicationNo").toString());
	       msg.replace("#userEmailId", parameter.get("userEmailId").toString());
        }else if(msgCode.equals("TCPMSG0007")) {
 	       msg.replace("#userName", parameter.get("userName").toString());
 	    }else if(msgCode.equals("TCPMSG0008")) {
 	       msg.replace("#transactionNo", parameter.get("transactionNo").toString());
 	       msg.replace("#grNo", parameter.get("grNo").toString());
    	}else if(msgCode.equals("TCPMSG0009")) {
    	   msg.replace("#applicationNo", parameter.get("applicationNo").toString());
        }else if(msgCode.equals("TCPMSG00010")) {
    	   msg.replace("#applicationNo", parameter.get("applicationNo").toString());
        }else if(msgCode.equals("TCPMSG00011")) {
    	   msg.replace("#applicationNo", parameter.get("applicationNo").toString());
        }else if(msgCode.equals("TCPMSG00012")) {
    		
    	}else if(msgCode.equals("TCPMSG00013")) {
    		 msg.replace("#otp", parameter.get("otp").toString());
    	}	
    	msg=msg;
    	return msg;
    }
    
	/********************************** message send on email and mobile end ******************************************/
    

}
