$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("features/web/admin/resources/admin_contractor_callout.feature");
formatter.feature({
  "line": 3,
  "name": "Admin - Contractor Callout",
  "description": "",
  "id": "admin---contractor-callout",
  "keyword": "Feature",
  "tags": [
    {
      "line": 1,
      "name": "@admin"
    },
    {
      "line": 1,
      "name": "@admin_resources"
    },
    {
      "line": 1,
      "name": "@admin_resources_contractor_callout"
    },
    {
      "line": 2,
      "name": "@mcp"
    }
  ]
});
formatter.scenarioOutline({
  "line": 6,
  "name": "Configure Exception Call Out Costs to Contractor - \"\u003cCallout Type\u003e\"",
  "description": "",
  "id": "admin---contractor-callout;configure-exception-call-out-costs-to-contractor---\"\u003ccallout-type\u003e\"",
  "type": "scenario_outline",
  "keyword": "Scenario Outline",
  "tags": [
    {
      "line": 5,
      "name": "@andy"
    }
  ]
});
formatter.step({
  "line": 7,
  "name": "a user with \"Mercury_Admin_Core\" role has logged in",
  "keyword": "Given "
});
formatter.step({
  "line": 8,
  "name": "\"Admin\" is selected from the Mercury navigation menu",
  "keyword": "And "
});
formatter.step({
  "line": 9,
  "name": "the \"Resources \u0026 Users\" tile is selected",
  "keyword": "And "
});
formatter.step({
  "line": 10,
  "name": "the user selects \"Resources\" from the sub menu",
  "keyword": "And "
});
formatter.step({
  "line": 11,
  "name": "an Active Contractor Resource is created",
  "keyword": "When "
});
formatter.step({
  "line": 12,
  "name": "the resource is edited",
  "keyword": "And "
});
formatter.step({
  "line": 13,
  "name": "a \"\u003cCallout Type\u003e\" exception is added",
  "keyword": "And "
});
formatter.step({
  "line": 14,
  "name": "a new rate is added for Site with \"\u003cCallout Type\u003e\" type",
  "keyword": "Then "
});
formatter.examples({
  "line": 15,
  "name": "",
  "description": "",
  "id": "admin---contractor-callout;configure-exception-call-out-costs-to-contractor---\"\u003ccallout-type\u003e\";",
  "rows": [
    {
      "cells": [
        "Callout Type"
      ],
      "line": 16,
      "id": "admin---contractor-callout;configure-exception-call-out-costs-to-contractor---\"\u003ccallout-type\u003e\";;1"
    },
    {
      "cells": [
        "Standard"
      ],
      "line": 17,
      "id": "admin---contractor-callout;configure-exception-call-out-costs-to-contractor---\"\u003ccallout-type\u003e\";;2"
    }
  ],
  "keyword": "Examples"
});
formatter.before({
  "duration": 6185303737,
  "status": "passed"
});
formatter.before({
  "duration": 31798169,
  "status": "passed"
});
formatter.scenario({
  "line": 17,
  "name": "Configure Exception Call Out Costs to Contractor - \"Standard\"",
  "description": "",
  "id": "admin---contractor-callout;configure-exception-call-out-costs-to-contractor---\"\u003ccallout-type\u003e\";;2",
  "type": "scenario",
  "keyword": "Scenario Outline",
  "tags": [
    {
      "line": 5,
      "name": "@andy"
    },
    {
      "line": 1,
      "name": "@admin"
    },
    {
      "line": 1,
      "name": "@admin_resources"
    },
    {
      "line": 2,
      "name": "@mcp"
    },
    {
      "line": 1,
      "name": "@admin_resources_contractor_callout"
    }
  ]
});
formatter.step({
  "line": 7,
  "name": "a user with \"Mercury_Admin_Core\" role has logged in",
  "keyword": "Given "
});
formatter.step({
  "line": 8,
  "name": "\"Admin\" is selected from the Mercury navigation menu",
  "keyword": "And "
});
formatter.step({
  "line": 9,
  "name": "the \"Resources \u0026 Users\" tile is selected",
  "keyword": "And "
});
formatter.step({
  "line": 10,
  "name": "the user selects \"Resources\" from the sub menu",
  "keyword": "And "
});
formatter.step({
  "line": 11,
  "name": "an Active Contractor Resource is created",
  "keyword": "When "
});
formatter.step({
  "line": 12,
  "name": "the resource is edited",
  "keyword": "And "
});
formatter.step({
  "line": 13,
  "name": "a \"Standard\" exception is added",
  "matchedColumns": [
    0
  ],
  "keyword": "And "
});
formatter.step({
  "line": 14,
  "name": "a new rate is added for Site with \"Standard\" type",
  "matchedColumns": [
    0
  ],
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "Mercury_Admin_Core",
      "offset": 13
    }
  ],
  "location": "LoginSteps.a_user_with_role_has_logged_in(String)"
});
formatter.write("login as username: Andrew.Paton, password: City2019");
formatter.write("Scenario Starting:\nBrowser: chrome v:86.0.4240.193 XP\nDate   : Thu Jan 07 12:41:28 UTC 2021\nURL    : https://test-uswm.mercury.software/Helpdesk#!/home");
formatter.result({
  "duration": 4574647245,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Admin",
      "offset": 1
    }
  ],
  "location": "HelpdeskHomePageSteps.Admin_is_selected_from_the_Mercury_navigation_menu(String)"
});
formatter.result({
  "duration": 1946127281,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Resources \u0026 Users",
      "offset": 5
    }
  ],
  "location": "TileSteps.a_random_tile_is_selected(String)"
});
formatter.result({
  "duration": 3269304885,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Resources",
      "offset": 18
    }
  ],
  "location": "AdminInvoiceLinesSteps.the_user_selects_from_the_sub_menu(String)"
});
formatter.result({
  "duration": 2099843613,
  "status": "passed"
});
formatter.match({
  "location": "AdminResourcesAndUsersSteps.active_contractor_resource_is_created()"
});
formatter.write("Supplier Code: TEST:M!wX351414");
formatter.write("Supplier Name: Gracie Abernathy");
formatter.write("Resource Name: TestAuto1610023303340");
formatter.result({
  "duration": 318780542175,
  "status": "passed"
});
formatter.match({
  "location": "AdminResourcesAndUsersSteps.resource_is_edited()"
});
formatter.result({
  "duration": 85894935127,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Standard",
      "offset": 3
    }
  ],
  "location": "AdminResourcesAndUsersSteps.exception_is_added(String)"
});
formatter.result({
  "duration": 2241209654,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Standard",
      "offset": 35
    }
  ],
  "location": "AdminResourcesAndUsersSteps.new_rate_is_added(String)"
});
formatter.write("Asserting that correct Site has been added");
formatter.write("Asserting that the input box is displayed for the correct callout rate");
formatter.result({
  "duration": 692467251,
  "status": "passed"
});
formatter.after({
  "duration": 2179684370,
  "status": "passed"
});
});