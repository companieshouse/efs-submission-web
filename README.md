# efs-submission-web
The Emergency Filing Service web application allows users to file forms by uploading electronic documents.

- Provides fields for users to enter data
- Calls endpoints on the `efs-submission-api` as well as other internal services

The service integrates with a number of internal systems. This includes [company-lookup.web.ch.gov.uk](https://github.com/companieshouse/company-lookup.web.ch.gov.uk) and [file-transfer-api](https://github.com/companieshouse/file-transfer-api).

Requirements
------------
* [Git](https://git-scm.com/downloads)
* [Java](https://www.oracle.com/java/technologies/downloads/#java21)
* [Maven](https://maven.apache.org/download.cgi)
* [efs-submission-api](https://github.com/companieshouse/efs-submission-api)
* Internal Companies House core services


## Building and Running Locally

**Note**: As this project has dependencies on internal Companies House libraries, you will need access to private GitHub repositories to build successfully. To run the service locally, you will need the CHS developer environment.  

1. From the command line, in the same folder as the Makefile run `make clean build`
1. Configure project environment variables where necessary
1. Start the service in the CHS developer environment
1. Access the web application, running in the CHS developer environment, on the host and port configured in application.properties

## Building the docker image 

    mvn -s settings.xml compile jib:dockerBuild -Dimage=416670754337.dkr.ecr.eu-west-2.amazonaws.com/efs-submission-web:latest

## Running Locally using Docker

1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the README.

1. Enable the `efs` module

1. Run `chs-dev up` and wait for all services to start

### To make local changes

Development mode is available for this service in [Docker CHS Development](https://github.com/companieshouse/docker-chs-development).

    ./bin/chs-dev development enable efs-submission-web

This will clone the efs-submission-web into the repositories folder inside docker-chs-dev folder. Any changes to the code or resources will automatically trigger a rebuild and reluanch.

Configuration
-------------
System properties for efs-submission-web are defined in `application.properties`. 

| Variable                                     | Description | Example                                                                                                                                                           | 
|----------------------------------------------|-------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ACCESSIBILITY_ABILITY_NET_URL                |             | https://mcmw.abilitynet.org.uk/                                                                                                                                   |
| ACCESSIBILITY_DIGITAL_CENTRE                 |             | https://digitalaccessibilitycentre.org/                                                                                                                           |
| ACCESSIBILITY_EQUALITY_SERVICE               |             | https://www.equalityadvisoryservice.com/                                                                                                                          |      
| ACCESSIBILITY_STATEMENT_PAGE_URL             |             | /efs-submission/accessibility-statement                                                                                                                           |  
| ACCESSIBILITY_USER_PANEL_URL                 |             | https://www.gov.uk/government/news/help-improve-companies-house                                                                                                   |  
| ACCESSIBILITY_WEB_GUIDE_URL                  |             | https://www.w3.org/TR/WCAG21/                                                                                                                                     |  
| BANNER_FEEDBACK_URL                          |             | https://www.smartsurvey.co.uk/s/uploadadocument-feedback/                                                                                                         |  
| CACHE_EVICTION_DELAY                         |             |                                                                                                                                                                   |  
| CACHE_LOGGING_LEVEL                          |             |                                                                                                                                                                   |  
| CACHE_TYPE                                   |             |                                                                                                                                                                   |
| COMPANY_NUMBER_PREFIX_BLOCKED                |             | OE                                                                                                                                                                |
| COMPANY_REGISTRATION_FILING_FORMS            |             | https://www.gov.uk/topic/company-registration-filing/forms                                                                                                        |  
| CONFIRMATION_FEEDBACK_URL                    |             | https://www.smartsurvey.co.uk/s/uploadadocument-confirmation/                                                                                                     |  
| EFS_CONTACT_US_PAGE_URL                      |             | /efs-submission/contact-us                                                                                                                                        |  
| EFS_PIWIK_START_GOAL_ID                      |             | 3                                                                                                                                                                 |  
| EFS_REGISTRATIONS_ENABLED                    |             | false                                                                                                                                                             |  
| EMPATHY_LAB_URL                              |             | https://gds.blog.gov.uk/2018/06/20/creating-the-uk-governments-accessibility-empathy-lab/                                                                         |  
| ENQUIRIES_MAILBOX                            |             | mailto:${ENQUIRY_EMAIL}                                                                                                                                           |
| FILE_OVERSEAS_ENTITY_URL                     |             | https://www.gov.uk/guidance/file-an-overseas-entity-update-statement                                                                                              |
| FILE_UPLOAD_MAX_FILE_SIZE                    |             |                                                                                                                                                                   |  
| GOVUK_CH_URL                                 |             | https://www.gov.uk/government/organisations/companies-house                                                                                                       |  
| GUIDANCE_PAGE_URL                            |             | /efs-submission/guidance                                                                                                                                          |  
| INSOLVENCY_GUIDANCE_PAGE_URL                 |             | /efs-submission/insolvency-guidance                                                                                                                               |  
| LOGGING_LEVEL                                |             |                                                                                                                                                                   |  
| MANAGEMENT_ENDPOINT_HEALTH_ENABLED           |             | true                                                                                                                                                              |  
| MANAGEMENT_ENDPOINTS_ENABLED_BY_DEFAULT      |             | false                                                                                                                                                             |  
| MANAGEMENT_ENDPOINTS_WEB_BASE_PATH           |             | /efs-submission-web                                                                                                                                               |  
| MANAGEMENT_ENDPOINTS_WEB_PATH_MAPPING_HEALTH |             | healthcheck                                                                                                                                                       |  
| PIWIK_SITE_ID                                |             |                                                                                                                                                                   |  
| PIWIK_URL                                    |             |                                                                                                                                                                   |  
| POLICIES_URL                                 |             | http://resources.companieshouse.gov.uk/serviceInformation.shtml                                                                                                   |
| REGISTER_OVERSEAS_ENTITY_URL                 |             | https://www.gov.uk/guidance/register-an-overseas-entity                                                                                                           |
| REGISTRATIONS_SERVICE_URL                    |             | https://www.gov.uk/limited-company-formation/register-your-company                                                                                                |  
| RNG_ALGORITHM_NAME                           |             |                                                                                                                                                                   |  
| RNG_PROVIDER_NAME                            |             |                                                                                                                                                                   |  
| SERVER_ERROR_INCLUDE_STACKTRACE              |             |                                                                                                                                                                   |  
| SERVER_ERROR_WHITELABEL_ENABLED              |             |                                                                                                                                                                   |  
| SERVICE_MANUAL_URL                           |             | https://www.gov.uk/service-manual/helping-people-to-use-your-service/making-your-service-accessible-an-introduction#meeting-government-accessibility-requirements |  
| SERVICE_UNAVAILABLE_PAGE_URL                 |             | /efs-submission/unavailable                                                                                                                                       |  
| SPRING_PROFILES_INCLUDE                      |             |                                                                                                                                                                   |  
| SPRING_SERVLET_MULTIPART_ENABLED             |             |                                                                                                                                                                   |  
| SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE       |             |                                                                                                                                                                   |  
| SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE    |             |                                                                                                                                                                   |  
| START_PAGE_CLOSE_COMPANY                     |             | https://www.gov.uk/topic/company-registration-filing/closing-company                                                                                              |
| START_PAGE_FILE_ACCOUNTS                     |             | https://www.gov.uk/file-your-company-annual-accounts                                                                                                              |  
| START_PAGE_FILE_STATEMENT                    |             | https://www.gov.uk/file-your-confirmation-statement-with-companies-house                                                                                          |  
| START_PAGE_MAKE_CHANGES                      |             | https://www.gov.uk/file-changes-to-a-company-with-companies-house                                                                                                 |  
| START_PAGE_REGISTER_COMPANY                  |             | https://www.gov.uk/topic/company-registration-filing/starting-company                                                                                             |  
| START_PAGE_REGISTRATION_FORM                 |             | https://www.gov.uk/topic/company-registration-filing/forms                                                                                                        |  
| START_PAGE_URL                               |             | /efs-submission/start                                                                                                                                             |  
| USER_RESEARCH_URL                            |             | https://companieshouse.blog.gov.uk/category/user-research/                                                                                                        |  
| WEB_SIGNOUT_REDIRECT_PATH                    |             | /efs-submission/start                                                                                                                                             |  

## Terraform ECS

### What does this code do?

The code present in this repository is used to define and deploy a dockerised container in AWS ECS.
This is done by calling a [module](https://github.com/companieshouse/terraform-modules/tree/main/aws/ecs) from terraform-modules. Application specific attributes are injected and the service is then deployed using Terraform via the CICD platform 'Concourse'.


Application specific attributes | Value                                | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**ECS Cluster**        |filing-maintain                                      | ECS cluster (stack) the service belongs to
**Load balancer**      |{env}-chs-chgovuk                                           | The load balancer that sits in front of the service
**Concourse pipeline**     |[Pipeline link](https://ci-platform.companieshouse.gov.uk/teams/team-development/pipelines/efs-submission-web ) <br> [Pipeline code](https://github.com/companieshouse/ci-pipelines/blob/master/pipelines/ssplatform/team-development/efs-submission-web)                                  | Concourse pipeline link in shared services


### Contributing
- Please refer to the [ECS Development and Infrastructure Documentation](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/4390649858/Copy+of+ECS+Development+and+Infrastructure+Documentation+Updated) for detailed information on the infrastructure being deployed.

### Testing
- Ensure the terraform runner local plan executes without issues. For information on terraform runners please see the [Terraform Runner Quickstart guide](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/1694236886/Terraform+Runner+Quickstart).
- If you encounter any issues or have questions, reach out to the team on the **#platform** slack channel.

### Vault Configuration Updates
- Any secrets required for this service will be stored in Vault. For any updates to the Vault configuration, please consult with the **#platform** team and submit a workflow request.

### Useful Links
- [ECS service config dev repository](https://github.com/companieshouse/ecs-service-configs-dev)
- [ECS service config production repository](https://github.com/companieshouse/ecs-service-configs-production)
