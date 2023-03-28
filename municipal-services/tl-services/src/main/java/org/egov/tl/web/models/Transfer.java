package org.egov.tl.web.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="transfer")
//@NamedQuery(name="Transfer.findAll", query="SELECT e FROM Transfer e")
public class Transfer implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	/*
	 * @Id
	 * 
	 * @SequenceGenerator(name="ZONE_PLAN_ID_GENERATOR",
	 * sequenceName="ZONE_PLAN_SEQ")
	 * 
	 * @GeneratedValue(strategy=GenerationType.SEQUENCE,
	 * generator="ZONE_PLAN_ID_GENERATOR") private Integer id;
	 */
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="LicenseNo")
	private String licenseNo;

	@Column(name="SelectType")
	private String selectType;

	@Column(name="AraeInAcres")
	private String araeInAcres;

	@Column(name="UndertakingThirdParty")
	private String undertakingThirdParty;
	
	@Column(name="ColonizerSeeking")
	private String colonizerSeeking;
	
	@Column(name="ConsentLetter")
	private String consentLetter;

	@Column(name="BoardResolution")
	private String boardResolution;

	@Column(name="ObjectionCertificate")
	private String objectionCertificate;

	@Column(name="TechnicalFinancialCapacity")
	private String technicalFinancialCapacity;
	
	@Column(name="UndertakingBalance")
	private String undertakingBalance;
	
	@Column(name="JustificationRequest")
	private String justificationRequest;

	@Column(name="AdministrativeCharges")
	private String administrativeCharges;

	@Column(name="StatusRegarding")
	private String statusRegarding;

	@Column(name="RegistrationStatus")
	private String registrationStatus;
	
	@Column(name="OtherDocument")
	private String otherDocument;
	
	@Column(name="CreationThirdParty")
	private String creationThirdParty;

	@Column(name="ColonizerSeekingTransfer")
	private String colonizerSeekingTransfer;

	@Column(name="ConsentLetterNewEntity")
	private String consentLetterNewEntity;

	@Column(name="BoardResolutionSignatory")
	private String boardResolutionSignatory;
	
	@Column(name="StatusRegistration")
	private String statusRegistration;
	
	@Column(name="DocumentOther")
	private String documentOther;

	@Column(name="ThirdPartyLicensedArea")
	private String thirdPartyLicensedArea;

	@Column(name="NewEntityChange")
	private String newEntityChange;

	@Column(name="AuthorizedSignatory")
	private String authorizedSignatory;
	
	@Column(name="NoObjection")
	private String noObjection;
	
	@Column(name="DocumentsTechnicalFianncial")
	private String documentsTechnicalFianncial;

	@Column(name="UndertakingPay")
	private String undertakingPay;

	@Column(name="JustificationChange")
	private String justificationChange;

	@Column(name="RequestJustification")
	private String requestJustification;
	
	@Column(name="AdministrativeChargesCases")
	private String administrativeChargesCases;
	
	@Column(name="RegistrationRera")
	private String registrationRera;
	
	@Column(name="Document")
	private String document;

	@Column(name="CraetionLicensedArea")
	private String craetionLicensedArea;

	@Column(name="JustificationEntity")
	private String justificationEntity;

	@Column(name="ResolutionBoardSignatory")
	private String resolutionBoardSignatory;
	
	@Column(name="RegistrationProjectRera")
	private String registrationProjectRera;
	
	@Column(name="AnyOtherDoc")
	private String anyOtherDoc;
	
	@Column(name="FormHorizontalRadios")
	private String formHorizontalRadios;


}
