CREATE TABLE IF NOT EXISTS  public.eg_service_plan (
	loi_number varchar(255) NOT NULL,
	auto_cad_file varchar(255) NULL,
	certifiead_copy_of_the_plan varchar(255) NULL,
	environmental_clearance varchar(255) NULL,
	self_certified_drawing_from_empaneled_doc varchar(255) NULL,
	self_certified_drawings_from_chareted_eng varchar(255) NOT NULL,
	shape_file_as_per_template varchar(255) NULL,
	undertaking varchar(255) NOT NULL,
	"action" varchar(255) NULL,
	assignee varchar(255) NULL,
	status varchar(255) NULL,
	business_service varchar(255) NULL,
	"comment" varchar(255) NULL,
	tenantid varchar(255) NULL,
	application_number varchar(255) NULL,
	created_by varchar(255) NULL,
	created_time varchar(255) NULL,
	last_modified_by varchar(255) NULL,
	last_modified_time varchar(255) NULL,
	sp_action varchar(255) NULL,
	CONSTRAINT eg_service_plan_pkey PRIMARY KEY (loi_number)
);



CREATE SEQUENCE IF NOT EXISTS public.seq_eg_ep_apl
	INCREMENT BY 1
	NO MINVALUE
    NO MAXVALUE
	START 1
	CACHE 1
	NO CYCLE;