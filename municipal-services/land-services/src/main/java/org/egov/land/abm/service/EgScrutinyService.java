package org.egov.land.abm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.land.abm.models.EgScrutinyInfoRequest;
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

		List<EgScrutiny> egScrutiny=this.egScrutinyRepo.findByApplication(applicationNumber);
		List<SecurityReport> securityReport = new ArrayList<SecurityReport>();
		SecurityReport object=null;
		List<UserComments>  comments=new ArrayList<UserComments>();
			for (EgScrutiny egScrutiny2 :egScrutiny) {
				
				object  = new SecurityReport();
				object.setName("Palam");
				comments=new ArrayList<UserComments>();
				object.setName(egScrutiny2.getFieldIdL());
				object.setValue(egScrutiny2.getFieldValue());
				for (EgScrutiny egScrutiny3 :egScrutiny) {
			if(egScrutiny3.getFieldIdL().equalsIgnoreCase(object.getName()))
					{
				UserComments comments2 = new UserComments();
				comments2.setEmployeeName(egScrutiny3.getEmployeeName());
				comments2.setDesignation(egScrutiny3.getDesignation());
				comments2.setRemarks(egScrutiny3.getComment());
				comments2.setIsApproved(egScrutiny3.getIsApproved());
				comments.add(comments2);
					}
				}
				
				object.setEmployees(comments);
				securityReport.add(object);
				
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
