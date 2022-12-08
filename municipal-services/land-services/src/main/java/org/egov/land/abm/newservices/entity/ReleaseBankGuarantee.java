package org.egov.land.abm.newservices.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="eg_release_bank_guarantee")
public class ReleaseBankGuarantee {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="license_number")
	private String licenseNumber;
	
	@Column(name="idc_edc")
	private String idcOrEdc;
	
	@Column(name="full_partial")
	private String fullOrPartial;
	
	
	
}
