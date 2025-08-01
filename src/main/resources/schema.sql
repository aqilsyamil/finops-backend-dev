-- DROP SCHEMA finops_v2;

CREATE SCHEMA finops_v2 AUTHORIZATION postgres;
-- finops_v2.t_mt_organization definition

-- Drop table

-- DROP TABLE finops_v2.t_mt_organization;

CREATE TABLE finops_v2.t_mt_organization (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	organization_name text NOT NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_mt_organization_pkey PRIMARY KEY (id)
);


-- finops_v2.t_anomaly_alert definition

-- Drop table

-- DROP TABLE finops_v2.t_anomaly_alert;

CREATE TABLE finops_v2.t_anomaly_alert (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	"name" varchar(25) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	organization_id uuid NULL,
	csp_connection_id uuid NULL,
	CONSTRAINT t_anomaly_alert_pkey PRIMARY KEY (id)
);


-- finops_v2.t_anomaly_alert_recipients definition

-- Drop table

-- DROP TABLE finops_v2.t_anomaly_alert_recipients;

CREATE TABLE finops_v2.t_anomaly_alert_recipients (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	alert_id uuid NULL,
	email varchar(255) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_anomaly_alert_recipients_pkey PRIMARY KEY (id)
);


-- finops_v2.t_anomaly_alert_threshold definition

-- Drop table

-- DROP TABLE finops_v2.t_anomaly_alert_threshold;

CREATE TABLE finops_v2.t_anomaly_alert_threshold (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	alert_id uuid NULL,
	threshold_amount_1 float8 NULL,
	threshold_amount_2 float8 NULL,
	threshold_type_1 varchar(255) NULL,
	threshold_type_2 varchar(255) NULL,
	relation varchar(25) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_anomaly_alert_threshold_pkey PRIMARY KEY (id)
);


-- finops_v2.t_anomaly_monitor definition

-- Drop table

-- DROP TABLE finops_v2.t_anomaly_monitor;

CREATE TABLE finops_v2.t_anomaly_monitor (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	"name" varchar(25) NULL,
	csp_connection_id uuid NULL,
	alert_id uuid NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_anomaly_monitor_pkey PRIMARY KEY (id)
);


-- finops_v2.t_billing_table definition

-- Drop table

-- DROP TABLE finops_v2.t_billing_table;

CREATE TABLE finops_v2.t_billing_table (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	"name" text NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	csp_connection_id uuid NULL,
	CONSTRAINT t_billing_table_pkey PRIMARY KEY (id)
);


-- finops_v2.t_budget_alerts_recipients definition

-- Drop table

-- DROP TABLE finops_v2.t_budget_alerts_recipients;

CREATE TABLE finops_v2.t_budget_alerts_recipients (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	budget_id uuid NULL,
	email varchar(255) NOT NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_budget_alerts_recipients_pkey PRIMARY KEY (id)
);


-- finops_v2.t_budget_services_selection definition

-- Drop table

-- DROP TABLE finops_v2.t_budget_services_selection;

CREATE TABLE finops_v2.t_budget_services_selection (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	budget_id uuid NULL,
	service_id uuid NULL,
	selected bool NOT NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_budget_services_selection_pkey PRIMARY KEY (id)
);


-- finops_v2.t_budget_threshold_rules definition

-- Drop table

-- DROP TABLE finops_v2.t_budget_threshold_rules;

CREATE TABLE finops_v2.t_budget_threshold_rules (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	budget_id uuid NULL,
	percentage float8 NULL,
	amount float8 NULL,
	trigger_type varchar(25) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_budget_threshold_rules_pkey PRIMARY KEY (id),
	CONSTRAINT t_budget_threshold_rules_trigger_type_check CHECK (((trigger_type)::text = ANY ((ARRAY['actual cost'::character varying, 'actual spend'::character varying])::text[])))
);


-- finops_v2.t_budgets definition

-- Drop table

-- DROP TABLE finops_v2.t_budgets;

CREATE TABLE finops_v2.t_budgets (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	organization_id uuid NULL,
	csp_connection_id uuid NULL,
	"name" varchar(25) NULL,
	time_range varchar(25) NULL,
	renewal_type varchar(25) NULL,
	start_month date NULL,
	budget_type varchar(25) NULL,
	amount float8 NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_budgets_budget_type_check CHECK (((budget_type)::text = 'fixed'::text)),
	CONSTRAINT t_budgets_pkey PRIMARY KEY (id),
	CONSTRAINT t_budgets_renewal_type_check CHECK (((renewal_type)::text = 'recurring'::text)),
	CONSTRAINT t_budgets_time_range_check CHECK (((time_range)::text = ANY ((ARRAY['monthly'::character varying, 'quarterly'::character varying, 'yearly'::character varying])::text[])))
);


