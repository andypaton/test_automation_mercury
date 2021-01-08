@portal @portal_jobs @portal_jobs_start_work
@geolocation
Feature: Portal - Jobs - Start Work
  
  # In Walmart, no confirmation message is displayed. Once Contractor Technicians select start work they are then logged out and need to log back in to stop work.  
  @uswm @usad @smoke1
  Scenario: Contractor Tech - Start Work
    Given a user with profile "Contractor Technician" and has an allocated job
      And is within GEO radius
      And the user logs in
     When the "Start Work" button is clicked
     Then user is logged out of portal
      And the Job is sitting in "In Progress" status
      And the resource assignment status is now "On Site"
      And the Timeline Event Summary has been updated with "On Site"

  @mcp
  Scenario: Contractor Tech - Start Work on second job
    Given a user with profile "Contractor Technician" and has started work on one job and has another allocated job to the same site
      And is within GEO radius
     When the user logs in
     Then the Jobs for Site page is displayed
      And the job In Progress can be stopped
      And the Allocated job can be started

  @ukrb
  Scenario: Contractor Tech - Start Work - Message is displayed
    Given a user with profile "Contractor Technician" and has an allocated job
      And is within GEO radius
      And the user logs in
     When the "Start Work" button is clicked
     Then start work message is displayed

  @ukrb @smoke1
  Scenario: Contractor Tech - Start Work - Cancel 
    Given a user with profile "Contractor Technician" and has an allocated job
      And is within GEO radius
      And the user logs in
      And the "Start Work" button is clicked
     When the "Cancel" button is clicked
     Then the user is returned to Jobs for site table
     
  @ukrb
  Scenario: Contractor Tech - Start Work - Confirm 
    Given a user with profile "Contractor Technician" and has an allocated job
      And is within GEO radius
      And the user logs in
      And the "Start Work" button is clicked
     When the "Confirm" button is clicked
     Then user is logged out of portal
      And the Job is sitting in "In Progress" status
      And the resource assignment status is now "On Site"
      And the Timeline Event Summary has been updated with "On Site"
     
  @mcp
  Scenario: Contractor within 750m GEO radius of multiple sites
    Given a Contractor Technician within a 750m GEO radius of multiple sites
      And with open jobs on each site
      And is within GEO radius
     When the user logs in
     Then the open jobs can be started on multiple sites
      And the "Jobs for Site" table displays expected headers
