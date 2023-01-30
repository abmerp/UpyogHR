ALTER TABLE public.eg_electric_plan ALTER COLUMN id TYPE varchar(255) ;

ALTER TABLE public.eg_electric_plan ALTER COLUMN id DROP DEFAULT ;

ALTER TABLE public.eg_electric_plan  ADD additionaldetails jsonb NULL;