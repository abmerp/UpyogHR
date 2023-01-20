CREATE TABLE IF NOT EXISTS  public.eg_electric_plan_audit (
	id bigserial NOT NULL,
	auto_cad varchar(255) NULL,
	environmental_clearance varchar(255) NULL,
	verified_plan varchar(255) NULL,
	loi_number varchar(255) NULL,
	self_centred_drawing varchar(255) NULL,
	shap_file_template varchar(255) NULL,
	tenantid varchar(255) NULL,
	created_by varchar(255) NULL,
	created_time int8 NULL,
	last_modified_by varchar(255) NULL,
	last_modified_time int8 NULL,
	application_number varchar(255) NULL,
	business_service varchar(255) NULL,
	"action" varchar(255) NULL,
	status varchar(255) NULL,
	"comment" varchar(255) NULL,
	pdf_format varchar(255) NULL,
	elecric_distribution varchar NULL,
	electrical_capacity varchar NULL,
	electrical_infra varchar NULL,
	load_sancation varchar NULL,
	switching_station varchar NULL
	
);

