package org.egov.tl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Import({ TracerConfiguration.class })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class TLConfiguration {

	@Value("${app.timezone}")
	private String timeZone;

	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	@Bean
	@Autowired
	public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		return converter;
	}

	// User Config
	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.context.path}")
	private String userContextPath;

	@Value("${egov.user.create.path}")
	private String userCreateEndpoint;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	@Value("${egov.user.update.path}")
	private String userUpdateEndpoint;

	@Value("${egov.user.username.prefix}")
	private String usernamePrefix;

	// Idgen Config
	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Value("${egov.idgen.tl.applicationNum.name}")
	private String applicationNumberIdgenNameTL;

	@Value("${egov.idgen.tl.applicationNum.format}")
	private String applicationNumberIdgenFormatTL;

	@Value("${egov.idgen.tl.licensenumber.name}")
	private String licenseNumberIdgenNameTL;

	@Value("${egov.idgen.tl.licensenumber.format}")
	private String licenseNumberIdgenFormatTL;

	@Value("${egov.idgen.bpareg.applicationNum.name}")
	private String applicationNumberIdgenNameBPA;

	@Value("${egov.idgen.bpareg.applicationNum.format}")
	private String applicationNumberIdgenFormatBPA;

	@Value("${egov.idgen.bpareg.licensenumber.name}")
	private String licenseNumberIdgenNameBPA;

	@Value("${egov.idgen.bpareg.licensenumber.format}")
	private String licenseNumberIdgenFormatBPA;

	// Persister Config
	@Value("${persister.save.tradelicense.topic}")
	private String saveTopic;

	@Value("${persister.update.tradelicense.topic}")
	private String updateTopic;

	@Value("${persister.update.tradelicense.workflow.topic}")
	private String updateWorkflowTopic;

	@Value("${persister.update.tradelicense.adhoc.topic}")
	private String updateAdhocTopic;

	// Location Config
	@Value("${egov.location.host}")
	private String locationHost;

	@Value("${egov.location.context.path}")
	private String locationContextPath;

	@Value("${egov.location.endpoint}")
	private String locationEndpoint;

	@Value("${egov.location.hierarchyTypeCode}")
	private String hierarchyTypeCode;

	@Value("${egov.tl.default.limit}")
	private Integer defaultLimit;

	@Value("${egov.tl.default.offset}")
	private Integer defaultOffset;

	@Value("${egov.tl.max.limit}")
	private Integer maxSearchLimit;

	// tradelicense Calculator
	@Value("${egov.tl.calculator.host}")
	private String calculatorHost;

	@Value("${egov.tl.calculator.calculate.endpoint}")
	private String calculateEndpointTL;

	@Value("${egov.tl.calculator.estimate.endpoint}")
	private String estimateEndpointTL;

	@Value("${egov.bpa.calculator.calculate.endpoint}")
	private String calculateEndpointBPA;

	@Value("${egov.tl.calculator.getBill.endpoint}")
	private String getBillEndpoint;

	@Value("${egov.billingservice.host}")
	private String billingHost;

	@Value("${egov.bill.gen.endpoint}")
	private String fetchBillEndpoint;

	// Institutional key word
	@Value("${egov.ownershipcategory.institutional}")
	private String institutional;

	@Value("${egov.receipt.businessserviceTL}")
	private String businessServiceTL;

	@Value("${egov.receipt.businessserviceBPA}")
	private String businessServiceBPA;

	// Property Service
	@Value("${egov.property.service.host}")
	private String propertyHost;

	@Value("${egov.property.service.context.path}")
	private String propertyContextPath;

	@Value("${egov.property.endpoint}")
	private String propertySearchEndpoint;

	// SMS
	@Value("${kafka.topics.notification.sms}")
	private String smsNotifTopic;

	@Value("${notification.sms.enabled.forTL}")
	private Boolean isTLSMSEnabled;

	@Value("${notification.sms.enabled.forBPA}")
	private Boolean isBPASMSEnabled;

	@Value("${notification.sms.enabled.forTLRENEWAL}")
	private Boolean isTLRENEWALSMSEnabled;

	// Localization
	@Value("${egov.localization.host}")
	private String localizationHost;

	@Value("${egov.localization.context.path}")
	private String localizationContextPath;

	@Value("${egov.localization.search.endpoint}")
	private String localizationSearchEndpoint;

	@Value("${egov.localization.statelevel}")
	private Boolean isLocalizationStateLevel;

	// MDMS
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndPoint;

	// Allowed Search Parameters
	@Value("${citizen.allowed.search.params}")
	private String allowedCitizenSearchParameters;

	@Value("${employee.allowed.search.params}")
	private String allowedEmployeeSearchParameters;

	@Value("${egov.tl.previous.allowed}")
	private Boolean isPreviousTLAllowed;

	@Value("${egov.tl.min.period}")
	private Long minPeriod;

	// Workflow
	@Value("${create.tl.workflow.name}")
	private String tlBusinessServiceValue;

	@Value("${workflow.context.path}")
	private String wfHost;

	@Value("${workflow.transition.path}")
	private String wfTransitionPath;

	@Value("${workflow.businessservice.search.path}")
	private String wfBusinessServiceSearchPath;

	@Value("${is.external.workflow.enabled}")
	private Boolean isExternalWorkFlowEnabled;

	@Value("${egov.tl.businessservices}")
	private String tlBusinessServices;

	// USER EVENTS
	@Value("${egov.ui.app.host}")
	private String uiAppHost;

	@Value("${egov.usr.events.create.topic}")
	private String saveUserEventsTopic;

	@Value("${egov.usr.events.pay.link}")
	private String payLink;

	@Value("${egov.msg.pay.link}")
	private String payLinkSMS;

	@Value("${egov.usr.events.pay.code}")
	private String payCode;

	@Value("${egov.user.event.notification.enabledForTL}")
	private Boolean isUserEventsNotificationEnabledForTL;

	@Value("${egov.user.event.notification.enabledForTLRenewal}")
	private Boolean isUserEventsNotificationEnabledForTLRenewal;

	@Value("${egov.user.event.notification.enabledForBPA}")
	private Boolean isUserEventsNotificationEnabledForBPA;

	@Value("${egov.usr.events.pay.triggers}")
	private String payTriggers;

	// Email
	@Value("${kafka.topics.notification.email}")
	private String emailNotifTopic;

	@Value("${notification.email.enabled}")
	private Boolean isEmailNotificationEnabled;

	@Value("${notification.email.enabled.forBPA}")
	private Boolean isEmailNotificationEnabledForBPA;

	// Reminder
	@Value("${egov.tl.reminder.period}")
	private Long reminderPeriod;

	@Value("${egov.tl.pagination.size}")
	private Integer paginationSize;

	@Value("${egov.tl.reminder.enable}")
	private Boolean isReminderEnabled;

	@Value("${egov.tl.batch.reminder.error.topic}")
	private String reminderErrorTopic;

	@Value("${egov.tl.batch.expire.error.topic}")
	private String expiryErrorTopic;

	// url shortner

	@Value("${egov.url.shortner.host}")
	private String urlShortnerHost;

	@Value("${tl.url.shortner.endpoint}")
	private String urlShortnerEndpoint;

	@Value("${egov.usr.events.view.application.triggers}")
	private String viewApplicationTriggers;

	@Value("${egov.usr.events.view.application.link}")
	private String viewApplicationLink;

	@Value("${egov.usr.events.view.application.code}")
	private String viewApplicationCode;

	@Value("${id.timezone}")
	private String egovAppTimeZone;

	// receipt
	@Value("${notification.url}")
	private String notificationUrl;

	@Value("${egov.download.receipt.link}")
	private String receiptDownloadLink;

	@Value("${egov.pg-service.host}")
	private String pgHost;

	@Value("${egov.pg-service.path}")
	private String pgPath;

	// Service Plan Application number Sequencce
	@Value("${egov.idgen.tlSP.applicationNum.name}")
	private String SPapplicationNumberIdgenNameTL;

	@Value("${egov.idgen.tlSP.applicationNum.format}")
	private String SPapplicationNumberIdgenFormatTL;

	// Service Plan Save Topic
	@Value("${persister.save.ServicePlan.topic}")
	private String SPsaveTopic;

	// Service Plan Update Topic
	@Value("${persister.update.ServicePlan.topic}")
	private String SPupdateTopic;

	@Value("${persister.bankguarantee.new.save}")
	private String saveNewBankGuaranteeTopic;

	@Value("${egov.idgen.tl.bankguarantee.new.name}")
	private String newBankGuaranteeApplNoIdGenName;

	@Value("${egov.idgen.tl.bankguarantee.new.format}")
	private String newBankGuaranteeApplNoIdGenFormat;

	@Value("${persister.bankguarantee.new.update}")
	private String updateNewBankGuaranteeTopic;

	@Value("${persister.bankguarantee.renew.save}")
	private String saveRenewBankGuaranteeTopic;

	@Value("${egov.idgen.tl.bankguarantee.renew.name}")
	private String renewBankGuaranteeApplNoIdGenName;

	@Value("${egov.idgen.tl.bankguarantee.renew.format}")
	private String renewBankGuaranteeApplNoIdGenFormat;

	// Electrical Plan Application number Sequencce
	@Value("${egov.idgen.tlEP.applicationNum.name}")
	private String EPapplicationNumberIdgenNameTL;

	@Value("${egov.idgen.tlEP.applicationNum.format}")
	private String EPapplicationNumberIdgenFormatTL;

	// Electrical Plan Save Topic
	@Value("${persister.save.ElectricalPlan.topic}")
	private String EPsaveTopic;

	// Electrical Plan Update Topic
	@Value("${persister.update.ElectricalPlan.topic}")
	private String EPupdateTopic;

	@Value("${persister.update.tradelicenses.fees.topic}")
	private String tlFeeTopic;

	// hrms

	@Value("${egov.hrms.host}")
	private String hrmsHost;

	@Value("${egov.hrms.workDir.path}")
	private String hrmsContextPath;

	// change beneficial
	@Value("${persister.changrebeneficial.save}")
	private String saveChangreBeneficialTopic;

	@Value("${persister.changrebeneficial.update}")
	private String updateChangreBeneficialTopic;
	
	@Value("${persister.changrebeneficial.updatePayment}")
	private String updatePaymentChangreBeneficialTopic;

	// renewal license
	@Value("${persister.renewallicense.save}")
	private String saveRenewalLicenseTopic;

	@Value("${persister.renewallicense.update}")
	private String updateRenewalLicenseTopic;

	@Value("${egov.idgen.tlAS.applicationNum.name}")
	private String approvalStandardApplicationName;
	@Value("${egov.idgen.tlAS.applicationNum.format}")
	private String approvalStandardformat;

	@Value("${egov.idgen.tlRLP.applicationNum.name}")
	private String revisedLayoutPlanName;
	
	@Value("${egov.idgen.tlRLP.applicationNum.format}")
	private String revisedLayoutPlanFormat;
}
