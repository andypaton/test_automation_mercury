@helpdesk @helpdesk_jobs @helpdesk_jobs_chase
Feature: Helpdesk - Jobs - Chase

  @mcp
  Scenario: Chase panel is displayed as expected
    Given a "Helpdesk Operator" has logged in
      And a job with an active "ETA Chase" chase and "Helpdesk / Only 1 Previous Chase" creation route
     When the "Chase" action is selected
     Then chase panel is displayed correctly
  
  @mcp
  Scenario: Update Chase panel is displayed as expected
    Given a "Helpdesk Operator" has logged in
      And a job with an active "ETA Chase" chase and "Helpdesk / With Chase Update" creation route
      And the Chase panel is displayed
     When the view/update button is selected
     Then view/update panel is displayed correctly

  @uswm @ukrb @usah
  Scenario: Add an ETA Chase to job
    Given a "Helpdesk Operator" has logged in
      And a "Logged" job is created and searched for
      And the Chase panel is displayed
     When an "ETA Chase" chase is created for the job
     Then An "Active" chase of type "ETA Chase" is displayed
      And the Job "is" added to the "Chase" monitor
     
  @uswm @ukrb @usah
  Scenario: Add an ETA Chase to job - invalid telephone number
    Given a "Helpdesk Operator" has logged in
      And a "Logged" job is created and searched for
      And the Chase panel is displayed   
      And the "New Chase" button is clicked
     When the "ETA Chase" chase details are entered
      And an invalid telephone number is entered
     Then an error for invalid phone number format is displayed
      And the "Create Chase" button is disabled
      
  @uswm @ukrb @usah
  Scenario Outline: Add an ETA Chase to a "<status>" job
    Given a "Helpdesk Operator" has logged in
      And a "<status>" job is created and searched for
      And the Chase panel is displayed   
     When an "ETA Chase" chase is created for the job 		
     Then An "Active" chase of type "ETA Chase" is displayed
      And the job status is still "<status>"    
      And the Job "is" added to the "Chase" monitor
    Examples:
    	| status	|
    	| Fixed		|
    	| Cancelled	|     

  @uswm @ukrb @usah
  Scenario: Update an unresolved ETA chase
    Given a "Helpdesk Operator" has logged in
      And a job with an active "ETA Chase" chase and "Helpdesk" creation route
      And the Chase panel is displayed   
     When an update is added to an unresolved chase 		
     Then the Actions button number flag count has not changed
      And the chase notes show the "update"
      And the timeline displays a "Update - ETA Chase" event
      
  @uswm @ukrb @usah
  Scenario Outline: Update "<chaseType>" to resolved
    Given a "Helpdesk Operator" has logged in
      And a job with an active "<chaseType>" chase and "<creationRoute>" creation route
      And the Chase panel is displayed   
     When the chase is updated to resolved 				
     Then the Actions button number flag will be reduced or cleared
      And the chase notes show the "resolvedChase"
      And the timeline displays a "<chaseEventType>" event 
      Examples:
      	| creationRoute | chaseType     | chaseEventType      |
      	| Helpdesk      | ETA Chase	    | Chase ETA Chase     |
      	| nonHelpdesk   | Manager Chase | Chase Manager Chase |
      	
  @uswm @ukrb @usah
  Scenario: Cancel a chase
    Given a "Helpdesk Operator" has logged in
      And a job with an active "ETA Chase" chase and "Helpdesk" creation route
      And the Chase panel is displayed   
     When the chase is updated to cancelled 				
     Then the Actions button number flag will be reduced or cleared
      And the chase notes show the "cancellation"
      And the timeline displays a "Chase ETA Chase" event
      
  @uswm @ukrb @usah
  Scenario: Helpdesk - Add another chase to a job that already has a chase
    Given a "Helpdesk Operator" has logged in
      And a job with an active "ETA Chase" chase and "Helpdesk" creation route
      And the Chase panel is displayed   
     When an "ETA Chase" chase is created for the job 		
     Then An "Active" chase of type "ETA Chase" is displayed
      And the timeline displays a "Chase ETA Chase" event
      And the Actions button number flag is increased
     
  @mcp
  Scenario: Helpdesk - Verify mandatory fields when creating a chase
    Given a "Helpdesk Operator" has logged in
      And a "reactive" job with status "Logged"
      And a search is run for the job reference
      And the Chase panel is displayed
     When a chase is created with no details entered
     Then mandatory field error messages are displayed 