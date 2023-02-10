ALTER TABLE public.eg_electric_plan ALTER COLUMN id TYPE varchar(255) ;

ALTER TABLE public.eg_electric_plan ALTER COLUMN id DROP DEFAULT ;

ALTER TABLE public.eg_electric_plan  ADD additionaldetails jsonb NULL;

ALTER TABLE public.eg_electric_plan ADD devName varchar(255) NULL;

ALTER TABLE public.eg_electric_plan ADD developmentPlan varchar(255) NULL;

ALTER TABLE public.eg_electric_plan ADD purpose varchar(255) NULL;

ALTER TABLE public.eg_electric_plan ADD totalArea varchar(255) NULL;