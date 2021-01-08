@portal @portal_ppm @portal_ppm_update
@mcp @wip
Feature: Portal - PPM - Update a Job

#Right now PPM jobs can be created only through desktop. Development work to move the PPM job functionality to portal is in progress. 
#PPMs can be reworked upon after they are available on portal. Marking these as wip until then.

  #@MTA-123
  Scenario: Update a ppm job with "City Resource" profile to a completed status
    Given a portal user with a "City Resource" profile and with "PPM Job" Jobs
      And the user logs in
      And the user is viewing the open jobs grid
     When a PPM job is searched for and opened
      And the PPM Details form is updated with a "complete" Status
      And the "Save" button is clicked
     Then the "The job update has been saved" message will be displayed
      And the View Open Jobs link will be displayed
      And the View All Jobs link will be displayed
      And the Job is updated with a "Complete" status

  #@MTA-123
  Scenario: Update a ppm job with "City Resource" profile to an Awaiting Parts status
    Given a portal user with a "City Resource" profile and with "PPM Job" Jobs
      And the user logs in
      And the user is viewing the open jobs grid
     When a PPM job is searched for and opened
      And the PPM Details form is updated with a "Awaiting Parts" Status
      And an ETA is selected
      And the "Save" button is clicked
     Then the "Parts Request" form will be displayed

  #@MTA-123
  Scenario: Update a ppm job with "City Resource" profile to an Awaiting Parts status with a part in the list
    Given a portal user with a "City Resource" profile and with "PPM Job" Jobs
      And the user logs in
      And the user is viewing the open jobs grid
      And a PPM job is searched for and opened
     When the PPM Details form is updated with "Awaiting Parts" Status and saved
      And the parts request form is completed with a part, in list, priced between <0.01> and <250.00>
      And the Add to Request List is clicked
      And the "Save" button is clicked
     Then the portal log a job form is displayed
      And the Job is updated with a "Active" status
      And the Job is updated with a "Awaiting Parts" resource status

  #@MTA-123
  Scenario: Update a ppm job with "City Resource" profile to an Awaiting Parts status with a part not in the list
    Given a portal user with a "City Resource" profile and with "PPM Job" Jobs
      And the user logs in
      And the user is viewing the open jobs grid
      And a PPM job is searched for and opened
     When the PPM Details form is updated with "Awaiting Parts" Status and saved
      And the user has a new Part
      And the parts request form is completed with a part, not in list, priced between <0.01> and <250.00>
      And the Add to Request List is clicked
     Then the portal log a job form is displayed
      And the Job is updated with a "Active" status
      And the Job is updated with a "Awaiting Parts" resource status

  #@MTA-123
  Scenario: Update a ppm job with "City Resource" profile to a Returning status
    Given a portal user with a "City Resource" profile and with "PPM Job" Jobs
      And the user logs in
      And the user is viewing the open jobs grid
     When a PPM job is searched for and opened
      And the PPM Details form is updated with a "Returning" Status
      And an ETA is selected
      And the "Save" button is clicked
     Then the "The job update has been saved" message will be displayed
      And the View Open Jobs link will be displayed
      And the View All Jobs link will be displayed
      And the Job is updated with a "Active" status
      And the Job is updated with a "Returning" resource status
