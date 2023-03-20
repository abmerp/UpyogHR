package org.egov.persistence.repository;

import org.springframework.stereotype.Service;

@Service
public class MessageCode {
	
	enum MsgCode {
		
		APPROVAL("TCPMSG0001:approval"),
		PASSWORD_FORGOT_SSO_SERVICE("TCPMSG0002:forgotPassoword"),
		GRANT("TCPMSG0003:grant"),
		LOI("TCPMSG0004:loi"),
		OBSERVATION_REPLAY("TCPMSG0005:observationReplay"),
		OBSERVATIONS("TCPMSG0006:observations"),
		PASSWORD_REQUEST("TCPMSG0007:passwordRequest"),
		PAYMENT("TCPMSG0008:payment"),
		REJECTION("TCPMSG0009:rejection"),
		RETURNED("TCPMSG0010:returned"),
		SUBMISSION_RESET("TCPMSG0011:submissionReset"),
		SUCCESSFULL_RESET("TCPMSG0012:successfullReset"),
		LOGIN("TCPMSG0013:login");

	    public final String label;

	    private MsgCode(String label) {
	        this.label = label;
	    }
	}
	
	public String getMessageCode(String msgCodeName) {
		String msgCode="";
		for(MsgCode msg:MsgCode.values()) {
			String[] msgLable=msg.label.split(":");
			if(msgLable[1].equals(msgCodeName)) {
				msgCode=msgLable[0];
				break;
			}
		}
		
		return msgCode;
	}
//	public static void main(String[] args) {
//		System.out.println(new MessageCode().getMessageCode("submissionReset"));
//	}

}
