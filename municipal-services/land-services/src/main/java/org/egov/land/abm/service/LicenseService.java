package org.egov.land.abm.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.egov.common.contract.request.User;
import org.egov.land.abm.models.LicenseServiceRequestInfo;
import org.egov.land.abm.newservices.entity.LicenseServiceDao;
import org.egov.land.abm.newservices.pojo.LicenseDetails;
import org.egov.land.abm.repo.LicenseServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LicenseService {

	@Autowired
	private ThirPartyAPiCall thirPartyAPiCall;
	@Autowired
	LicenseServiceRepo newServiceInfoRepo;
	@Autowired
	EntityManager em;
	private long id = 1;

	@Transactional
	public LicenseServiceDao createNewServic(LicenseServiceRequestInfo newServiceInfo, User user) {

		List<LicenseDetails> newServiceInfoData;

		LicenseServiceDao newServiceIn;
		List<LicenseDetails> newServiceInfoDatas;
		if (newServiceInfo.getId() != null && newServiceInfo.getId() > 0) {

			newServiceIn = em.find(LicenseServiceDao.class, newServiceInfo.getId());

			newServiceInfoData = newServiceIn.getNewServiceInfoData();
			float cv = newServiceIn.getCurrentVersion() + 0.1f;

			for (LicenseDetails newobj : newServiceInfoData) {

				if (newobj.getVer() == newServiceIn.getCurrentVersion()) {

					switch (newServiceInfo.getPageName()) {
					case "ApplicantInfo": {
						newobj.setApplicantInfo(newServiceInfo.getNewServiceInfoData().getApplicantInfo());
						break;
					}
					case "ApplicantPurpose": {
						newobj.setApplicantPurpose(newServiceInfo.getNewServiceInfoData().getApplicantPurpose());
						break;
					}
					case "LandSchedule": {
						newobj.setLandSchedule(newServiceInfo.getNewServiceInfoData().getLandSchedule());
						break;
					}
					case "DetailsofAppliedLand": {
						newobj.setDetailsofAppliedLand(
								newServiceInfo.getNewServiceInfoData().getDetailsofAppliedLand());
						break;
					}
					case "FeesAndCharges": {
						newobj.setFeesAndCharges(newServiceInfo.getNewServiceInfoData().getFeesAndCharges());
						break;
					}
					}

					newobj.setVer(cv);
					newServiceIn.getNewServiceInfoData().add(newobj);
					break;
				}
			}

			newServiceIn.setUpdatedDate(new Date());

			newServiceIn.setUpdateddBy(newServiceInfo.getUpdateddBy());
			newServiceIn.setCurrentVersion(cv);

		} else {
			newServiceInfoDatas = new ArrayList<>();
			newServiceIn = new LicenseServiceDao();
			newServiceIn.setCreatedBy(newServiceInfo.getCreatedBy());
			newServiceIn.setCreatedDate(new Date());
			newServiceIn.setUpdatedDate(new Date());
			newServiceIn.setApplicationNumber(newServiceInfo.getApplicationStatus());
			
			newServiceInfo.getNewServiceInfoData().setVer(0.1f);
			newServiceIn.setUpdateddBy(newServiceInfo.getUpdateddBy());
			newServiceInfoDatas.add(newServiceInfo.getNewServiceInfoData());
			newServiceIn.setNewServiceInfoData(newServiceInfoDatas);
			newServiceIn.setCurrentVersion(0.1f);
		}

		// ********************transaction number***************.//
		String transactionNumber;
		if (newServiceIn.getApplication_Status().equalsIgnoreCase("SUBBMIT")) {
			Map<String, Object> authtoken = new HashMap<String, Object>();
			Map<String, Object> mapTNum = new HashMap<String, Object>();
			authtoken.put("UserId", user.getId());
			authtoken.put("UserLoginId", user.getId());
			authtoken.put("Email", user.getEmailId());
			mapTNum.put("UserLoginId", user.getId());
			mapTNum.put("TpUserId", user.getId());
			mapTNum.put("EmailId", user.getEmailId());
			mapTNum.put("MobNo", user.getMobileNumber());
			transactionNumber = thirPartyAPiCall.generateTransactionNumber(mapTNum, authtoken).getBody().get("Value")
					.toString();
		}
		return newServiceInfoRepo.save(newServiceIn);
	}

	public LicenseServiceDao getNewServicesInfoById(Long id) {

		LicenseServiceDao newServiceInfo = newServiceInfoRepo.getOne(id);
		System.out.println("new service info size : " + newServiceInfo.getNewServiceInfoData().size());
		for (int i = 0; i < newServiceInfo.getNewServiceInfoData().size(); i++) {
			if (newServiceInfo.getCurrentVersion() == newServiceInfo.getNewServiceInfoData().get(i).getVer()) {
				newServiceInfo.setNewServiceInfoData(Arrays.asList(newServiceInfo.getNewServiceInfoData().get(i)));
			}
		}
		return newServiceInfo;
	}

	public List<LicenseServiceDao> getNewServicesInfoAll() {
		return newServiceInfoRepo.findAll();
	}

	public List<String> getApplicantsNumber() {
		return this.newServiceInfoRepo.getApplicantsNumber();
	}

	public void postTransactionDeatil(Long applicationNumber, User user) {

		String dairyNumber;
		String caseNumber;
		String applicationNmber;
		String saveTransaction;
        DateTimeFormatter formatter = 
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        LocalDateTime localDateTime = LocalDateTime.now();
        String date=formatter.format(localDateTime);
		Map<String, Object> authtoken = new HashMap<String, Object>();
		authtoken.put("UserId", user.getId());
		authtoken.put("UserLoginId", user.getId());
		authtoken.put("EmailId", user.getEmailId());
		List<LicenseDetails> newServiceInfoData;
		if (applicationNumber != null && applicationNumber > 0) {

			LicenseServiceDao newServiceIn = em.find(LicenseServiceDao.class, applicationNumber);

			newServiceInfoData = newServiceIn.getNewServiceInfoData();
		

			for (LicenseDetails newobj : newServiceInfoData) {

				if (newobj.getVer() == newServiceIn.getCurrentVersion()) {

				/************************************************
				 * Dairy Number End Here
				 *****************************/
				Map<String, Object> mapDNo = new HashMap<String, Object>();

				mapDNo.put("Village", newobj.getApplicantInfo().getVillage());
				mapDNo.put("DiaryDate",date);
				mapDNo.put("ReceivedFrom", user.getUserName());
				mapDNo.put("UserId", "1234");
				mapDNo.put("DistrictCode",
						newobj.getApplicantPurpose().getDistrict());
				mapDNo.put("UserLoginId", user.getId());
				dairyNumber = thirPartyAPiCall.generateDiaryNumber(mapDNo, authtoken).getBody().get("Value").toString();
				System.out.println("dairyNumber"+dairyNumber);

				/************************************************
				 * End Here
				 *****************************/
				// case number
				Map<String, Object> mapCNO = new HashMap<String, Object>();
				mapCNO.put("DiaryNo", dairyNumber);
				mapCNO.put("DiaryDate", date);
				mapCNO.put("DeveloperId", 2);
				mapCNO.put("PurposeId", 2);
				mapCNO.put("StartDate", date);
				mapCNO.put("DistrictCode",
						"0618");
			//	mapCNO.put("Village", newobj.getApplicantInfo().getVillage());
//				mapCNO.put("ChallanAmount",
//						newobj.getFeesAndCharges().getPayableNow());
				mapCNO.put("ChallanAmount","12.5");
				mapCNO.put("UserId", "2");
				mapCNO.put("UserLoginId",user.getId());
				caseNumber = thirPartyAPiCall.generateCaseNumber(mapCNO, authtoken).getBody().get("Value").toString();
				System.out.println("caseNumber"+caseNumber);
				/************************************************
				 * End Here
				 *****************************/
				// application number
				Map<String, Object> mapANo = new HashMap<String, Object>();
				mapANo.put("DiaryNo", dairyNumber);
				mapANo.put("DiaryDate", date);
				mapANo.put("TotalArea", newobj.getFeesAndCharges().getTotalArea());
				mapANo.put("Village", "0618");
				//mapANo.put("PurposeId", newobj.getApplicantPurpose().getPurposeDd());
				mapANo.put("PurposeId", "2");
				mapANo.put("NameofOwner", 12.5);
				mapANo.put("DateOfHearing", date);
				mapANo.put("DateForFilingOfReply", date);
				mapANo.put("UserId", "2");
				mapANo.put("UserLoginId",user.getId());
				applicationNmber = thirPartyAPiCall.generateApplicationNumber(mapANo, authtoken).getBody().get("Value")
						.toString();
				System.out.println("applicationNmber"+applicationNmber);

				/************************************************
				 * End Here
				 *****************************/
				/************************************************
				 * satrt transaction save
				 *****************************/
				Map<String, Object> map3 = new HashMap<String, Object>();
				map3.put("UserName", user.getUserName());
				map3.put("EmailId", user.getEmailId());
				map3.put("MobNo", user.getMobileNumber());
				map3.put("TxnNo", "");
				map3.put("TxnAmount", newobj.getFeesAndCharges().getPayableNow());
				map3.put("NameofOwner", newobj.getApplicantPurpose()
						.getApplicationPurposeData1().getLandOwner());
				map3.put("LicenceFeeNla",
						newobj.getFeesAndCharges().getLicenseFee());
				map3.put("ScrutinyFeeNla",
						newobj.getFeesAndCharges().getScrutinyFee());
				map3.put("UserId", user.getId());
				map3.put("UserLoginId", user.getId());
				map3.put("TpUserId", user.getId());
				//TODO Renu to Add these two vaues
				map3.put("PaymentMode", "online");
			
				map3.put("PayAgreegator", "PNB");
				map3.put("LcApplicantName", user.getUserName());
				map3.put("LcPurpose", newobj.getApplicantPurpose().getPurposeDd());
				map3.put("LcDevelopmentPlan", newobj.getDetailsofAppliedLand()
						.getDetailsAppliedLand6().getDevelopmentPlan());
				map3.put("LcDistrict", newobj.getApplicantPurpose().getDistrict());
				saveTransaction = thirPartyAPiCall.saveTransactionData(map3, authtoken).getBody().get("Value")
						.toString();
				System.out.println("saveTransaction"+saveTransaction);

				/************************************************
				 * End Here
				 *****************************/
			break;
				}
				
			}

		}
		
		
		
	}
	
	
	public NewServiceInfo findByLoiNumber(String loiNumber) {
		return this.newServiceInfoRepo.findByLoiNumber(loiNumber);
	}
	public boolean existsByLoiNumber(String loiNumber) {
		return this.newServiceInfoRepo.existsByLoiNumber(loiNumber);
	}
}