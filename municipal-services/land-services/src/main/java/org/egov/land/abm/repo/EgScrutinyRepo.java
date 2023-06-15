package org.egov.land.abm.repo;

import java.util.List;

import org.egov.land.abm.newservices.entity.EgScrutiny;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EgScrutinyRepo extends JpaRepository<EgScrutiny, Long> {

	public List<EgScrutiny> findByApplicationIdOrderByUseridDescTsDesc(String applicationNumber);

	public EgScrutiny findByApplicationIdAndFieldIdL(String applicantId, String fieldId);// applicationId field_d

	public List<EgScrutiny> findByApplicationIdAndUseridOrderByUseridDesc(String applicantId, Integer userId);

	@Query(value = "select s from EgScrutiny s where s.applicationId=?1  order by userId,created_on")
	public List<EgScrutiny> findByApplicationId(String applicationNumber);

	public EgScrutiny findById(Integer id);

	@Query(value = "select s from EgScrutiny s where s.applicationId=?1  order by created_on DESC")
	public List<EgScrutiny> findByApplication(String applicationNumber);

	@Query(value = "select s from EgScrutiny s where s.applicationId=?1 and s.isApproved not in ('Noting','Performa')  order by created_on DESC")
	public List<EgScrutiny> findByApplicationSearch(String applicationNumber);

	@Query(value = "select s from EgScrutiny s where s.applicationId=?1 and s.isApproved not in ('Not In Order','conditional','In Order')  order by created_on ASC")
	public List<EgScrutiny> findByApplicationSearch5(String applicationNumber);

	@Query(value = "select s from EgScrutiny s where s.applicationId=?1 and s.role in(?2) and s.isApproved not in ('Noting','Performa') order by created_on DESC")
	public List<EgScrutiny> findByApplication_roles(String applicationNumber, List<String> roles);
	@Query(value = "select s from EgScrutiny s where s.applicationId=?1 and s.userid=?2 order by created_on DESC")
	public List<EgScrutiny> findByApplicationIdAndUserid(String applicantId, Integer userId);

	@Query(value = "select s from EgScrutiny s where s.applicationId=?1 and s.fieldIdL =?2 and s.userid=?3 and s.serviceId= ?4 and s.applicationStatus =?5 and s.employeeName=?6 and s.role=?7 and s.isApproved = ?8 order by created_on DESC")
	public EgScrutiny findByApplicationIdAndFieldIdLAndUseridAndServiceId(String applicationId, String fieldId,
			Integer userID, Integer serviceId, String applicationStatus, String employeeName, String role,
			String status);

//	public boolean existsByApplicationIdAndFieldIdLAndUseridAndServiceId(String applicationId, String fieldId,
//			Integer userID, Integer serviceId, String applicationStatus, String employeeName, String role,
//			String status);
	
	public boolean existsByApplicationIdAndFieldIdLAndUseridAndServiceId(String applicationId, String fieldId,
			Integer userID, Integer serviceId);

	@Query(value = "select s from EgScrutiny s where s.applicationId=?1 and s.fieldIdL=?2 and s.userid=?3 and s.serviceId=?4")
	public EgScrutiny isExistsByApplicationIdAndFieldIdLAndUseridAndServiceId(String applicationId, String fieldId,
			Integer userID, Integer serviceId);
}
