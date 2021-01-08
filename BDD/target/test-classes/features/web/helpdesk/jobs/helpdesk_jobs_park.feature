@helpdesk @helpdesk_jobs @helpdesk_jobs_park
Feature: Helpdesk - Jobs - Park

  #Commenting out step until bug MCP-13605 is resolved
  #Business Team have agreed they are happy that this issue exists in live
  @uswm @ukrb @usah
  @bugRainbow @bugWalmart
  Scenario: Parked Job - Parking a job - auto assigned resource [bug: MCP-13605, MCP-20552]
    Given a "Helpdesk Operator" has logged in
      And a search is run for a job that is not of type "Landlord" with "P1" fault priority and is assigned to a resource
     When the assigned resource is removed from manage resources section
      And the advise removal is confirmed
      And the additional resource section is closed
      And the details for why no additional resource is required is entered
      And user selects park job action with reason and date to unpark
     Then the client status is now "Parked"
      And the timeline displays a "Job Parked" event with "Parked until, Reason"
#      And the timeline displays a "Additional Resource Not Required" event

  #Commenting out step until bug MCP-13605 is resolved
  #Business Team have agreed they are happy that this issue exists in live
  @usad
  @bugRainbow @bugWalmart
  Scenario: Parked Job - Parking a job - auto assigned resource [bug: MCP-13605, MCP-20552]
    Given a "Helpdesk Manager" has logged in
      And a search is run for a job that is not of type "Landlord" with "P1" fault priority and is assigned to a resource
     When the assigned resource is removed from manage resources section
      And the advise removal is confirmed
      And the additional resource section is closed
      And the details for why no additional resource is required is entered
      And user selects park job action with reason and date to unpark
     Then the client status is now "Parked"
      And the timeline displays a "Job Parked" event with "Parked until, Reason"
#      And the timeline displays a "Additional Resource Not Required" event
  
  @uswm @ukrb @usah
  Scenario: Parked Job - Unparking a job
    Given a "Helpdesk Operator" has logged in
      And a search is run for a parked job that is not of type "Landlord" with "P1" fault priority
     When the "UnPark Job" action is selected
     Then the client status is now "Logged"
      And the job is now sitting in the "Awaiting Assignment" monitor
      And the timeline displays a "Job Un-Parked" event
      
  @usad @bugAdvoate
  Scenario: Parked Job - Unparking a job [bug: MCP-21297]
    Given a "Helpdesk Manager" has logged in
      And a search is run for a parked job that is not of type "Landlord" with "P1" fault priority
     When the "UnPark Job" action is selected
     Then the client status is now "Logged"
      And the job is now sitting in the "Awaiting Assignment" monitor
      And the timeline displays a "Job Un-Parked" event
  
  #Commenting out step until bug MCP-20552 is resolved
  @bugWalmart @bugAdvocate
  @smoke @uswm @ukrb @usah
  Scenario: Parked Job - Parking a job - No resource assigned [bug: MCP-20552, MCP-20605]
    Given a "Helpdesk Operator" has logged in
      And a search is run for a job that is not of type "Landlord" with "P1" fault priority and is not assigned to a resource
     When the job is parked
     Then the client status is now "Parked"
      And the job is now sitting in the "Parked Jobs" monitor
     # And the timeline displays a "Job Parked" event with "Parked until, Reason"
     
  #Commenting out step until bug MCP-20552 is resolved
  @bugWalmart
  @smoke @usad
  Scenario: Parked Job - Parking a job - No resource assigned [bug: MCP-20552]
    Given a "Helpdesk Manager" has logged in
      And a search is run for a job that is not of type "Landlord" with "P1" fault priority and is not assigned to a resource
     When the job is parked
     Then the client status is now "Parked"
      And the job is now sitting in the "Parked Jobs" monitor
     # And the timeline displays a "Job Parked" event with "Parked until, Reason"
  
  @uswm @ukrb @usah
  Scenario: Parked Job - Parking a job - No resource assigned - Unpark Date and Time should prepopulate for 1hr time
    Given a "Helpdesk Operator" has logged in
      And a search is run for a job that is not of type "Landlord" with "P1" fault priority and is not assigned to a resource
     When the "Park Job" action is selected
     Then Unpark Date and Time should prepopulate for one hour time
     
  @usad
  Scenario: Parked Job - Parking a job - No resource assigned - Unpark Date and Time should prepopulate for 1hr time
    Given a "Helpdesk Manager" has logged in
      And a search is run for a job that is not of type "Landlord" with "P1" fault priority and is not assigned to a resource
     When the "Park Job" action is selected
     Then Unpark Date and Time should prepopulate for one hour time
 