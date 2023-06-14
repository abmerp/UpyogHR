package org.egov.land.abm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.egov.land.abm.contract.PerformaContract;
import org.egov.land.abm.models.EgScrutinyInfoRequest;
import org.egov.land.abm.models.EmployeeSecurtinyReport;
import org.egov.land.abm.models.FiledDetails;
import org.egov.land.abm.newservices.entity.EgScrutiny;
import org.egov.land.abm.newservices.entity.SecurityReport;
import org.egov.land.abm.newservices.entity.UserComments;
import org.egov.land.abm.repo.EgScrutinyRepo;
import org.egov.land.service.LandEnrichmentService;
import org.postgresql.jdbc2.ArrayAssistantRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EgScrutinyService {

	@Autowired
	EgScrutinyRepo egScrutinyRepo;

	public EgScrutiny createAndUpdateEgScrutiny(EgScrutinyInfoRequest egScrutinyInfoRequest) {

		EgScrutiny egScrutiny = egScrutinyRepo.findByApplicationIdAndFieldIdLAndUseridAndServiceId(
				egScrutinyInfoRequest.getEgScrutiny().getApplicationId(),
				egScrutinyInfoRequest.getEgScrutiny().getFieldIdL(), egScrutinyInfoRequest.getEgScrutiny().getUserid(),
				egScrutinyInfoRequest.getEgScrutiny().getServiceId(),
				egScrutinyInfoRequest.getEgScrutiny().getApplicationStatus(),
				egScrutinyInfoRequest.getEgScrutiny().getEmployeeName(),
				egScrutinyInfoRequest.getEgScrutiny().getRole(), egScrutinyInfoRequest.getEgScrutiny().getIsApproved());

		if (egScrutiny != null) {
			egScrutiny = egScrutinyRepo.isExistsByApplicationIdAndFieldIdLAndUseridAndServiceId(
					egScrutinyInfoRequest.getEgScrutiny().getApplicationId(),
					egScrutinyInfoRequest.getEgScrutiny().getFieldIdL(),
					egScrutinyInfoRequest.getEgScrutiny().getUserid(),
					egScrutinyInfoRequest.getEgScrutiny().getServiceId());
			egScrutiny.setComment(egScrutinyInfoRequest.getEgScrutiny().getComment());
			egScrutiny.setCreatedOn(egScrutinyInfoRequest.getEgScrutiny().getCreatedOn());
			egScrutiny.setIsApproved(egScrutinyInfoRequest.getEgScrutiny().getIsApproved());
			egScrutiny.setFieldValue(egScrutinyInfoRequest.getEgScrutiny().getFieldValue());
			egScrutiny.setIsLOIPart(egScrutinyInfoRequest.getEgScrutiny().getIsLOIPart());
			egScrutiny.setTs(new java.sql.Time(new Date().getTime()));
			return egScrutinyRepo.save(egScrutiny);
		} else {
			egScrutinyInfoRequest.getEgScrutiny().setTs(new java.sql.Time(new Date().getTime()));
			egScrutinyInfoRequest.getEgScrutiny().setCreatedOn(new java.sql.Time(new Date().getTime()));
			return egScrutinyRepo.save(egScrutinyInfoRequest.getEgScrutiny());
		}

	}

	public List<EgScrutiny> search(String applicationNumber, Integer userId) {

		if (userId != null && userId > 0) {
			return this.egScrutinyRepo.findByApplicationIdAndUserid(applicationNumber, userId);

		} else {
			return this.egScrutinyRepo.findByApplicationId(applicationNumber);
		}

	}

	public List<SecurityReport> search2(String applicationNumber, Integer userId) {

		List<EgScrutiny> egScrutiny = this.egScrutinyRepo.findByApplicationSearch(applicationNumber);
		List<SecurityReport> securityReport = new ArrayList<SecurityReport>();
		SecurityReport object = null;
		List<UserComments> comments = new ArrayList<UserComments>();
		boolean isExisting = false;
		for (EgScrutiny egScrutiny2 : egScrutiny) {

			object = new SecurityReport();

			comments = new ArrayList<UserComments>();
			object.setName(egScrutiny2.getFieldIdL());
			object.setValue(egScrutiny2.getFieldValue());
			for (EgScrutiny egScrutiny3 : egScrutiny) {
				if (egScrutiny3.getFieldIdL().equalsIgnoreCase(object.getName())) {
					UserComments comments2 = new UserComments();
					comments2.setEmployeeName(egScrutiny3.getEmployeeName());
					comments2.setDesignation(egScrutiny3.getDesignation());
					comments2.setRole(egScrutiny3.getRole());
					comments2.setCreatedOn(egScrutiny3.getCreatedOn());
					comments2.setRemarks(egScrutiny3.getComment());
					comments2.setIsApproved(egScrutiny3.getIsApproved());
					comments2.setTs(egScrutiny3.getTs());
					comments.add(comments2);

				}
			}

			for (SecurityReport securityReportt : securityReport) {
				if (securityReportt.getName().equalsIgnoreCase(object.getName())) {
					// securityReportt.getEmployees().add(comments.get(comments.size()-1));
					isExisting = true;
					break;
				}
			}

			object.setEmployees(comments);
			if (!isExisting)
				securityReport.add(object);
			log.info("\t" + securityReport.size() + "\t");
		}

		return securityReport;

	}

	public List<EmployeeSecurtinyReport> search3(String applicationNumber, Integer userId) {

		List<EgScrutiny> egScrutiny = this.egScrutinyRepo.findByApplication(applicationNumber);
		List<EmployeeSecurtinyReport> securityReport = new ArrayList<EmployeeSecurtinyReport>();
		EmployeeSecurtinyReport object = null;
		List<FiledDetails> approvedfiledDetails = null;
		List<FiledDetails> disApprovedfiledDetails = null;
		List<FiledDetails> condApprovedfiledDetails = null;
		List<FiledDetails> performaFieldDetail = null;
		List<FiledDetails> notingDetail = null;
		List<EmployeeSecurtinyReport> securityReportf = new ArrayList<EmployeeSecurtinyReport>();

		for (EgScrutiny egScrutiny2 : egScrutiny) {
			// securityReport = new ArrayList<EmployeeSecurtinyReport>();
			object = new EmployeeSecurtinyReport();
			boolean isExisting = false;
			approvedfiledDetails = new ArrayList<FiledDetails>();
			disApprovedfiledDetails = new ArrayList<FiledDetails>();
			condApprovedfiledDetails = new ArrayList<FiledDetails>();
			performaFieldDetail = new ArrayList<FiledDetails>();
			notingDetail = new ArrayList<FiledDetails>();
			object.setEmployeeName(egScrutiny2.getEmployeeName());
			object.setUserID(egScrutiny2.getUserid().toString());
			object.setRole(egScrutiny2.getRole());
			object.setDesignation(egScrutiny2.getDesignation());
			object.setCreatedOn(egScrutiny2.getCreatedOn());
			object.setApplicationStatus(egScrutiny2.getApplicationStatus());
			object.setId(egScrutiny2.getId());
			object.setTs(egScrutiny2.getTs());
			int i = 0;
			for (EgScrutiny egScrutiny3 : egScrutiny) {
				if (egScrutiny3.getApplicationStatus().equalsIgnoreCase(object.getApplicationStatus())
						&& egScrutiny3.getUserid().toString().equalsIgnoreCase(object.getUserID())) {

					FiledDetails comments2 = new FiledDetails();
					comments2.setName(egScrutiny3.getFieldIdL());
					comments2.setRemarks(egScrutiny3.getComment());
					comments2.setIsApproved(egScrutiny3.getIsApproved());
					comments2.setValue(egScrutiny3.getFieldValue());
					if (egScrutiny3.getIsApproved().equalsIgnoreCase("In Order"))
						approvedfiledDetails.add(comments2);
					else if (egScrutiny3.getIsApproved().equalsIgnoreCase("Not In Order"))
						disApprovedfiledDetails.add(comments2);
					else if (egScrutiny3.getIsApproved().equalsIgnoreCase("conditional"))
						condApprovedfiledDetails.add(comments2);
					else if (egScrutiny3.getIsApproved().equalsIgnoreCase("Proforma"))
						performaFieldDetail.add(comments2);
					else if (egScrutiny3.getIsApproved().equalsIgnoreCase("Noting"))
						notingDetail.add(comments2);
					// egScrutiny.remove(i);
				}
				i++;

			}

			if (securityReport.size() > 0)
				for (EmployeeSecurtinyReport objecttmp : securityReport) {
					if ((objecttmp.getApplicationStatus().equalsIgnoreCase(object.getApplicationStatus())
							&& objecttmp.getUserID().equalsIgnoreCase(object.getUserID())
							&& objecttmp.getEmployeeName().equalsIgnoreCase(object.getEmployeeName()))) {
						isExisting = true;
						break;
					}
				}

			if (!isExisting) {
				object.setApprovedfiledDetails(approvedfiledDetails);
				object.setDisApprovedfiledDetails(disApprovedfiledDetails);
				object.setCondApprovedfiledDetails(condApprovedfiledDetails);
				object.setPerformaFieldDetail(performaFieldDetail);
				object.setNotingDetail(notingDetail);
				securityReport.add(object);
				// securityReportf.addAll(securityReport);
				log.info("\t" + securityReport.size() + "\t");

			}
		}

		return securityReport;

	}

	public List<EmployeeSecurtinyReport> search4(String applicationNumber, List<String> roles) {

		List<EgScrutiny> egScrutiny = this.egScrutinyRepo.findByApplication_roles(applicationNumber, roles);
		List<EmployeeSecurtinyReport> securityReport = new ArrayList<EmployeeSecurtinyReport>();
		EmployeeSecurtinyReport object = null;
		List<FiledDetails> approvedfiledDetails = null;
		List<FiledDetails> disApprovedfiledDetails = null;
		List<FiledDetails> condApprovedfiledDetails = null;
		List<EmployeeSecurtinyReport> securityReportf = new ArrayList<EmployeeSecurtinyReport>();

		for (EgScrutiny egScrutiny2 : egScrutiny) {
			// securityReport = new ArrayList<EmployeeSecurtinyReport>();
			object = new EmployeeSecurtinyReport();
			boolean isExisting = false;
			approvedfiledDetails = new ArrayList<FiledDetails>();
			disApprovedfiledDetails = new ArrayList<FiledDetails>();
			condApprovedfiledDetails = new ArrayList<FiledDetails>();
			object.setEmployeeName(egScrutiny2.getEmployeeName());
			object.setUserID(egScrutiny2.getUserid().toString());
			object.setRole(egScrutiny2.getRole());
			object.setDesignation(egScrutiny2.getDesignation());
			object.setCreatedOn(egScrutiny2.getCreatedOn());
			object.setApplicationStatus(egScrutiny2.getApplicationStatus());
			object.setTs(egScrutiny2.getTs());
			int i = 0;
			for (EgScrutiny egScrutiny3 : egScrutiny) {
				if (egScrutiny3.getApplicationStatus().equalsIgnoreCase(object.getApplicationStatus())
						&& egScrutiny3.getUserid().toString().equalsIgnoreCase(object.getUserID())) {

					FiledDetails comments2 = new FiledDetails();
					comments2.setName(egScrutiny3.getFieldIdL());
					comments2.setRemarks(egScrutiny3.getComment());
					comments2.setIsApproved(egScrutiny3.getIsApproved());
					comments2.setValue(egScrutiny3.getFieldValue());
					if (egScrutiny3.getIsApproved().equalsIgnoreCase("In Order"))
						approvedfiledDetails.add(comments2);
					else if (egScrutiny3.getIsApproved().equalsIgnoreCase("Not In Order"))
						disApprovedfiledDetails.add(comments2);
					else if (egScrutiny3.getIsApproved().equalsIgnoreCase("conditional"))
						condApprovedfiledDetails.add(comments2);
					// egScrutiny.remove(i);
				}
				i++;

			}

			if (securityReport.size() > 0)
				for (EmployeeSecurtinyReport objecttmp : securityReport) {
					if ((objecttmp.getApplicationStatus().equalsIgnoreCase(object.getApplicationStatus())
							&& objecttmp.getUserID().equalsIgnoreCase(object.getUserID())
							&& objecttmp.getEmployeeName().equalsIgnoreCase(object.getEmployeeName()))) {
						isExisting = true;
						break;
					}
				}

			if (!isExisting) {
				object.setApprovedfiledDetails(approvedfiledDetails);
				object.setDisApprovedfiledDetails(disApprovedfiledDetails);
				object.setCondApprovedfiledDetails(condApprovedfiledDetails);

				securityReport.add(object);
				// securityReportf.addAll(securityReport);
				log.info("\t" + securityReport.size() + "\t");

			}
		}

		return securityReport;

	}

	public List<EmployeeSecurtinyReport> search5(String applicationNumber) {

		List<EgScrutiny> egScrutiny = this.egScrutinyRepo.findByApplicationSearch5(applicationNumber);
		List<EmployeeSecurtinyReport> securityReport = new ArrayList<EmployeeSecurtinyReport>();
		EmployeeSecurtinyReport object = null;
//		List<FiledDetails> approvedfiledDetails = null;
//		List<FiledDetails> disApprovedfiledDetails = null;
//		List<FiledDetails> condApprovedfiledDetails = null;
		List<FiledDetails> performaFieldDetail = null;
		List<FiledDetails> notingDetail = null;
		List<EmployeeSecurtinyReport> securityReportf = new ArrayList<EmployeeSecurtinyReport>();

		for (EgScrutiny egScrutiny2 : egScrutiny) {
			// securityReport = new ArrayList<EmployeeSecurtinyReport>();
			object = new EmployeeSecurtinyReport();
			boolean isExisting = false;
//			approvedfiledDetails = new ArrayList<FiledDetails>();
//			disApprovedfiledDetails = new ArrayList<FiledDetails>();
//			condApprovedfiledDetails = new ArrayList<FiledDetails>();
			performaFieldDetail = new ArrayList<FiledDetails>();
			notingDetail = new ArrayList<FiledDetails>();
			object.setEmployeeName(egScrutiny2.getEmployeeName());
			object.setUserID(egScrutiny2.getUserid().toString());
			object.setRole(egScrutiny2.getRole());
			object.setDesignation(egScrutiny2.getDesignation());
			object.setCreatedOn(egScrutiny2.getCreatedOn());
			object.setApplicationStatus(egScrutiny2.getApplicationStatus());
			object.setId(egScrutiny2.getId());
			object.setTs(egScrutiny2.getTs());
			int i = 0;
			for (EgScrutiny egScrutiny3 : egScrutiny) {
				if (egScrutiny3.getApplicationStatus().equalsIgnoreCase(object.getApplicationStatus())
						&& egScrutiny3.getUserid().toString().equalsIgnoreCase(object.getUserID())) {

					FiledDetails comments2 = new FiledDetails();
					comments2.setName(egScrutiny3.getFieldIdL());
					comments2.setRemarks(egScrutiny3.getComment());
					comments2.setIsApproved(egScrutiny3.getIsApproved());
					comments2.setValue(egScrutiny3.getFieldValue());
//					if (egScrutiny3.getIsApproved().equalsIgnoreCase("In Order"))
//						approvedfiledDetails.add(comments2);
//					else if (egScrutiny3.getIsApproved().equalsIgnoreCase("Not In Order"))
//						disApprovedfiledDetails.add(comments2);
//					else if (egScrutiny3.getIsApproved().equalsIgnoreCase("conditional"))
//						condApprovedfiledDetails.add(comments2);

					if (egScrutiny3.getIsApproved().equalsIgnoreCase("Proforma"))
						performaFieldDetail.add(comments2);
					else if (egScrutiny3.getIsApproved().equalsIgnoreCase("Noting"))
						notingDetail.add(comments2);
					// egScrutiny.remove(i);
				}
				i++;

			}

			if (securityReport.size() > 0)
				for (EmployeeSecurtinyReport objecttmp : securityReport) {
					if ((objecttmp.getApplicationStatus().equalsIgnoreCase(object.getApplicationStatus())
							&& objecttmp.getUserID().equalsIgnoreCase(object.getUserID())
							&& objecttmp.getEmployeeName().equalsIgnoreCase(object.getEmployeeName()))) {

						isExisting = true;
						break;
					}
				}

			if (!isExisting) {
//				object.setApprovedfiledDetails(approvedfiledDetails);
//				object.setDisApprovedfiledDetails(disApprovedfiledDetails);
//				object.setCondApprovedfiledDetails(condApprovedfiledDetails);
				object.setPerformaFieldDetail(performaFieldDetail);
				object.setNotingDetail(notingDetail);
				securityReport.add(object);
				// securityReportf.addAll(securityReport);
				log.info("\t" + securityReport.size() + "\t");

			}
		}

		return securityReport;

	}

	public EgScrutiny findById(Integer id) {
		return this.egScrutinyRepo.findById(id);
	}

	public EgScrutiny findByApplicationIdAndField_d(String applicationId, String fieldId, Integer userId,
			Integer serviceId) {

		return this.egScrutinyRepo.isExistsByApplicationIdAndFieldIdLAndUseridAndServiceId(applicationId, fieldId,
				userId, serviceId);

	}

	public List<EgScrutiny> findByApplicationIdAndUserId(String applicantId, Integer fieldId) {
		return this.egScrutinyRepo.findByApplicationIdAndUseridOrderByUseridDesc(applicantId, fieldId);
	}

	public List<EgScrutiny> createAndUpdatePerforma(PerformaContract performaContract) {
		List<EgScrutiny> egScrutinyList = performaContract.getEgScrutiny();
		List<EgScrutiny> egScrutinyLists = new ArrayList<>();
		for (EgScrutiny egScrutiny : egScrutinyList) {
			EgScrutiny egScrutinys = egScrutinyRepo.findByApplicationIdAndFieldIdLAndUseridAndServiceId(
					egScrutiny.getApplicationId(), egScrutiny.getFieldIdL(), egScrutiny.getUserid(),
					egScrutiny.getServiceId(), egScrutiny.getApplicationStatus(), egScrutiny.getEmployeeName(),
					egScrutiny.getRole(), egScrutiny.getIsApproved());
			if (egScrutinys != null) {
				egScrutiny.setComment(egScrutiny.getComment());
				egScrutiny.setCreatedOn(new java.sql.Time(new Date().getTime()));
				egScrutiny.setIsApproved(egScrutiny.getIsApproved());
				egScrutiny.setFieldValue(egScrutiny.getFieldValue());
				egScrutiny.setIsLOIPart(egScrutiny.getIsLOIPart());
				egScrutiny.setId(egScrutinys.getId());
				egScrutiny.setTs(new java.sql.Time(new Date().getTime()));
				// return egScrutinyRepo.save(egScrutiny);
			} else {
				egScrutiny.setTs(new java.sql.Time(new Date().getTime()));
				egScrutiny.setCreatedOn(new java.sql.Time(new Date().getTime()));

			}
			egScrutinyLists.add(egScrutiny);

		}

		return egScrutinyRepo.saveAll(egScrutinyLists);

	}
}
