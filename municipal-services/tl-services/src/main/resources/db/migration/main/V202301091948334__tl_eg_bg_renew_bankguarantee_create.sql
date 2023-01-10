CREATE TABLE IF NOT EXISTS public.eg_bg_renew_bank_guarantee(
    id character varying(64) NOT NULL,
    application_number character varying(32) NOT NULL,
    status character varying(100) NOT NULL,
    licence_number character varying(200),
    amount_In_Fig character varying(100),
    amount_in_words character varying(200),
    extended_Time character varying(200),
    bank_name character varying(200),
    memo_number character varying(200),
    hard_copy_Submitted character varying(200),
    upload_bg character varying(100),
    validity Date,
    consent_Letter character varying(200),
    tenantId character varying(16),
    additionalDetails JSONB,
    
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
    
    CONSTRAINT eg_bg_renew_bankGuarantee_uk UNIQUE (id),
    CONSTRAINT eg_bg_renew_bankGuarantee_pk PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS public.eg_bg_renew_bank_guarantee_auditdetails(
    id character varying(64) NOT NULL,
    application_number character varying(32) NOT NULL,
    status character varying(100) NOT NULL,
    licence_number character varying(200),
    amount_In_Fig character varying(100),
    amount_in_words character varying(200),
    extended_Time character varying(200),
    bank_name character varying(200),
    memo_number character varying(200),
    hard_copy_Submitted character varying(200),
    upload_bg character varying(100),
    validity Date,
    consent_Letter character varying(200),
    tenantId character varying(16),
    additionalDetails JSONB,
    
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint
);

CREATE INDEX IF NOT EXISTS eg_bg_renew_bank_guarantee_index  ON eg_bg_renew_bank_guarantee 
(
    tenantId,
    licence_number,
    bank_name,
    status
);

CREATE SEQUENCE IF NOT EXISTS seq_eg_bg_renew_bank_guarantee
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;