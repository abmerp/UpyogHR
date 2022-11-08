package org.egov.land.calcutaor;

import java.util.Map;

import org.egov.land.util.LandUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalculatorImpl implements Calculator {

	@Autowired
	LandUtil landUtil;

	private double areaInSqmtr(String arce) {
		return (AREA * Double.valueOf(arce));
	}

	public  FeesTypeCalculationDto feesTypeCalculation(CalculatorRequest calculatorRequest)
		{
		Map<String,String> mdms ;
		FeesTypeCalculationDto feesTypeCalculationDto = new FeesTypeCalculationDto();	
		switch (calculatorRequest.getPotenialZone()) {
		
		case ZONE_HYPER: 
		
			switch (calculatorRequest.getPurposeCode()) { 
			
			case PURPOSE_ITP:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				
				break;
				
				
			case PURPOSE_ITC:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				
				feesTypeCalculationDto.setScrutinyFeeChargesCal((PERCENTAGE1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 2.5f * 10
						+ PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * Long.getLong(mdms.get("scrutinyFeeCharges")) * 0.1f));
				
				mdms.get("licenseFeeCharges");
						mdms.get("conversionCharges");
								mdms.get("externalDevelopmentCharges");
										mdms.get("stateInfrastructureDevelopmentCharges");
										
				break;
			case PURPOSE_IPL:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_IPA:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_RGP:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_SGC:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_DDJAY_APHP:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_NILP:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_TODGH:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_CIR:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_AHP:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_CIS:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_MLU_CZ:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_MLU_RZ:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_LDEF:
			mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
			
				break;
			case PURPOSE_NILPC:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_TODCOMM:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_TODIT:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_TODMUD:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_CPL:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_SPRPRGH:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_DRH:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
			case PURPOSE_RHP:
				mdms = (Map)landUtil.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),calculatorRequest.getPurposeCode());
				break;
				
			}
			break;
		}
		
		return feesTypeCalculationDto;
	
		}
}