package org.egov.land.abm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.egov.common.contract.request.User;
import org.egov.land.abm.models.NewServiceInfoModel;
import org.egov.land.abm.newservices.entity.NewServiceInfo;
import org.egov.land.abm.newservices.pojo.NewServiceInfoData;
import org.egov.land.abm.repo.NewServiceInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NewServiceInfoService {

	@Autowired
	private ThirPartyAPiCall thirPartyAPiCall;
	@Autowired
	NewServiceInfoRepo newServiceInfoRepo;
	@Autowired
	EntityManager em;
	private long id = 1;

	@Transactional
	public NewServiceInfo createNewServic(NewServiceInfoModel newServiceInfo, User user) {

		List<NewServiceInfoData> newServiceInfoData;

		NewServiceInfo newServiceIn;
		List<NewServiceInfoData> newServiceInfoDatas;
		if (newServiceInfo.getId() != null && newServiceInfo.getId() > 0) {

			newServiceIn = em.find(NewServiceInfo.class, newServiceInfo.getId(), LockModeType.PESSIMISTIC_WRITE);

			newServiceInfoData = newServiceIn.getNewServiceInfoData();
			float cv = newServiceIn.getCurrentVersion() + 0.1f;

			for (NewServiceInfoData newobj : newServiceInfoData) {

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
			newServiceIn = new NewServiceInfo();
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

	public NewServiceInfo getNewServicesInfoById(Long id) {

		NewServiceInfo newServiceInfo = newServiceInfoRepo.getOne(id);
		System.out.println("new service info size : " + newServiceInfo.getNewServiceInfoData().size());
		for (int i = 0; i < newServiceInfo.getNewServiceInfoData().size(); i++) {
			if (newServiceInfo.getCurrentVersion() == newServiceInfo.getNewServiceInfoData().get(i).getVer()) {
				newServiceInfo.setNewServiceInfoData(Arrays.asList(newServiceInfo.getNewServiceInfoData().get(i)));
			}
		}
		return newServiceInfo;
	}

	public List<NewServiceInfo> getNewServicesInfoAll() {
		return newServiceInfoRepo.findAll();
	}

	public List<String> getApplicantsNumber() {
		return this.newServiceInfoRepo.getApplicantsNumber();
	}

	private void method(Long applicationNumber, User user) {

		String dairyNumber;
		String caseNumber;
		String applicationNmber;
		String saveTransaction;
		Map<String, Object> authtoken = new HashMap<String, Object>();
		authtoken.put("UserId", user.getId());
		authtoken.put("UserLoginId", user.getId());
		authtoken.put("Email", user.getEmailId());

		if (applicationNumber != null && applicationNumber > 0) {

			NewServiceInfo newServiceIn = em.find(NewServiceInfo.class, applicationNumber,
					LockModeType.PESSIMISTIC_WRITE);

			for (int i = 0; i < newServiceIn.getNewServiceInfoData().size(); i++) {

				/************************************************
				 * Dairy Number End Here
				 *****************************/
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("Village", newServiceIn.getNewServiceInfoData().get(i).getApplicantInfo().getVillage());
				map.put("DiaryDate", new Date());
				map.put("ReceivedFrom", user.getUserName());
				map.put("UserId", user.getId());
				map.put("DistrictCode",
						newServiceIn.getNewServiceInfoData().get(i).getApplicantPurpose().getDistrict());
				map.put("UserLoginId", user.getId());
				dairyNumber = thirPartyAPiCall.generateDiaryNumber(map, authtoken).getBody().get("Value").toString();

				/************************************************
				 * End Here
				 *****************************/
				// case number
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("DiaryNo", dairyNumber);
				map1.put("DiaryDate", new Date());
				map1.put("DeveloperId", user.getId());
				map1.put("PurposeId", newServiceIn.getNewServiceInfoData().get(i).getApplicantPurpose().getPurposeDd());
				map1.put("StartDate", new Date());
				map.put("DistrictCode",
						newServiceIn.getNewServiceInfoData().get(i).getApplicantPurpose().getDistrict());
				map.put("Village", newServiceIn.getNewServiceInfoData().get(i).getApplicantInfo().getVillage());
				map1.put("ChallanAmount",
						newServiceIn.getNewServiceInfoData().get(i).getFeesAndCharges().getPayableNow());
				map1.put("UserId", user.getId());
				map1.put("UserLoginId", user.getId());
				caseNumber = thirPartyAPiCall.generateCaseNumber(map, authtoken).getBody().get("Value").toString();
				/************************************************
				 * End Here
				 *****************************/
				// application number
				Map<String, Object> map2 = new HashMap<String, Object>();
				map1.put("DiaryNo", dairyNumber);
				map1.put("DiaryDate", new Date());
				map1.put("TotalArea", newServiceIn.getNewServiceInfoData().get(i).getFeesAndCharges().getTotalArea());
				map.put("Village", newServiceIn.getNewServiceInfoData().get(i).getApplicantInfo().getVillage());
				map1.put("PurposeId", newServiceIn.getNewServiceInfoData().get(i).getApplicantPurpose().getPurposeDd());
				map1.put("NameofOwner", newServiceIn.getNewServiceInfoData().get(i).getApplicantPurpose()
						.getApplicationPurposeData1().getLandOwner());
				map1.put("DateOfHearing", new Date());
				map1.put("DateForFilingOfReply", new Date());
				map1.put("UserId", user.getId());
				map1.put("UserLoginId", user.getId());
				applicationNmber = thirPartyAPiCall.generateCaseNumber(map, authtoken).getBody().get("Value")
						.toString();
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
				map3.put("TxnAmount", newServiceIn.getNewServiceInfoData().get(i).getFeesAndCharges().getPayableNow());
				map3.put("NameofOwner", newServiceIn.getNewServiceInfoData().get(i).getApplicantPurpose()
						.getApplicationPurposeData1().getLandOwner());
				map3.put("LicenceFeeNla",
						newServiceIn.getNewServiceInfoData().get(i).getFeesAndCharges().getLicenseFee());
				map3.put("ScrutinyFeeNla",
						newServiceIn.getNewServiceInfoData().get(i).getFeesAndCharges().getScrutinyFee());
				map3.put("UserId", user.getId());
				map3.put("UserLoginId", user.getId());
				map3.put("TpUserId", user.getId());
				//TODO Renu to Add these two vaues
				map3.put("PaymentMode", "");
				map3.put("PayAgreegator", "");
				map3.put("LcApplicantName", user.getUserName());
				map3.put("LcPurpose", newServiceIn.getNewServiceInfoData().get(i).getApplicantPurpose().getPurposeDd());
				map3.put("LcDevelopmentPlan", newServiceIn.getNewServiceInfoData().get(i).getDetailsofAppliedLand()
						.getDetailsAppliedLand6().getDevelopmentPlan());
				map3.put("LcDistrict", newServiceIn.getNewServiceInfoData().get(i).getApplicantPurpose().getDistrict());
				saveTransaction = thirPartyAPiCall.saveTransactionData(map, authtoken).getBody().get("Value")
						.toString();

				/************************************************
				 * End Here
				 *****************************/

			}

		}
	}
}