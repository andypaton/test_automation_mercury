@maintenance
Feature: create jobs

  Scenario Outline: bulk job creation via API - manual input
    Given asset/fault mappings: "<AssetTypeId>" "<AssetTypeName>" "<AssetSubTypeId>" "<AssetSubTypeName>" "<AssetClassificationId>" "<AssetClassificationName>" "<FaultTypeId>" "<FaultTypeName>" "<Location>"
      And for siteId "420"
     When api requests are made to create jobs
     Then the create job results are output
   Examples:
      | AssetTypeId | AssetTypeName | AssetSubTypeId | AssetSubTypeName   | AssetClassificationId | AssetClassificationName | FaultTypeId | FaultTypeName                                 | Location |
      | 1           | Bakery        | 1              | Bread Slicer       | 1                     | Standard                | 15          | Competitive Impact                            | Department - Bakery/Bakery Prep |
      | 1           | Bakery        | 2              | Cake Photo Printer | 2                     |                         | 59          | Installation/Replacement                      | Department - Dairy/Dairy Prep |
      | 1           | Bakery        | 3              | Donut Fryer        | 3                     |                         | 15          | Competitive Impact                            | Department - Deli/Deli Prep |
      | 1           | Bakery        | 4              | Mixer              | 4                     | Pastry                  | 15          | Competitive Impact                            | Department - Fresh Meat/Fresh Meat Prep |
      | 1           | Bakery        | 5              | Mobile Cart        | 5                     | Hot                     | 59          | Installation/Replacement                      | Department - Pizza/Pizza Prep |
      | 1           | Bakery        | 6              | Oven               | 6                     | Modular                 | 59          | Installation/Replacement                      | Department - Produce/Produce Prep |
      | 1           | Bakery        | 6              | Oven               | 138                   | Rack                    | 62          | Leaking                                       | Department - Rotisserie/Rotisserie Prep |
      | 1           | Bakery        | 7              | Proofer            | 7                     |                         | 130         | Working Intermittently                        | Department - Seafood/Seafood Prep |
      | 1           | Bakery        | 8              | Retarder           | 8                     |                         | 130         | Working Intermittently                        | Plant Room - Sprinkler/Riser |
      | 1           | Bakery        | 9              | Water Chiller      | 9                     |                         | 24          | Damaged - Still Operational                   | Department - Outdoor Living/Garden Center |
      | 1           | Bakery        | 145            | Tortilla Machine   | 3326                  |                         | 453         | Electric Shock                                | Cafe/Seating |
      | 1           | Bakery        | 146            | Thermoglazer       | 3327                  |                         | 90          | Not switching off                             | Department - Tire/Battery |
      | 1           | Bakery        | 157            | Rack/Pan Washer    | 3331                  |                         | 22          | Damaged                                       | Plant Room - Generator |
      | 1           | Bakery        | 188            | Display Case       | 3276                  | Donut Case              | 49          | Glass - Smashed/Damaged                       | Department - Health and Beauty |
      | 1           | Bakery        | 188            | Display Case       | 3282                  | Pretzel Display         | 24          | Damaged - Still Operational                   | Checkout |
      | 1           | Bakery        | 188            | Display Case       | 3383                  |                         | 4           | Alarm - Rack Pressure                         | Parking Lot - Landscaped Areas |
      | 1           | Bakery        | 196            | Bread Warmer       | 3272                  |                         | 85          | Non Operational                               | Department - Vision Center |
      | 2           | Building      | 10             | Awning             | 10                    |                         | 80          | Missing Panels/Parts                          | Roof |
      | 2           | Building      | 11             | Bollard            | 11                    |                         | 53          | Health & Safety - Immediate Response Required | Plant Room - Electrical |
      | 2           | Building      | 12             | Cart Corral        | 12                    |                         | 80          | Missing Panels/Parts                          | Rest Room - Associate Ladies |
      | 2           | Building      | 13             | Ceiling            | 13                    | Metal Tile              | 15          | Competitive Impact                            | Rest Room - Associate Handicap |
      | 2           | Building      | 13             | Ceiling            | 139                   |                         | 59          | Installation/Replacement                      | Department - Sporting Goods |
      | 2           | Building      | 13             | Ceiling            | 140                   | Tile                    | 59          | Installation/Replacement                      | Department - Tobacco |
      | 2           | Building      | 15             | Clock              | 15                    |                         | 15          | Competitive Impact                            | Rest Room - Customer Ladies |
      | 2           | Building      | 16             | Door               | 16                    | External Fire           | 50          | Grafitti Removal - Non Offensive              | Customer Service Desk |
      | 2           | Building      | 16             | Door               | 142                   | External Fire Shutter   | 126         | Unable to secure store                        | Foyer |
      | 2           | Building      | 16             | Door               | 143                   | External Metal          | 116         | Structural Damage - Critical                  | Auto Care Center |
      | 2           | Building      | 34             | Parking Lot        | 3306                  | Speed Bump              | 19          | Cracked/Scratched                             | Office - Training Room (ADC) |
      | 2           | Building      | 48             | Window             | 188                   | Glazing Shop Window     | 19          | Cracked/Scratched                             | Gas Station |
      | 7           | Food Services | 222            | Meat Tenderizer    | 3285                  |                         | 19          | Cracked/Scratched                             | Department - Online Pickup |
     
     
  Scenario Outline: bulk job creation via API - all possible assets to all fault type combinations (over 4000)
    Given database mappings for all assets to all faults
      And for siteId "<siteId>"
     When the Service Channel throttle is set to a "2" second wait after every "10" jobs 
      And api requests are made to create jobs
     Then the create job results are output
    Examples:
     | siteId |
     | 6      |
     | 420    |
     
     
  # creates about 3 - 4 jobs per minute
  Scenario Outline: bulk job creation via Helpdesk - all possible assets to all fault type combinations (over 4000)
    Given database mappings for all assets to all faults
      And for a "<siteType>" site
     When requests are made to create jobs via the Helpdesk
     Then the create job results are output
    Examples:
     | siteType            |
     | Neighborhood Market |
     | Sam's Club          |
     | SuperCenter         |
     | Walmart             |
     

  Scenario Outline: bulk job creation - specified asset classifications to all fault type combinations
    Given database mappings for the following asset classifications to all faults:
      | Auto Center       | 
      | Electronics       | 
      | Gun               | 
      | Photo Lab         | 
      And for a "<siteType>" site
     When api requests are made to create jobs
     Then the create job results are output
    Examples: 
      | siteType            | 
      | Neighborhood Market | 
      | Sam's Club          | 
      | SuperCenter         | 
      | Walmart             | 
      

  @toggles @AutoAssign
  Scenario: bulk job creation - specified asset classifications to all fault type combinations
    Given the assets listed in excel filename "RVRSNew27Sept18.csv"
      And the system feature toggle "AutoAssign" is "disabled"
     When api requests are made to create jobs
     Then the create job results are output
     

  @toggles @AutoAssign
  Scenario: rerun failed jobs
    Given failed jobs that were recorded in the TestAutomation_Audit table and created between "2018-10-08 14:01:26.480" and "2018-10-08 14:01:26.480"
      And the system feature toggle "AutoAssign" is "disabled"
     When api requests are made to create jobs
     Then the create job results are output
     

  @toggles @AutoAssign
  Scenario: rerun failed API requests
    Given failed API requests that were recorded in the TestAutomation_Audit table and created between "2018-10-05 09:03:02.883" and "2018-10-05 09:05:14.603"
      And the system feature toggle "AutoAssign" is "disabled"
     When the failed API requests are resubmitted
     Then the create job results are output
     
     
  Scenario Outline: bulk job creation via API - all possible non RHVAC assets to a random fault type combinations (around 310)
    Given database mappings for all non RHVAC assets to a random fault
      And for siteId "<siteId>"
     When the Service Channel throttle is set to a "2" second wait after every "10" jobs 
      And api requests are made to create jobs
     Then the create job results are output
    Examples:
     | siteId |
     | 6      |
     | 420    |
     

  # this scenario runs from Jenkins and is built with parameters
  @jobCreationHelper
  Scenario: Create a Job in the correct status
