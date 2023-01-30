
ALTER TABLE public.eg_service_plan_audit  ADD additionaldetails jsonb NULL;

ALTER TABLE public.eg_service_plan_audit ALTER COLUMN self_certified_drawings_from_chareted_eng DROP NOT NULL;

ALTER TABLE public.eg_service_plan_audit ALTER COLUMN undertaking DROP NOT NULL;