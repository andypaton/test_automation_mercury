@portal @portal_ppm @portal_ppm_request
@mcp @wip
Feature: Portal - PPM - Request a quote

#Right now PPM jobs can be created only through desktop. Development work to move the PPM job functionality to portal is in progress. 
#PPMs can be reworked upon after they are available on portal. Marking these as wip until then.

  #@MTA-142
  Scenario Outline: Add an "<quoteType>" quote to a ppm job with a "Returning" status
   Given a portal user with a "<resourceType>" profile and with "PPM Job" Jobs
     And the user logs in
     And the user is viewing the open jobs grid
     And a PPM job is searched for and opened
     And the PPM Details form is completed with a "Returning" Status
    When the request a quote button is clicked
     And a random quote is created for a "<quoteType>" quote type and saved
    Then "A quote job has been created" message will be displayed
     And a reactive job is created which is linked to the PPM job
     And the reactive job created is a "<quoteType>" job
    Examples:
      |resourceType   | quoteType  |
      | City Resource | OPEX       |
      | City Resource | CAPEX      |