-- finops_v2.t_csp definition

-- Drop table

-- DROP TABLE finops_v2.t_csp;

CREATE TABLE finops_v2.t_csp (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	"name" varchar(25) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_csp_pkey PRIMARY KEY (id)
);


-- finops_v2.t_csp_connection definition

-- Drop table

-- DROP TABLE finops_v2.t_csp_connection;

CREATE TABLE finops_v2.t_csp_connection (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	organization_id uuid NULL,
	csp_id uuid NULL,
	data_source uuid NULL,
	"name" varchar(25) NULL,
	description varchar(2000) NULL,
	plan_type uuid NULL,
	access_key_id varchar(255) NOT NULL,
	secret_key_id varchar(255) NOT NULL,
	region uuid NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_csp_connection_pkey PRIMARY KEY (id)
);


-- finops_v2.t_data_base definition

-- Drop table

-- DROP TABLE finops_v2.t_data_base;

CREATE TABLE finops_v2.t_data_base (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	"name" varchar(50) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	csp_connection_id uuid NULL,
	CONSTRAINT t_data_base_pkey PRIMARY KEY (id)
);


-- finops_v2.t_data_catalog definition

-- Drop table

-- DROP TABLE finops_v2.t_data_catalog;

CREATE TABLE finops_v2.t_data_catalog (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	"name" varchar(50) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	csp_connection_id uuid NULL,
	CONSTRAINT t_data_catalog_pkey PRIMARY KEY (id)
);


-- finops_v2.t_data_source definition

-- Drop table

-- DROP TABLE finops_v2.t_data_source;

CREATE TABLE finops_v2.t_data_source (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	"name" text NULL,
	output_bucket_url varchar(255) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	csp_connection_id uuid NULL,
	CONSTRAINT t_data_source_pkey PRIMARY KEY (id)
);


-- finops_v2.t_environment_type_uploads definition

-- Drop table

-- DROP TABLE finops_v2.t_environment_type_uploads;

CREATE TABLE finops_v2.t_environment_type_uploads (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	file_name text NULL,
	file_url text NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	csp_connection_id uuid NULL,
	CONSTRAINT t_environment_type_uploads_pkey PRIMARY KEY (id)
);


-- finops_v2.t_focus_anomaly_detection definition

-- Drop table

-- DROP TABLE finops_v2.t_focus_anomaly_detection;

CREATE TABLE finops_v2.t_focus_anomaly_detection (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	anomaly_monitor_id uuid NULL,
	start_date date NULL,
	last_detected_date date NULL,
	expected_spend float8 NULL,
	actual_spend float8 NULL,
	linked_account_id varchar(25) NULL,
	linked_account_name varchar(255) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_focus_anomaly_detection_pkey PRIMARY KEY (id)
);


-- finops_v2.t_focus_anomaly_monitors definition

-- Drop table

-- DROP TABLE finops_v2.t_focus_anomaly_monitors;

CREATE TABLE finops_v2.t_focus_anomaly_monitors (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	service_id uuid NULL,
	"name" varchar(25) NULL,
	"type" varchar(255) NULL,
	date_created date NULL,
	date_updated date NULL,
	monitored_dimensions text NULL,
	tags text NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	organization_id uuid NULL,
	CONSTRAINT t_focus_anomaly_monitors_pkey PRIMARY KEY (id)
);


-- finops_v2.t_focus_log definition

-- Drop table

-- DROP TABLE finops_v2.t_focus_log;

CREATE TABLE finops_v2.t_focus_log (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	organization_id uuid NULL,
	csp_connection_id uuid NULL,
	log_date date NOT NULL,
	status bool NOT NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_focus_log_pkey PRIMARY KEY (id)
);


-- finops_v2.t_focus_report definition

-- Drop table

-- DROP TABLE finops_v2.t_focus_report;

