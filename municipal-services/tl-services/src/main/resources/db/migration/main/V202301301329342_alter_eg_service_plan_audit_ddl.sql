
ALTER TABLE public.eg_service_plan_audit  ADD additionaldetails jsonb NULL;

ALTER TABLE public.eg_service_plan_audit ALTER COLUMN self_certified_drawings_from_chareted_eng DROP NOT NULL;

ALTER TABLE public.eg_service_plan_audit ALTER COLUMN undertaking DROP NOT NULL;

ALTER TABLE public.eg_service_plan_audit ADD devName varchar(255) NULL;

ALTER TABLE public.eg_service_plan_audit ADD developmentPlan varchar(255) NULL;

ALTER TABLE public.eg_service_plan_audit ADD purpose varchar(255) NULL;

ALTER TABLE public.eg_service_plan_audit ADD totalArea varchar(255) NULL;