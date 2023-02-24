CREATE TABLE IF NOT EXISTS public.eg_tl_bank_guarantee(
    id character varying(64) NOT NULL,
    application_number character varying(32) NOT NULL,
    status character varying(100),
    loi_number character varying(100) NOT NULL,
    bg_number character varying(100),
    type_of_bg character varying(100),
    upload_bg character varying(100),
    bank_name character varying(100),
    amount_In_Fig numeric(20,2),
    amount_in_words character varying(200),
    license_Applied character varying(200),
    validity Date,
    bank_Guarantee_Status character varying(64),
    licence_Number character varying(200),
    hardcopy_Submitted boolean,
    full_Certificate character varying(200),
    partial_Certificate character varying(200),
    additional_Documents JSONB,
    tenantId character varying(16),
    additionalDetails JSONB,
    hardcopy_Submitted_Document character varying(64),
    existing_Bg_Number character varying(200),
    claim_Period integer,
    origin_Country character varying(200),
    tcp_submission_received character varying(200),
    indian_bank_advised_certificate character varying(200),
    release_bank_guarantee character varying(200),
    businessservice character varying(32),
    
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
    
    CONSTRAINT eg_bg_new_bankGuarantee_uk UNIQUE (id),
    CONSTRAINT eg_bg_new_bankGuarantee_pk PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS public.eg_tl_bank_guarantee_auditdetails(
    id character varying(64) NOT NULL,
    application_number character varying(32) NOT NULL,
    status character varying(100),
    loi_number character varying(100) NOT NULL,
    bg_number character varying(100),
    type_of_bg character varying(100),
    upload_bg character varying(100),
    bank_name character varying(100),
    amount_In_Fig numeric(20,2),
    amount_in_words character varying(200),
    license_Applied character varying(200),
    validity Date,
    bank_Guarantee_Status character varying(64),
    licence_Number character varying(200),
    hardcopy_Submitted boolean,
    full_Certificate character varying(200),
    partial_Certificate character varying(200),
    additional_Documents JSONB,
    tenantId character varying(16),
    additionalDetails JSONB,
    hardcopy_Submitted_Document character varying(64),
    existing_Bg_Number character varying(200),
    claim_Period integer,
    origin_Country character varying(200),
    tcp_submission_received character varying(200),
    indian_bank_advised_certificate character varying(200),
    release_bank_guarantee character varying(200),
    businessservice character varying(32),
    
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint
);

CREATE INDEX IF NOT EXISTS eg_bg_new_bank_guarantee_index  ON eg_tl_bank_guarantee 
(
    tenantId,
    loi_number,
    bank_name,
    status,
    type_of_bg
);

CREATE SEQUENCE IF NOT EXISTS seq_eg_bg_new_bank_guarantee
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;