@portal @portal_ppm @portal_ppm_view
@mcp @wip
Feature: Portal - PPM - View a Job

#Right now PPM jobs can be created only through desktop. Development work to move the PPM job functionality to portal is in progress. 
#PPMs can be reworked upon after they are available on portal. Marking these as wip until then.

  #@MTA-101-view-ppm @MCP-9570
  Scenario Outline: Verify a "<JOB>" with City Resource profile
    Given a portal user with a "City Resource" profile and with "<JOB>" Jobs
      And the user logs in
      And the user is viewing the open jobs grid
     When a PPM job is searched for and opened
     Then the PPM job is visible
      And the question 'Do you want to raise a PPM remedial Job?' <DISPLAYED> displayed
    Examples:
      | JOB                         | DISPLAYED |
      | PPM Job without certificate | is        |
      | PPM Job with certificate    | is not    |