package org.egov.land.abm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.land.abm.models.EgScrutinyInfoRequest;
import org.egov.land.abm.models.EmployeeSecurtinyReport;
import org.egov.land.abm.models.FiledDetails;
import org.egov.land.abm.newservices.entity.EgScrutiny;
import org.egov.land.abm.newservices.entity.SecurityReport;
import org.egov.land.abm.newservices.entity.UserComments;
import org.egov.land.abm.repo.EgScrutinyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EgScrutinyService {

	@Autowired
	EgScrutinyRepo egScrutinyRepo;

	public EgScrutiny createAndUpdateEgScrutiny(EgScrutinyInfoRequest egScrutinyInfoRequest) {

		boolean isExist = egScrutinyRepo.existsByApplicationIdAndFieldIdLAndUseridAndServiceId(
				egScrutinyInfoRequest.getEgScrutiny().getApplicationId(),
				egScrutinyInfoRequest.getEgScrutiny().getFieldIdL(), egScrutinyInfoRequest.getEgScrutiny().getUserid(),
				egScrutinyInfoRequest.getEgScrutiny().getServiceId());

		if (isExist) {
			EgScrutiny egScrutiny = egScrutinyRepo.isExistsByApplicationIdAndFieldIdLAndUseridAndServiceId(
					egScrutinyInfoRequest.getEgScrutiny().getApplicationId(),
					egScrutinyInfoRequest.getEgScrutiny().getFieldIdL(),
					egScrutinyInfoRequest.getEgScrutiny().getUserid(),
					egScrutinyInfoRequest.getEgScrutiny().getServiceId());
			egScrutiny.setComment(egScrutinyInfoRequest.getEgScrutiny().getComment());
			egScrutiny.setCreatedOn(egScrutinyInfoRequest.getEgScrutiny().getCreatedOn());
			egScrutiny.setIsApproved(egScrutinyInfoRequest.getEgScrutiny().getIsApproved());
			egScrutiny.setFieldValue(egScrutinyInfoRequest.getEgScrutiny().getFieldValue());
			egScrutiny.setIsLOIPart(egScrutinyInfoRequest.getEgScrutiny().getIsLOIPart());
			egScrutiny.setTs(new Date());
			return egScrutinyRepo.save(egScrutiny);
		} else {
			egScrutinyInfoRequest.getEgScrutiny().setTs(new Date());
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

		List<EgScrutiny> egScrutiny = this.egScrutinyRepo.findByApplication(applicationNumber);
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

		}

		return securityReport;

	}

	public List<EmployeeSecurtinyReport> search3(String applicationNumber, Integer userId) {

		List<EgScrutiny> egScrutiny = this.egScrutinyRepo.findByApplication(applicationNumber);
		List<EmployeeSecurtinyReport> securityReport = new ArrayList<EmployeeSecurtinyReport>();
		EmployeeSecurtinyReport object = null;
		List<FiledDetails> approvedfiledDetails = new ArrayList<FiledDetails>();
		List<FiledDetails> disApprovedfiledDetails = new ArrayList<FiledDetails>();
		List<FiledDetails> condApprovedfiledDetails = new ArrayList<FiledDetails>();

		boolean isExisting = false;
		for (EgScrutiny egScrutiny2 : egScrutiny) {

			object = new EmployeeSecurtinyReport();

			approvedfiledDetails = new ArrayList<FiledDetails>();
			object.setEmployeeName(egScrutiny2.getEmployeeName());
			object.setRole(egScrutiny2.getRole());
			object.setDesignation(egScrutiny2.getDesignation());
			object.setCreatedOn(egScrutiny2.getCreatedOn());
			object.setApplicationStatus(egScrutiny2.getApplicationStatus());

			for (EgScrutiny egScrutiny3 : egScrutiny) {
				if (egScrutiny3.getApplicationStatus().equalsIgnoreCase(object.getApplicationStatus())
						&& egScrutiny3.getDesignation().equalsIgnoreCase(object.getDesignation())
						&& egScrutiny3.getRole().equalsIgnoreCase(object.getRole())&& !egScrutiny3.getFieldIdL().equalsIgnoreCase(egScrutiny2.getFieldIdL()) ) {
					FiledDetails comments2 = new FiledDetails();
					comments2.setName(egScrutiny3.getFieldIdL());
					comments2.setRemarks(egScrutiny3.getComment());
					comments2.setIsApproved(egScrutiny3.getIsApproved());
					if(egScrutiny3.getIsApproved().equalsIgnoreCase("In Order"))
					approvedfiledDetails.add(comments2);
					else if(egScrutiny3.getIsApproved().equalsIgnoreCase("Not In Order"))
							disApprovedfiledDetails.add(comments2);
					else
						if(egScrutiny3.getIsApproved().equalsIgnoreCase("conditional"))
								condApprovedfiledDetails.add(comments2);

				}
			}

			for (EmployeeSecurtinyReport securityReportt : securityReport) {
				if (securityReportt.getApplicationStatus().equalsIgnoreCase(object.getApplicationStatus())
						&& securityReportt.getDesignation().equalsIgnoreCase(object.getDesignation())
						&& securityReportt.getRole().equalsIgnoreCase(object.getRole())) {
					// securityReportt.getEmployees().add(comments.get(comments.size()-1));
					isExisting = true;
					break;
				}
			}

			object.setApprovedfiledDetails(approvedfiledDetails);
			object.setDisApprovedfiledDetails(disApprovedfiledDetails);
			object.setCondApprovedfiledDetails(condApprovedfiledDetails);
			
			if (!isExisting)
			{
				securityReport.add(object);

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

}
