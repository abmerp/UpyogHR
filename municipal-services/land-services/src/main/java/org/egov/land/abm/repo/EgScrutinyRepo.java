package org.egov.land.abm.repo;

import java.util.List;

import org.egov.land.abm.newservices.entity.EgScrutiny;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EgScrutinyRepo extends JpaRepository<EgScrutiny, Long>{


	public List<EgScrutiny> findByApplicationIdOrderByUseridDescTsDesc(Integer applicationNumber);
	
	public EgScrutiny findByApplicationIdAndFieldIdL(Integer applicantId,String fieldId);//applicationId field_d
	
	public List<EgScrutiny> findByApplicationIdAndUseridOrderByUseridDesc(Integer applicantId,Integer userId);

	@Query(value="select s from EgScrutiny s where s.applicationId=?1  order by userId,created_on")
	public List<EgScrutiny> findByApplicationId(Integer applicationNumber);
	public EgScrutiny findById(Integer id);
	
	
	@Query(value="select s from EgScrutiny s where s.applicationId=?1 and s.userid=?2 order by created_on ")
	public List<EgScrutiny> findByApplicationIdAndUserid(Integer applicantId,Integer userId);

	
	public boolean existsByApplicationIdAndFieldIdLAndUseridAndServiceId(Integer applicationId,String fieldId,Integer userID,Integer serviceId);

	@Query(value="select s from EgScrutiny s where s.applicationId=?1 and s.fieldIdL=?2 and s.userid=?3 and s.serviceId=?4")
	public EgScrutiny isExistsByApplicationIdAndFieldIdLAndUseridAndServiceId(Integer applicationId,String fieldId,Integer userID,Integer serviceId);
}
