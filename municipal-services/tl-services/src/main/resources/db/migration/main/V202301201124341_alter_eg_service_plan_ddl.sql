ALTER TABLE public.eg_service_plan ALTER COLUMN self_certified_drawings_from_chareted_eng DROP NOT NULL;

ALTER TABLE public.eg_service_plan ALTER COLUMN undertaking DROP NOT NULL;

ALTER TABLE public.eg_service_plan ADD additionaldetails jsonb NULL;

ALTER TABLE public.eg_service_plan ADD devName varchar(255) NULL;

ALTER TABLE public.eg_service_plan ADD developmentPlan varchar(255) NULL;

ALTER TABLE public.eg_service_plan ADD purpose varchar(255) NULL;

ALTER TABLE public.eg_service_plan ADD totalArea varchar(255) NULL;