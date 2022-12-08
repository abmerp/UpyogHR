package org.egov.tl.service;

import java.lang.reflect.InvocationTargetException;
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
import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;

import org.egov.common.contract.request.User;
import org.egov.tl.service.dao.LicenseServiceDao;
import org.egov.tl.service.repo.LicenseServiceRepo;
import org.egov.tl.util.TLConstants;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.LicenseServiceRequest;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LicenseService {

	@Autowired
	private ThirPartyAPiCall thirPartyAPiCall;
	@Autowired
	LicenseServiceRepo newServiceInfoRepo;
	@Autowired
	EntityManager em;
	private long id = 1;
	@Autowired
	TradeLicenseService tradeLicenseService;

	@Transactional
	public LicenseServiceResponseInfo createNewServic(LicenseServiceRequest newServiceInfo)
			throws JsonProcessingException {

		List<LicenseDetails> newServiceInfoData;
		LicenseServiceResponseInfo objLicenseServiceRequestInfo = new LicenseServiceResponseInfo();
		LicenseServiceDao newServiceIn;
		List<LicenseDetails> newServiceInfoDatas = null;
		User user = newServiceInfo.getRequestInfo().getUserInfo();
		if (newServiceInfo.getId() != null && newServiceInfo.getId() > 0) {

			newServiceIn = em.find(LicenseServiceDao.class, newServiceInfo.getId());

			newServiceInfoData = newServiceIn.getNewServiceInfoData();
			float cv = newServiceIn.getCurrentVersion() + 0.1f;

			for (LicenseDetails newobj : newServiceInfoData) {

				if (newobj.getVer() == newServiceIn.getCurrentVersion()) {

					switch (newServiceInfo.getPageName()) {
					case "ApplicantInfo": {
						newobj.setApplicantInfo(newServiceInfo.getLicenseDetails().getApplicantInfo());
						break;
					}
					case "ApplicantPurpose": {
						newobj.setApplicantPurpose(newServiceInfo.getLicenseDetails().getApplicantPurpose());
						break;
					}
					case "LandSchedule": {
						newobj.setLandSchedule(newServiceInfo.getLicenseDetails().getLandSchedule());
						break;
					}
					case "DetailsofAppliedLand": {
						newobj.setDetailsofAppliedLand(newServiceInfo.getLicenseDetails().getDetailsofAppliedLand());
						break;
					}
					case "FeesAndCharges": {
						newobj.setFeesAndCharges(newServiceInfo.getLicenseDetails().getFeesAndCharges());
						break;
					}
					}

					newobj.setVer(cv);
					newServiceIn.getNewServiceInfoData().add(newobj);
					break;
				}
			}
			newServiceIn.setTenantId(newServiceInfo.getRequestInfo().getUserInfo().getTenantId());
			newServiceIn.setUpdatedDate(new Date());
			newServiceIn.setApplicationStatus(newServiceInfo.getApplicationStatus());
			newServiceIn.setUpdateddBy(newServiceInfo.getUpdateddBy());
			newServiceIn.setCurrentVersion(cv);

		} else {
			newServiceInfoDatas = new ArrayList<>();
			newServiceIn = new LicenseServiceDao();
			newServiceIn.setCreatedBy(newServiceInfo.getCreatedBy());
			newServiceIn.setCreatedDate(new Date());
			newServiceIn.setUpdatedDate(new Date());
			newServiceIn.setTenantId(newServiceInfo.getRequestInfo().getUserInfo().getTenantId());
			newServiceIn.setApplicationStatus(newServiceInfo.getApplicationStatus());
			newServiceIn.setApplicationNumber(newServiceInfo.getApplicationStatus());

			newServiceInfo.getLicenseDetails().setVer(0.1f);
			newServiceIn.setUpdateddBy(newServiceInfo.getUpdateddBy());
			newServiceInfoDatas.add(newServiceInfo.getLicenseDetails());
			newServiceIn.setNewServiceInfoData(newServiceInfoDatas);
			newServiceIn.setCurrentVersion(0.1f);
		}

		// ********************transaction number***************.//

		if (!StringUtil.isBlank(newServiceIn.getApplicationStatus())
				&& newServiceIn.getApplicationStatus().equalsIgnoreCase("INITIATE")) {
			TradeLicenseRequest request = new TradeLicenseRequest();
			request.setRequestInfo(newServiceInfo.getRequestInfo());

			TradeLicense tradeLicense = new TradeLicense();

			TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
			tradeLicense.setId(String.valueOf(newServiceInfo.getId()));
			// tradeLicense.setStatus(newServiceInfo.getApplicationStatus());
			tradeLicense.setAction("INITIATE");
			tradeLicense.setApplicationDate(new Date().getTime());
			// tradeLicense.getApplicationNumber();
			tradeLicense.setApplicationType(TradeLicense.ApplicationTypeEnum.NEW);
			// tradeLicense.getAssignee();
			// tradeLicense.getAuditDetails();
			tradeLicense.setBusinessService("TL");
			// tradeLicense.getCalculation();
			// tradeLicense.getComment();
			// tradeLicense.getFileStoreId();
			tradeLicense.setFinancialYear("2022-23");
			tradeLicense.setIssuedDate(new Date().getTime());
			// tradeLicense.getLicenseNumber();
			tradeLicense.setLicenseType(TradeLicense.LicenseTypeEnum.PERMANENT);
			tradeLicense.setTenantId(newServiceIn.getTenantId());
			tradeLicense.setTradeName(newServiceIn.getNewServiceInfoData().get(0).getApplicantPurpose().getPurpose());
//			tradeLicense.setValidFrom();
//			tradeLicense.setValidTo();
//			tradeLicense.setWfDocuments();
			tradeLicense.setWorkflowCode("NewTL");

			tradeLicense.setTradeLicenseDetail(tradeLicenseDetail);
			tradeLicenseDetail.setId(String.valueOf(newServiceInfo.getId()));
			tradeLicenseDetail.getAdditionalDetail();
			tradeLicenseDetail.getApplicationDocuments();
			tradeLicenseDetail.getChannel();
			tradeLicenseDetail.getOwners();
			tradeLicenseDetail.getVerificationDocuments();
			tradeLicenseDetail.setTradeType("NewTL");

			ObjectMapper mapper = new ObjectMapper();
			String data = mapper.writeValueAsString(newServiceInfoDatas);
			JsonNode jsonNode = mapper.readTree(data);
			tradeLicenseDetail.setAdditionalDetail(jsonNode);

			request.addLicensesItem(tradeLicense);
			List<TradeLicense> tradelicenses = tradeLicenseService.create(request, TLConstants.businessService_TL);
			// request.getLicenses().clear();
			request.setLicenses(tradelicenses);
			// tradeLicenseService.update(request, TLConstants.businessService_TL);
			newServiceIn.setApplicationNumber(tradelicenses.get(0).getApplicationNumber());

			Map<String, Object> authtoken = new HashMap<String, Object>();
			Map<String, Object> mapTNum = new HashMap<String, Object>();

			authtoken.put("UserId", user.getId());
			authtoken.put("TpUserId", user.getId());
			authtoken.put("EmailId", user.getEmailId());

			mapTNum.put("UserLoginId", user.getId());
			mapTNum.put("TpUserId", user.getId());
			mapTNum.put("EmailId", user.getEmailId());
			mapTNum.put("MobNo", user.getMobileNumber());

			String transactionNumber;

			transactionNumber = thirPartyAPiCall.generateTransactionNumber(mapTNum, authtoken).getBody().get("Value")
					.toString();
			log.info("TransactionNumber\t" + transactionNumber);

		}
		newServiceIn = newServiceInfoRepo.save(newServiceIn);
		try {
			BeanUtils.copyProperties(objLicenseServiceRequestInfo, newServiceIn);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return objLicenseServiceRequestInfo;
	}

	public LicenseServiceResponseInfo getNewServicesInfoById(Long id) {
		LicenseServiceResponseInfo licenseServiceResponseInfo = new LicenseServiceResponseInfo();
		LicenseServiceDao newServiceInfo = newServiceInfoRepo.getOne(id);
		System.out.println("new service info size : " + newServiceInfo.getNewServiceInfoData().size());
		for (int i = 0; i < newServiceInfo.getNewServiceInfoData().size(); i++) {
			if (newServiceInfo.getCurrentVersion() == newServiceInfo.getNewServiceInfoData().get(i).getVer()) {
				newServiceInfo.setNewServiceInfoData(Arrays.asList(newServiceInfo.getNewServiceInfoData().get(i)));
			}
		}
		try {
			BeanUtils.copyProperties(licenseServiceResponseInfo, newServiceInfo);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return licenseServiceResponseInfo;
	}

	public LicenseServiceResponseInfo getNewServicesInfoById(String applcationNUmber) {
		LicenseServiceResponseInfo licenseServiceResponseInfo = new LicenseServiceResponseInfo();
		LicenseServiceDao newServiceInfo = newServiceInfoRepo.findByAppNumber(applcationNUmber);
		System.out.println("new service info size : " + newServiceInfo.getNewServiceInfoData().size());
		for (int i = 0; i < newServiceInfo.getNewServiceInfoData().size(); i++) {
			if (newServiceInfo.getCurrentVersion() == newServiceInfo.getNewServiceInfoData().get(i).getVer()) {
				newServiceInfo.setNewServiceInfoData(Arrays.asList(newServiceInfo.getNewServiceInfoData().get(i)));
			}
		}
		try {
			BeanUtils.copyProperties(licenseServiceResponseInfo, newServiceInfo);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return licenseServiceResponseInfo;
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
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		LocalDateTime localDateTime = LocalDateTime.now();
		String date = formatter.format(localDateTime);

		Map<String, Object> authtoken = new HashMap<String, Object>();
		authtoken.put("UserId", user.getId());
		authtoken.put("TpUserId", user.getId());
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
					mapDNo.put("DiaryDate", date);
					mapDNo.put("ReceivedFrom", user.getUserName());
					mapDNo.put("UserId", "1234");
					mapDNo.put("DistrictCode", newobj.getApplicantPurpose().getDistrict());
					mapDNo.put("UserLoginId", user.getId());
					dairyNumber = thirPartyAPiCall.generateDiaryNumber(mapDNo, authtoken).getBody().get("Value")
							.toString();

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
					mapCNO.put("DistrictCode", "0618");
					mapCNO.put("Village", newobj.getApplicantInfo().getVillage());
					mapCNO.put("ChallanAmount", newobj.getFeesAndCharges().getPayableNow());
					mapCNO.put("ChallanAmount", "12.5");
					mapCNO.put("UserId", "2");
					mapCNO.put("UserLoginId", user.getId());
					caseNumber = thirPartyAPiCall.generateCaseNumber(mapCNO, authtoken).getBody().get("Value")
							.toString();
					System.out.println("caseNumber" + caseNumber);
					/************************************************
					 * End Here
					 *****************************/
					// application number
					Map<String, Object> mapANo = new HashMap<String, Object>();
					mapANo.put("DiaryNo", dairyNumber);
					mapANo.put("DiaryDate", date);
					mapANo.put("TotalArea", newobj.getFeesAndCharges().getTotalArea());
					mapANo.put("Village", "0618");
					mapANo.put("PurposeId", newobj.getApplicantPurpose().getPurpose());
					mapANo.put("PurposeId", "2");
					mapANo.put("NameofOwner", 12.5);
					mapANo.put("DateOfHearing", date);
					mapANo.put("DateForFilingOfReply", date);
					mapANo.put("UserId", "2");
					mapANo.put("UserLoginId", user.getId());
					applicationNmber = thirPartyAPiCall.generateApplicationNumber(mapANo, authtoken).getBody()
							.get("Value").toString();

					/************************************************
					 * End Here
					 *****************************/
					/************************************************
					 * starttransaction data
					 ************************/
					Map<String, Object> map3 = new HashMap<String, Object>();
					map3.put("UserName", user.getUserName());
					map3.put("EmailId", user.getEmailId());
					map3.put("MobNo", user.getMobileNumber());
					map3.put("TxnNo", "");
					map3.put("TxnAmount", newobj.getFeesAndCharges().getPayableNow());
					map3.put("NameofOwner", newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getLandOwner());
					map3.put("LicenceFeeNla", newobj.getFeesAndCharges().getLicenseFee());
					map3.put("ScrutinyFeeNla", newobj.getFeesAndCharges().getScrutinyFee());
					map3.put("UserId", user.getId());
					map3.put("UserLoginId", user.getId());
					map3.put("TpUserId", user.getId());
					// TODO Renu to Add these two vaues
					map3.put("PaymentMode", "online");
					map3.put("PayAgreegator", "PNB");

					map3.put("LcApplicantName", user.getUserName());
					map3.put("LcPurpose", newobj.getApplicantPurpose().getPurpose());
					// to do select development plan
					map3.put("LcDevelopmentPlan", newobj.getApplicantPurpose().getPotential());
					map3.put("LcDistrict", newobj.getApplicantPurpose().getDistrict());
					saveTransaction = thirPartyAPiCall.saveTransactionData(map3, authtoken).getBody().get("Value")
							.toString();

					/************************************************
					 * End Here
					 *****************************/
					break;
				}

			}

		}

	}

	public LicenseServiceDao findNewServicesInfoById(Long id) {

		LicenseServiceDao newServiceInfo = newServiceInfoRepo.getOne(id);
		System.out.println("new service info size : " + newServiceInfo.getNewServiceInfoData().size());
		for (int i = 0; i < newServiceInfo.getNewServiceInfoData().size(); i++) {
			if (newServiceInfo.getCurrentVersion() == newServiceInfo.getNewServiceInfoData().get(i).getVer()) {
				newServiceInfo.setNewServiceInfoData(Arrays.asList(newServiceInfo.getNewServiceInfoData().get(i)));
			}
		}
		return newServiceInfo;
	}

	public LicenseServiceDao findByLoiNumber(String loiNumber) {
		return this.newServiceInfoRepo.findByLoiNumber(loiNumber);
	}

	public boolean existsByLoiNumber(String loiNumber) {
		return this.newServiceInfoRepo.existsByLoiNumber(loiNumber);
	}

	public boolean existsById(Long id) {
		return this.newServiceInfoRepo.existsById(id);
	}
}