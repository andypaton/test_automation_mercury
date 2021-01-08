@maintenance
Feature: create views in the Staging schema

  @MTA-226
  Scenario Outline: Create Staging view for <view>
    Given a resource file for view "<view>"
     When view is created
     Then the view returns results
    Examples: 
      | view              |
      | CityTechStores    |
      | Resources         |
      | Rota              |
      | VendorStores      |
      | JobSingleResource |
      | QuoteUserScenario | 