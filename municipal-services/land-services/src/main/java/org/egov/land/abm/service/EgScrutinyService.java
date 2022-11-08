package org.egov.land.abm.service;

import java.util.Date;
import java.util.List;

import org.egov.land.abm.models.EgScrutinyInfoRequest;
import org.egov.land.abm.newservices.entity.EgScrutiny;
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
			egScrutiny.setTs(new Date());
			return egScrutinyRepo.save(egScrutiny);
		} else {
			egScrutinyInfoRequest.getEgScrutiny().setTs(new Date());
			return egScrutinyRepo.save(egScrutinyInfoRequest.getEgScrutiny());
		}

	}

	public List<EgScrutiny> search(Integer applicationNumber, Integer userId) {

		if (userId != null && userId > 0) {
			return this.egScrutinyRepo.findByApplicationIdAndUserid(applicationNumber, userId);

		} else {
			return this.egScrutinyRepo.findByApplicationId(applicationNumber);
		}

	}

	public EgScrutiny findById(Integer id) {
		return this.egScrutinyRepo.findById(id);
	}

	public EgScrutiny findByApplicationIdAndField_d(Integer applicationId, String fieldId, Integer userId,
			Integer serviceId) {

		return this.egScrutinyRepo.isExistsByApplicationIdAndFieldIdLAndUseridAndServiceId(applicationId, fieldId,
				userId, serviceId);

	}

	public List<EgScrutiny> findByApplicationIdAndUserId(Integer applicantId, Integer fieldId) {
		return this.egScrutinyRepo.findByApplicationIdAndUseridOrderByUseridDesc(applicantId, fieldId);
	}

}
