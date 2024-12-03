provider "aws" {
  region = var.aws_region
}

terraform {
  backend "s3" {
  }
  required_version = "~> 1.3"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.54.0"
    }
    vault = {
      source  = "hashicorp/vault"
      version = "~> 3.18.0"
    }
  }
}

module "secrets" {
  source = "git@github.com:companieshouse/terraform-modules//aws/ecs/secrets?ref=1.0.296"

  environment = var.environment
  kms_key_id  = data.aws_kms_key.kms_key.id
  name_prefix = "${local.service_name}-${var.environment}"
  secrets     = nonsensitive(local.service_secrets)
}

module "ecs-service" {
  source                    = "git@github.com:companieshouse/terraform-modules//aws/ecs/ecs-service?ref=1.0.296"

  # Environmental configuration
  aws_profile             = var.aws_profile
  aws_region              = var.aws_region
  ecs_cluster_id          = data.aws_ecs_cluster.ecs_cluster.id
  environment             = var.environment
  task_execution_role_arn = data.aws_iam_role.ecs_cluster_iam_role.arn
  vpc_id                  = data.aws_vpc.vpc.id

  # Load balancer configuration
  lb_listener_arn                   = data.aws_lb_listener.service_lb_listener.arn
  lb_listener_rule_priority         = local.lb_listener_rule_priority
  lb_listener_paths                 = local.lb_listener_paths
  multilb_setup                     = false


  # ECS Task container health check
  health_check_grace_period_seconds = 360
  healthcheck_healthy_threshold     = "2"
  healthcheck_matcher            = local.healthcheck_matcher
  healthcheck_path               = local.healthcheck_path
  use_task_container_healthcheck = true


  # Docker container details
  container_port    = local.container_port
  container_version = var.efs_submission_web_version
  docker_registry   = var.docker_registry
  docker_repo       = local.docker_repo



  # Service configuration
  desired_task_count                 = var.desired_task_count
  fargate_subnets                    = local.application_subnet_ids
  max_task_count                     = var.max_task_count
  min_task_count                     = var.min_task_count
  name_prefix                        = local.name_prefix
  required_cpus                      = var.required_cpus
  required_memory                    = var.required_memory
  service_autoscale_enabled          = var.service_autoscale_enabled
  service_autoscale_target_value_cpu = var.service_autoscale_target_value_cpu
  service_name                       = local.service_name
  service_scaledown_schedule         = var.service_scaledown_schedule
  service_scaleup_schedule           = var.service_scaleup_schedule
  use_capacity_provider              = var.use_capacity_provider
  use_fargate                        = var.use_fargate


  # Cloudwatch
  cloudwatch_alarms_enabled = var.cloudwatch_alarms_enabled

  # Service environment variable and secret configs
  app_environment_filename  = local.app_environment_filename
  task_environment          = local.task_environment
  task_secrets              = local.task_secrets
  use_set_environment_files = local.use_set_environment_files

}
