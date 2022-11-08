package org.egov.land.abm.newservices.entity;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

import javax.persistence.Entity;
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
@Entity
@Builder
@Table(name = "eg_service_plan")
public class ServicePlan implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String loiNumber;
	private boolean undertaking;
	private boolean selfCertifiedDrawingsFromCharetedEng;
	private String selfCertifiedDrawingFromEmpaneledDoc;
	private String environmentalClearance;
	private String shapeFileAsPerTemplate;
	private String autoCadFile;
	private String certifieadCopyOfThePlan;

}
