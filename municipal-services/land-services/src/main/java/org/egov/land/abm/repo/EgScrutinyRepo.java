package org.egov.land.abm.repo;

import java.util.List;

import org.egov.land.abm.newservices.entity.EgScrutiny;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EgScrutinyRepo extends JpaRepository<EgScrutiny, Long>{


	public List<EgScrutiny> findByApplicationIdOrderByUseridDescTsDesc(String applicationNumber);
	
	public EgScrutiny findByApplicationIdAndFieldIdL(String applicantId,String fieldId);//applicationId field_d
	
	public List<EgScrutiny> findByApplicationIdAndUseridOrderByUseridDesc(String applicantId,Integer userId);

	@Query(value="select s from EgScrutiny s where s.applicationId=?1  order by userId,created_on")
	public List<EgScrutiny> findByApplicationId(String applicationNumber);
	public EgScrutiny findById(Integer id);
	
	
	@Query(value="select s from EgScrutiny s where s.applicationId=?1  order by name,created_on")
	public List<EgScrutiny> findByApplication(String applicationNumber);
	
	
	@Query(value="select s from EgScrutiny s where s.applicationId=?1 and s.userid=?2 order by created_on ")
	public List<EgScrutiny> findByApplicationIdAndUserid(String applicantId,Integer userId);

	
	public boolean existsByApplicationIdAndFieldIdLAndUseridAndServiceId(String applicationId,String fieldId,Integer userID,Integer serviceId);

	@Query(value="select s from EgScrutiny s where s.applicationId=?1 and s.fieldIdL=?2 and s.userid=?3 and s.serviceId=?4")
	public EgScrutiny isExistsByApplicationIdAndFieldIdLAndUseridAndServiceId(String applicationId,String fieldId,Integer userID,Integer serviceId);
}