#    Given system property "ResourceType" is "City Resource"
#      And system property "JobResourceStatus" is "Logged / Awaiting Acceptance"
#      And system property "AssetType" is "Non gas"
#      And system property "NumberOfJobs" is "1"
#      And system property "Priority" is "P0"  
#      And system property "Environment" is "trn_usad"  
    Given jobs have to be created
     When jobs are created via api calls
     Then the create job results are saved
     
  # same as above - but with properties fed in from given steps instead of from jenkins job
  Scenario: Create a Job in the correct status
    Given system property "Environment" is "test_usad"
      And system property "ResourceType" is "Contractor"
      And system property "JobResourceStatus" is "Logged"
      And system property "AssetType" is "Non gas"
      And system property "NumberOfJobs" is "1"
      And system property "Priority" is "P0"
      And jobs have to be created
     When jobs are created via api calls
     Then the create job results are saved
     
  # replicate above scenario, but for 1 job and data supplied in datatable
  Scenario Outline: Create a Job
    Given "<JOB_RESOURCE_STATUS>" job has to be created for "<RESOURCE_TYPE>" for "<ASSET_TYPE>" and "<PRIORITY>" priority
     When jobs are created via api calls
     Then the create job results are saved
    Examples:
      | RESOURCE_TYPE | JOB_RESOURCE_STATUS | ASSET_TYPE | PRIORITY |
      | Contractor    | Fixed / Complete    | Non gas    | P0       |
      
      
      
  # this scenario runs from Jenkins and is built with parameters
  # in walmart the resource needs to be added to the ppm type until new changes are merged
  # in advocate the resource is added to the ppm
  @ppmCreationHelper
  Scenario: Create a PPM Job
    Given a system property for "Environment"
     When PPM jobs are created
     Then the create job results are saved
     
  # same as above - but with properties fed in from given steps instead of from jenkins job
  # to run this scenario locally you will need to map jenkins folder to your Y drive
  # in walmart the resource needs to be added to the ppm type until new changes are merged
  # in advocate the resource is added to the ppm
  Scenario: Create a PPM Job
    Given system property "Environment" is "trn_usad"
      And the properties are set up correctly
     When PPM jobs are created
     Then the create job results are saved
     
  # this scenario runs from Jenkins and is built with parameters
  # in walmart the resource needs to be added to the ppm type until new changes are merged
  # in advocate the resource is added to the ppm
  @ppmCreationHelperSite
  Scenario: Create a PPM Job for Site
    Given a system property for "Environment"
     When PPM jobs are created for site
     Then the create job results are saved
     
  # same as above - but with properties fed in from given steps instead of from jenkins job
  # to run this scenario locally you will need to map jenkins folder to your Y drive
  # in walmart the resource needs to be added to the ppm type until new changes are merged
  # in advocate the resource is added to the ppm
  Scenario: Create a PPM Job for Site
    Given system property "Environment" is "test_uswm"
      And the properties are set up correctly
     When PPM jobs are created for site
     Then the create job results are saved
      