CREATE TABLE finops_v2.t_focus_report (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	focus_log_id uuid NULL,
	availabilityzone text NULL,
	billedcost float8 NULL,
	billingaccountid text NULL,
	billingaccountname text NULL,
	billingcurrency text NULL,
	billingperiodend timestamptz NULL,
	billingperiodstart timestamptz NULL,
	chargecategory text NULL,
	chargeclass text NULL,
	chargedescription text NULL,
	chargefrequency text NULL,
	chargeperiodend timestamptz NULL,
	chargeperiodstart timestamptz NULL,
	commitmentdiscountcategory text NULL,
	commitmentdiscountid text NULL,
	commitmentdiscountname text NULL,
	commitmentdiscountstatus text NULL,
	commitmentdiscounttype text NULL,
	consumedquantity float8 NULL,
	consumedunit text NULL,
	contractedcost float8 NULL,
	contractedunitprice float8 NULL,
	effectivecost float8 NULL,
	invoiceissuername text NULL,
	listcost float8 NULL,
	listunitprice float8 NULL,
	pricingcategory text NULL,
	pricingquantity float8 NULL,
	pricingunit text NULL,
	providername text NULL,
	publishername text NULL,
	regionid text NULL,
	regionname text NULL,
	resourceid text NULL,
	resourcename text NULL,
	resourcetype text NULL,
	servicecategory text NULL,
	servicename text NULL,
	skuid text NULL,
	skupriceid text NULL,
	subaccountid text NULL,
	subaccountname text NULL,
	tags jsonb NULL,
	x_costcategories jsonb NULL,
	x_discounts jsonb NULL,
	x_operation text NULL,
	x_servicecode text NULL,
	x_usagetype text NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_focus_usage_pkey PRIMARY KEY (id)
);


-- finops_v2.t_group_connection definition

-- Drop table

-- DROP TABLE finops_v2.t_group_connection;

CREATE TABLE finops_v2.t_group_connection (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	organization_id uuid NULL,
	"name" varchar(25) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_group_connection_pkey PRIMARY KEY (id)
);


-- finops_v2.t_groups_info definition

-- Drop table

-- DROP TABLE finops_v2.t_groups_info;

CREATE TABLE finops_v2.t_groups_info (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	group_connection_id uuid NULL,
	csp_connection_id uuid NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_groups_info_pkey PRIMARY KEY (id)
);


-- finops_v2.t_mt_features definition

-- Drop table

-- DROP TABLE finops_v2.t_mt_features;

CREATE TABLE finops_v2.t_mt_features (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	feature_name varchar(100) NOT NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_mt_features_feature_name_key UNIQUE (feature_name),
	CONSTRAINT t_mt_features_pkey PRIMARY KEY (id)
);


-- finops_v2.t_mt_role_feature_permissions definition

-- Drop table

-- DROP TABLE finops_v2.t_mt_role_feature_permissions;

CREATE TABLE finops_v2.t_mt_role_feature_permissions (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	role_id uuid NULL,
	feature_id uuid NULL,
	can_read bool DEFAULT false NOT NULL,
	can_write bool DEFAULT false NOT NULL,
	can_list bool DEFAULT false NOT NULL,
	can_modify bool DEFAULT false NOT NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_mt_role_feature_permissions_pkey PRIMARY KEY (id)
);


-- finops_v2.t_mt_roles definition

-- Drop table

-- DROP TABLE finops_v2.t_mt_roles;

CREATE TABLE finops_v2.t_mt_roles (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	role_name varchar(100) NOT NULL,
	role_type varchar(50) NOT NULL,
	status varchar(20) NOT NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	deleted_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	CONSTRAINT t_mt_roles_pkey PRIMARY KEY (id),
	CONSTRAINT t_mt_roles_role_type_check CHECK (((role_type)::text = ANY ((ARRAY['system'::character varying, 'custom'::character varying])::text[]))),
	CONSTRAINT t_mt_roles_status_check CHECK (((status)::text = ANY ((ARRAY['active'::character varying, 'not active'::character varying])::text[])))
);


-- finops_v2.t_mt_team_members definition

-- Drop table

-- DROP TABLE finops_v2.t_mt_team_members;

CREATE TABLE finops_v2.t_mt_team_members (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	team_id uuid NULL,
	user_id uuid NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_mt_team_members_pkey PRIMARY KEY (id)
);


-- finops_v2.t_mt_teams definition

-- Drop table

-- DROP TABLE finops_v2.t_mt_teams;

