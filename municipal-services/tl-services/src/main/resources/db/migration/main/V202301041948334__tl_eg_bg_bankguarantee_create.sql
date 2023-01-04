 CREATE TABLE IF NOT EXISTS public.eg_bg_new_bank_guarantee(
    id character varying(64) NOT NULL,
    application_number character varying(32) NOT NULL,
    status character varying(100) NOT NULL,
    loi_number character varying(100) NOT NULL,
    memo_number character varying(100),
    type_of_bg character varying(100),
    upload_bg character varying(100),
    bank_name character varying(100),
    amount_In_Fig character varying(100),
    amount_in_words character varying(200),
    consent_Letter character varying(200),
    license_Applied character varying(200),
    validity Date,
    tenantId character varying(16),
    additionalDetails JSONB,
    
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
    
    CONSTRAINT eg_land_bankGuarantee_uk UNIQUE (id),
    CONSTRAINT eg_land_bankGuarantee_pk PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS public.eg_bg_new_bank_guarantee_auditdetails(
    id character varying(64) NOT NULL,
    application_number character varying(32) NOT NULL,
    status character varying(100) NOT NULL,
    loi_number character varying(100) NOT NULL,
    memo_number character varying(100),
    type_of_bg character varying(100),
    upload_bg character varying(100),
    bank_name character varying(100),
    amount_In_Fig character varying(100),
    amount_in_words character varying(200),
    consent_Letter character varying(200),
    license_Applied character varying(200),
    validity Date,
    tenantId character varying(16),
    additionalDetails JSONB,
    
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint
);

CREATE INDEX IF NOT EXISTS eg_bg_new_bank_guarantee_index  ON eg_bg_new_bank_guarantee 
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