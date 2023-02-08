ALTER TABLE public.eg_electric_plan_audit ALTER COLUMN id TYPE varchar(255) ;

ALTER TABLE public.eg_electric_plan_audit ALTER COLUMN id DROP DEFAULT ;

ALTER TABLE public.eg_electric_plan_audit  ADD additionaldetails jsonb NULL;

ALTER TABLE public.eg_electric_plan_audit ADD devName varchar(255) NULL;

ALTER TABLE public.eg_electric_plan_audit ADD developmentPlan varchar(255) NULL;

ALTER TABLE public.eg_electric_plan_audit ADD purpose varchar(255) NULL;

ALTER TABLE public.eg_electric_plan_audit ADD totalArea varchar(255) NULL;