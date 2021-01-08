@admin @admin @admin_sites
@uswm @ukrb @usah
Feature: Admin Site Type Locations
  
  Scenario: Admin Site Type Locations - Home Screen
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
     When the user selects "Site Types/Locations Mapping" from the sub menu
     Then the "Site Types" table on the "Site Types" page displays each row correctly
     
  Scenario: Site Type Locations Mapping table on Edit Site Type page
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
      And the user selects "Site Types/Locations Mapping" from the sub menu
     When the user clicks on the Edit link of a SiteType
     Then the "Site Type Locations Mapping" table on the "Site Type" page displays correctly
     
  Scenario: Site Type Locations Mapping table - Picture linked to the location should be displayed & relevant checkbox should be ticked
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
      And the user selects "Site Types/Locations Mapping" from the sub menu
     When the user clicks on the Edit link of a SiteType
      And searches for a location with a linked icon
     Then the "Site Type Locations Mapping" table on the "Site Type" page displays each row correctly
     
  Scenario: User saves Site Types Location Mapping - Popup alert with configuration changed for specific sitetype message should be displayed 
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
      And the user selects "Site Types/Locations Mapping" from the sub menu
      And the user clicks on the Edit link of a SiteType
      And searches for a location not mapped to the site type
     When "All" checkbox is ticked
      And the "Save" button is clicked
     Then "Saved" popup alert is displayed with text "configuration saved" for the specific site type
     
  Scenario: Adding a Location to Site Types - Cancel
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
      And the user selects "Site Types/Locations Mapping" from the sub menu
      And the user clicks on the Edit link of a SiteType
      And searches for a location not mapped to the site type
     When "All" checkbox is ticked
      And the "Cancel" button is clicked
     Then the "Site Types" table on the "Site Types" page displays each row correctly
      And the Site Type Location Mapping "is not" saved to the database 
     
  Scenario: Adding a Location to Site Types - Save
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
      And the user selects "Site Types/Locations Mapping" from the sub menu
      And the user clicks on the Edit link of a SiteType
      And searches for a location not mapped to the site type
     When "All" checkbox is ticked
      And the "Save" button is clicked
      And the "Saved" popup alert is confirmed
     Then the "Site Types" table on the "Site Types" page displays each row correctly
      And the Site Type Location Mapping "is" saved to the database

  #The following Delete scenarios are not valid for Site Types. Business team confirmed this         
  @deprecated @wip
  Scenario: Deleting a Location from Site Types - Save - Popup alert asking user to remove the assets before deleting the location is displayed
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
      And the user selects "Site Types/Locations Mapping" from the sub menu
     When the user clicks on the Edit link of a SiteType
      And searches for a location mapped to the site type
      And "All" checkbox is unticked
      And the "Save" button is clicked
     Then a "Please remove these assets or asset classification mappings prior to deleting the location." popup alert is displayed
   
  @deprecated @wip
  Scenario: Deleting a Location from Site Types - Cancel
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
      And the user selects "Site Types/Locations Mapping" from the sub menu
     When the user clicks on the Edit link of a SiteType
      And searches for a location mapped to the site type
      And "All" checkbox is unticked
      And the "Cancel" button is clicked
     Then the "Site Types" table on the "Site Types" page displays each row correctly
      And the Site Type Location Mapping "is" still saved to the database
      