CREATE TABLE finops_v2.t_mt_teams (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	team_name varchar(100) NOT NULL,
	status varchar(20) NOT NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	organization_id uuid NULL,
	CONSTRAINT t_mt_teams_pkey PRIMARY KEY (id),
	CONSTRAINT t_mt_teams_status_check CHECK (((status)::text = ANY ((ARRAY['active'::character varying, 'inactive'::character varying])::text[])))
);


-- finops_v2.t_mt_users definition

-- Drop table

-- DROP TABLE finops_v2.t_mt_users;

CREATE TABLE finops_v2.t_mt_users (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	organization_id uuid NULL,
	username varchar(100) NOT NULL,
	email varchar(255) NOT NULL,
	role_id uuid NULL,
	status varchar(20) NOT NULL,
	last_access_at timestamptz NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	deleted_at timestamptz NULL,
	password_hash varchar(255) NOT NULL,
	CONSTRAINT t_mt_users_email_key UNIQUE (email),
	CONSTRAINT t_mt_users_pkey PRIMARY KEY (id),
	CONSTRAINT t_mt_users_status_check CHECK (((status)::text = ANY ((ARRAY['active'::character varying, 'deactivated'::character varying])::text[])))
);


-- finops_v2.t_plan_type definition

-- Drop table

-- DROP TABLE finops_v2.t_plan_type;

CREATE TABLE finops_v2.t_plan_type (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	"name" varchar(25) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	csp_connection_id uuid NULL,
	CONSTRAINT t_plan_type_pkey PRIMARY KEY (id)
);


-- finops_v2.t_recommendations_report definition

-- Drop table

-- DROP TABLE finops_v2.t_recommendations_report;

CREATE TABLE finops_v2.t_recommendations_report (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	focus_log_id uuid NULL,
	account_id text NULL,
	action_type text NULL,
	currency_code text NULL,
	current_resource_details text NULL,
	current_resource_summary text NULL,
	current_resource_type text NULL,
	estimated_monthly_cost_after_discount float8 NULL,
	estimated_monthly_cost_before_discount float8 NULL,
	estimated_monthly_savings_after_discount float8 NULL,
	estimated_monthly_savings_before_discount float8 NULL,
	estimated_savings_percentage_after_discount float8 NULL,
	estimated_savings_percentage_before_discount float8 NULL,
	implementation_effort text NULL,
	last_refresh_timestamp timestamptz NULL,
	recommendation_id text NULL,
	recommendation_lookback_period_in_days int4 NULL,
	recommendation_source text NULL,
	recommended_resource_details text NULL,
	recommended_resource_summary text NULL,
	recommended_resource_type text NULL,
	region text NULL,
	resource_arn text NULL,
	restart_needed bool NULL,
	rollback_possible bool NULL,
	tags jsonb NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_focus_recommendations_pkey PRIMARY KEY (id)
);


-- finops_v2.t_region definition

-- Drop table

-- DROP TABLE finops_v2.t_region;

CREATE TABLE finops_v2.t_region (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	"name" varchar(25) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	csp_connection_id uuid NULL,
	CONSTRAINT t_region_pkey PRIMARY KEY (id)
);


-- finops_v2.t_service definition

-- Drop table

-- DROP TABLE finops_v2.t_service;

CREATE TABLE finops_v2.t_service (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	partition_name varchar(255) NOT NULL,
	service_name varchar(255) NOT NULL,
	region_name varchar(255) NOT NULL,
	account_id varchar(255) NOT NULL,
	resource_type varchar(255) NOT NULL,
	resource_id varchar(255) NOT NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	csp_connection_id uuid NULL,
	CONSTRAINT t_services_pkey PRIMARY KEY (id)
);


-- finops_v2.t_tag_status definition

-- Drop table

-- DROP TABLE finops_v2.t_tag_status;

CREATE TABLE finops_v2.t_tag_status (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	tags_id uuid NOT NULL,
	"name" varchar(25) NULL,
	value varchar(255) NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_tag_status_pkey PRIMARY KEY (id)
);


-- finops_v2.t_tags definition

-- Drop table

-- DROP TABLE finops_v2.t_tags;

CREATE TABLE finops_v2.t_tags (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	csp_connection_id uuid NOT NULL,
	service_id uuid NULL,
	created_at timestamptz DEFAULT now() NULL,
	updated_at timestamptz NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	deleted_at timestamptz NULL,
	CONSTRAINT t_tags_pkey PRIMARY KEY (id)
);


