#@MCP-1203
@portal @portal_jobs
@wip @deprecated
@mcp
Feature: Portal - Jobs - Update with status Awaiting Parts - Negative
# The pricebook data has not been configured correctly therefore these tests are prone to failing
# Marking as @wip @deprecated as these test functionality that is performed by techs on the ipads

  #@negative @MTA-222 @MTA-900 @MTA-923
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part In List and verify Part Details are populated 
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed 
     When the user selects Part In List  
      And the user selects the Supplier for Part In List
     Then the Part Number is populated
      And the Part Description is populated
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |
      
      
  #@negative @MTA-274 @MTA-295
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part In List with no Supplier and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed 
     When the user selects Part In List  
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |
      
  #@negative 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part In List with no Priority and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed 
     When the user selects Part In List  
      And the user selects the Supplier for Part In List
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |
 
  #@negative
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part In List with no Quanity and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed 
     When the user selects Part In List  
     And the user selects the Supplier for Part In List
      And the Priority "<priority>" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |

  #@negative @MTA-813
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part In List with no Delivery Method and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed 
     When the user selects Part In List  
      And the user selects the Supplier for Part In List
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |
      
  #@negative 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part In List with no Delivery Address and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed 
     When the user selects Part In List  
      And the user selects the Supplier for Part In List
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |
    
  #@negative
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part Not In List with no Supplier  and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed
      And the user has a new Part
     When the user selects Part Not In List
      And the user enters a new Part Number
      And the user enters a new Part Description
      And the user enters a new Manufacturer Ref
      And the user enters a new Model
      And the user enters a new Serial Number
      And the user enters a new Unit Price
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |

  #@negative
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part Not In List with no Part Number and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed
      And the user has a new Part
     When the user selects Part Not In List
      And the user selects a random Supplier
      And the user enters a new Part Description
      And the user enters a new Manufacturer Ref
      And the user enters a new Model
      And the user enters a new Serial Number
      And the user enters a new Unit Price
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |
    
  #@negative 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part Not In List with no Part Description and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed
      And the user has a new Part
     When the user selects Part Not In List
      And the user selects a random Supplier
      And the user enters a new Part Number
      And the user enters a new Manufacturer Ref
      And the user enters a new Model
      And the user enters a new Serial Number
      And the user enters a new Unit Price
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |

  #@negative 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part Not In List with no Manufacturer Ref and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed
      And the user has a new Part
     When the user selects Part Not In List
      And the user selects a random Supplier
      And the user enters a new Part Number
      And the user enters a new Part Description
      And the user enters a new Model
      And the user enters a new Serial Number
      And the user enters a new Unit Price
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |
    
  #@negative 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part Not In List with no Model  and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
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
      And the user enters a new Serial Number
      And the user enters a new Unit Price
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |
  
  #@negative 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part Not In List with no Serial Number and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
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
      And the user enters a new Unit Price
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |

  #@negative 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part Not In List with no Priority and verify Add to Request List button is disabled
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
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
      And the user enters a new Unit Price
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
     Then the Add to Request List button is diabled
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |
    
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part In List and verify the New Parts Request Grid is updated
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed 
     When the user selects Part In List  
      And the user selects the Supplier for Part In List
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
      And the Add to Request List is clicked
     Then the Existing Parts Request grid is updated
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |

  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part Not In List and verify the New Parts Request Grid is updated
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
      And the user enters a new Unit Price
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
      And the Add to Request List is clicked
     Then the Existing Parts Request grid is updated
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |
      
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part In List and verify the New Parts Request Grid is updated
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
      And the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the update Job form is complete with "Awaiting Parts"
      And the "Parts Request" form will be displayed 
     When the user selects Part In List  
      And the user selects the Supplier for Part In List
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
      And the Add to Request List is clicked
     Then the Existing Parts Request grid is updated
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |

  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Awaiting Parts and Part Not In List and verify the New Parts Request Grid is updated
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
      And the user enters a new Unit Price
      And the Priority "<priority>" is entered
      And the quantity "1" is entered
      And the Delivery Method "<deliverymethod>" is entered
      And the Delivery Address "<deliveryaddress>" is entered
      And the Add to Request List is clicked
     Then the Existing Parts Request grid is updated
    Examples:
      | profile          | jobtype  | priority | deliverymethod  | deliveryaddress |
      | City Resource    | reactive | Same Day | Direct To Store | Job Store       |      
