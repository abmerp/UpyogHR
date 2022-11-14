package org.egov.land.abm.newservices.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the eg_liecnse_ui_fields database table.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="eg_liecnse_ui_fields")
//@NamedQuery(name="EgLiecnseUiField.findAll", query="SELECT e FROM EgLiecnseUiField e")
public class EgLiecnseUiField implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="EG_LIECNSE_UI_FIELDS_ID_GENERATOR", sequenceName="EG_LIECNSE_UI_FIELDS_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EG_LIECNSE_UI_FIELDS_ID_GENERATOR")
	private Integer id;

	@Column(name="field_name")
	private String fieldName;


}