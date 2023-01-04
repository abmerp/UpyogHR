package org.egov.tl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.IdGenRepository;
import org.egov.tl.repository.rowmapper.SPRowMapper;
//import org.egov.land.abm.contract.ServicePlanRequest;
import org.egov.tl.service.repo.ServicePlanRepo;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
//import org.egov.tl.web.controllers.ServicePlanRequest;
import org.egov.tl.web.models.ServicePlan;
import org.egov.tl.web.models.ServicePlanContract;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.Idgen.IdResponse;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.egov.tracer.model.CustomException;
//import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.flywaydb.core.internal.jdbc.RowMapper;
//import org.egov.land.abm.contract.ServicePlanContract;
//import org.egov.land.abm.newservices.entity.ServicePlan;
//import org.egov.land.abm.repo.ServicePlanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Service
public class ServicePlanService {
	
	private static final String businessService_TL = "SERVICE_PLAN";

	@Autowired ServicePlanRepo servicePlanRepo;
	
	@Autowired private WorkflowIntegrator wfIntegrator;
	
	@Autowired private TLConfiguration config;
	
	@Autowired private IdGenRepository idGenRepository;
	
	@Autowired private Producer producer;
	
	@Autowired private TradeUtil tradeUtil;
	
	@Autowired private SPRowMapper spRowMapper;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
//	@Autowired JdbcTemplateObject jdbcTemplateObject;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public ServicePlanRequest createAndUpdate(ServicePlanContract servicePlanContract) {
		
		String uuid = servicePlanContract.getRequestInfo().getUserInfo().getUuid();
		
		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);
		
		
		ServicePlanRequest servicePlanRequest = servicePlanContract.getServicePlanRequest();
		
		List<String> applicationNumbers = null;
		 int count = 1 ;
		
		applicationNumbers = getIdList(servicePlanContract.getRequestInfo(), servicePlanRequest.getTenantID(), 
				config.getSPapplicationNumberIdgenNameTL(),
				config.getSPapplicationNumberIdgenFormatTL(), count );
		
		
		servicePlanRequest.setAuditDetails(auditDetails);
		servicePlanRequest.setApplicationNumber(applicationNumbers.get(0));
		
		TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();
//		TradeLicense tradeLicenseSP  = tradeLicenseRequest.getLicenses().set(0 , TradeLicense);
		TradeLicense tradeLicenseSP = new TradeLicense();
		List<TradeLicense> tradeLicenseSPlist = new ArrayList<>();
		tradeLicenseSP.setBusinessService(servicePlanRequest.getBusinessService());
		tradeLicenseSP.setAction(servicePlanRequest.getAction());
		tradeLicenseSP.setAssignee(servicePlanRequest.getAssignee());
		tradeLicenseSP.setApplicationNumber(servicePlanRequest.getLoiNumber());
		tradeLicenseSP.setWorkflowCode(businessService_TL);
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(businessService_TL);
		tradeLicenseSP.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseSP.setComment(servicePlanRequest.getComment());
		tradeLicenseSP.setWfDocuments(null);
		tradeLicenseSP.setTenantId(servicePlanRequest.getTenantID());
		tradeLicenseSP.setBusinessService(businessService_TL);
		
		tradeLicenseRequest.setRequestInfo(servicePlanContract.getRequestInfo());
		tradeLicenseSPlist.add(tradeLicenseSP);
		tradeLicenseRequest.setLicenses(tradeLicenseSPlist);
		
		wfIntegrator.callWorkFlow(tradeLicenseRequest);
		
		servicePlanRequest.setStatus(tradeLicenseRequest.getLicenses().get(0).getStatus());
		
		servicePlanContract.setServicePlanRequest(servicePlanRequest);
		
		 producer.push(config.getSPsaveTopic(), servicePlanContract);
		
	return 	servicePlanRequest ;
		
//		return this.servicePlanRepo.save(servicePlanContract.getServicePlanRequest().toBuilder());
	}
	
	
	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}
	
	public List<ServicePlanRequest> searchServicePlan(String loiNumber) {
		
		List<Object> preparedStatement = new ArrayList<>();
		
		 Map<String, String> paramMap = new HashedMap();
		StringBuilder builder;
		
		String query = "SELECT loi_number, auto_cad_file, certifiead_copy_of_the_plan, environmental_clearance, self_certified_drawing_from_empaneled_doc, self_certified_drawings_from_chareted_eng, shape_file_as_per_template, status, sp_action, undertaking, assignee, action, business_service, comment, tenantid, application_number , created_by, created_time, last_modified_by, last_modified_time\r\n"
				+ "FROM public.eg_service_plan\r\n"
				+ "WHERE ";
//				+ "loi_number='111111111111111111137'";
		
		builder = new StringBuilder(query);
		
//		if(loiNumber != null) {
			builder.append("loi_number= :LN");
			paramMap.put("LN", loiNumber);
			preparedStatement.add(loiNumber);
//			}
//		SqlParameterSource preparedStmtList;
		
//		List<T> query2 = jdbcTemplate.query(builder.toString() , (RowMapper<T>) spRowMapper ,  preparedStatement.toArray());
		
//		jdbcTemplate.query(query, null, null)
		
//		List<ServicePlanRequest> waterConnectionList =  jdbcTemplate.query(query,  spRowMapper, preparedStatement.toArray());
		
		List<ServicePlanRequest> query2 = namedParameterJdbcTemplate.query(builder.toString()  , paramMap , spRowMapper );
		
//		return this.servicePlanRepo.findByLoiNumber(loiNumber);
		return query2;
	}
}
