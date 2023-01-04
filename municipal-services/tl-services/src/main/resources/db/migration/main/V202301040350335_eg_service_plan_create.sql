 CREATE TABLE IF NOT EXISTS  public.eg_service_plan (
	loi_number varchar(255) NOT NULL,
	auto_cad_file varchar(255) NULL,
	certifiead_copy_of_the_plan varchar(255) NULL,
	environmental_clearance varchar(255) NULL,
	self_certified_drawing_from_empaneled_doc varchar(255) NULL,
	self_certified_drawings_from_chareted_eng bool NOT NULL,
	shape_file_as_per_template varchar(255) NULL,
	status varchar(255) NULL,
	sp_action varchar(255) NULL,
	undertaking bool NOT NULL,
	assignee varchar(255) NULL,
	action varchar(255) NULL,
	business_service varchar(255) NULL,
	comment varchar(255) NULL,
	tenantid varchar(255) NULL,
	application_number varchar(255) NULL,
	created_by varchar(64) NULL,
	created_time varchar(64) NULL,
	last_modified_by varchar(64) NULL,
	last_modified_time varchar(64) NULL,
	CONSTRAINT eg_service_plan_pkey PRIMARY KEY (loi_number)
);


CREATE SEQUENCE IF NOT EXISTS public.seq_eg_sp_apl
	START WITH 1
	INCREMENT BY 1
	NO MINVALUE
    NO MAXVALUE
    CACHE 1;
	