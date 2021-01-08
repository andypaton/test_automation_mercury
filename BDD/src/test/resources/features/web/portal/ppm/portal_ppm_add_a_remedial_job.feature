@portal @portal_ppm @portal_ppm_add
@mcp @wip
Feature: Portal - PPM - Add a remedial Job

#Right now PPM jobs can be created only through desktop. Development work to move the PPM job functionality to portal is in progress. 
#PPMs can be reworked upon after they are available on portal. Marking these as wip until then.

  @bugWalmart
  Scenario: Add a remedial job to a ppm job with a "Returning" status and assigned to me [bug: MCP-8272]
    Given a portal user with a "City Resource" profile and with "PPM Job" Jobs
      And the user logs in
      And the user is viewing the open jobs grid
      And a PPM job is searched for and opened
      And the PPM Details form is completed with a "Returning" Status
     When the request a remedial job box is selected
      And assigned to me is clicked
      And the Remedial job form is updated and saved
     Then "A remedial job has been created" message will be displayed
      And a reactive job is created which is linked to the PPM job
      And the reactive job has a "Assigned To Me" assignment status

  @bugWalmart
  Scenario: Add a remedial job to a ppm job with a "Returning" status and not assigned to me [bug: MCP-8272]
    Given a portal user with a "City Resource" profile and with "PPM Job" Jobs
      And the user logs in
      And the user is viewing the open jobs grid
      And a PPM job is searched for and opened
      And the PPM Details form is completed with a "Returning" Status
     When the request a remedial job box is selected
      And not assigned to me is clicked
      And the Remedial job form is updated and saved
     Then "A remedial job has been created" message will be displayed
      And a reactive job is created which is linked to the PPM job
      And the reactive job has a "Not Assigned To Me" assignment status

