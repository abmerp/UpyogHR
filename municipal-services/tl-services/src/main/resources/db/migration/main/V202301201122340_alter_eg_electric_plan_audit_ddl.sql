ALTER TABLE public.eg_electric_plan_audit ALTER COLUMN id TYPE varchar(255) ;

ALTER TABLE public.eg_electric_plan_audit ALTER COLUMN id DROP DEFAULT ;

ALTER TABLE public.eg_electric_plan_audit  ADD additionaldetails jsonb NULL;