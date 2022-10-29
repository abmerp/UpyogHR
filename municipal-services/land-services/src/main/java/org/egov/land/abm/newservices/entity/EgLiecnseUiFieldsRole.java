package org.egov.land.abm.newservices.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the eg_liecnse_ui_fields_role database table.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="eg_liecnse_ui_fields_role")
@NamedQuery(name="EgLiecnseUiFieldsRole.findAll", query="SELECT e FROM EgLiecnseUiFieldsRole e")
public class EgLiecnseUiFieldsRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="EG_LIECNSE_UI_FIELDS_ROLE_ID_GENERATOR", sequenceName="EG_LIECNSE_UI_FIELDS_ROLE_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EG_LIECNSE_UI_FIELDS_ROLE_ID_GENERATOR")
	private Integer id;

	@Column(name="eg_liecnse_ui_fields_id")
	private Integer egLiecnseUiFieldsId;

	@Column(name="role_id")
	private Integer roleId;

	
}