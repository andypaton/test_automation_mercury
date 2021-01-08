#@MCP-1203
@portal @portal_jobs
@wip @deprecated
@mcp
Feature: Portal - Jobs - Update with status Awaiting Parts
  
  #@MTA-269 MCP-11905
  @bug
  Scenario Outline: Update job with parts not in list and unit price below 250 and "<priority>" priority and deliver to Direct To Store
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed
      And the user has a new Part
     When the user selects Part Not In List
      And the user selects a random Supplier
      And the user enters a new Part Number
      And the user enters a new Part Description
      And the user enters a new Manufacturer Ref
      And the user enters a new Model
      And the user enters a new Serial Number
      And the user enters a new Unit Price less than the auto complete value
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
      And the Add to Request List is clicked
      And the Save Request is clicked
     Then the Parts Request has been recorded in the database
      And the Resource Assignment table has been updated with the status "Awaiting Parts Review"
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress | status      |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       | In Progress |
      | City Resource    | reactive | Next Day | Direct To Store | Home Store      | In Progress |

  #@MTA-904 MCP-11905
  @bug
  Scenario Outline: Update job with parts not in list and unit price below 250 and "<priority>" priority and deliver to Trade Counter Collection
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed
      And the user has a new Part
     When the user selects Part Not In List
      And the user selects a random Supplier
      And the user enters a new Part Number
      And the user enters a new Part Description
      And the user enters a new Manufacturer Ref
      And the user enters a new Model
      And the user enters a new Serial Number
      And the user enters a new Unit Price less than the auto complete value
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Add to Request List is clicked
      And the Save Request is clicked
     Then the Parts Request has been recorded in the database
      And the Resource Assignment table has been updated with the status "Awaiting Parts Review"
    Examples:
      | profile        | jobtype  | priority | deliverymethod           | status      |
      | City Resource  | reactive | Same Day | Trade Counter Collection | In Progress |
      
      
  #@MTA-140 MCP-11910
  @bug
  Scenario Outline: Update job with parts in list and unit price below 250 and "<priority>" priority and deliver to Direct To Store
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed 
     When the user selects Part In List with Unit Price less than the auto complete value
      And the user selects the Supplier for Part In List
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
      And the Add to Request List is clicked
      And the Save Request is clicked
      And the "Process Purchase Order Documents" job runs
     Then the Parts Request has been recorded in the database
      And the Resource Assignment table has been updated with the status "Awaiting Parts"
      And the JobTimelineEvent table has been updated with "Parts Order Approved"
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress | status      |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       | In Progress |      
      | City Resource    | reactive | Next Day | Direct To Store | Home Store      | In Progress |   

  #@MTA-140 MCP-11910
  @bug
  Scenario Outline: Update job with parts in list and unit price below 250 and "<priority>" priority and deliver to Trade Counter Collection
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed 
     When the user selects Part In List with Unit Price less than the auto complete value
      And the user selects the Supplier for Part In List 
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Add to Request List is clicked
      And the Save Request is clicked
      And the "Process Purchase Order Documents" job runs
      And the "Export Job Updates" job runs      
     Then the Parts Request has been recorded in the database
      And the Resource Assignment table has been updated with the status "Awaiting Parts"
      And the JobTimelineEvent table has been updated with "Parts Order Approved"
    Examples:
      | profile          | jobtype  | priority | deliverymethod           | status      |
      | City Resource    | reactive | Same Day | Trade Counter Collection | In Progress |

  #@MTA-140 MCP-11910
  @bug
  Scenario Outline: Update job with parts in list and unit price greater than 250 and "<priority>" priority and deliver to Trade Counter Collection
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed 
     When the user selects Part In List with Unit Price greater than the auto complete value
      And the user selects the Supplier for Part In List 
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Add to Request List is clicked
      And the Save Request is clicked
      And the "Process Purchase Order Documents" job runs
     Then the Parts Request has been recorded in the database
      And the Resource Assignment table has been updated with the status "Awaiting Parts Review"
    Examples:
      | profile          | jobtype  | priority | deliverymethod           | status      |
      | City Resource    | reactive | Same Day | Trade Counter Collection | In Progress |                        