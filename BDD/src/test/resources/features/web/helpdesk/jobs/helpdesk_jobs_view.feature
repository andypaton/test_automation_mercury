@helpdesk @helpdesk_jobs @helpdesk_jobs_view
@mcp
Feature: Helpdesk - Jobs - View 
     
  Scenario Outline: job timeline displays all gas questions and answers for a "<timelineEvent>" event
    Given a "Helpdesk Operator" has logged in
     When a search is run for a job with a "2019" FGAS regulated site visit and "<timelineEvent>"
     Then the timeline displays a "<timelineEvent>" event with all questions asked and answers
    Examples:
      | timelineEvent           |
      | Complete                |
      | Resource returning      |
      | Resource Awaiting Parts |

  Scenario: job timeline displays all gas questions and answers updated by a Gas Refrigeration Administrator
    Given a "Helpdesk Operator" has logged in
     When a search is run for a job that has been edited by a Gas Refrigeration Administrator
     Then the timeline displays a "Gas details updated" event with all questions asked and answers
