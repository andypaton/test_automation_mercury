# Script to create all the necessary SQL scripts to set up the TestAutomation Database
# Example Usage: ConfigureDBScripts.ps1 "Test_USWM_MercuryHelpdesk" "Test_USWM_MercuryPortal" "Test_USWM_TestAutomation" "Test_USWM"
# Ouput will be a new folder named testDbSchema with all customer scripts created in there

$helpdeskDb=$args[0]
$portalDb=$args[1]
$testDb=$args[2]
$testSchema=$args[3]

Write-Host "Using the following input parameters:"
Write-Host "helpdeskDb: $helpdeskDb"
Write-Host "portalDb: $portalDb"
Write-Host "testDb: $testDb"
Write-Host "testSchema: $testSchema"

Read-Host -Prompt "Press any key to continue or CTRL+C to quit"

Write-Host "Starting the script creation"


New-Item -ItemType Directory -Force -Path .\$testSchema

& .\buildSQLFile.ps1 "CreateSchema.sql" $helpdeskDb $portalDb $testDb $testSchema

& .\buildSQLFile.ps1 "createDatabaseArtifacts.sql" $helpdeskDb $portalDb $testDb $testSchema

& .\buildSQLFile.ps1 "CreateFGASDatabaseArtifacts.sql" $helpdeskDb $portalDb $testDb $testSchema

& .\buildSQLFile.ps1 "CreateGasTables.sql" $helpdeskDb $portalDb $testDb $testSchema

& .\buildSQLFile.ps1 "MasterQuestion.sql" $helpdeskDb $portalDb $testDb $testSchema

& .\buildSQLFile.ps1 "MasterAnswer.sql" $helpdeskDb $portalDb $testDb $testSchema

& .\buildSQLFile.ps1 "GasType.sql" $helpdeskDb $portalDb $testDb $testSchema

& .\buildSQLFile.ps1 "GasQuestionSet.sql" $helpdeskDb $portalDb $testDb $testSchema

& .\buildSQLFile.ps1 "GasQuestionAnswerSet.sql" $helpdeskDb $portalDb $testDb $testSchema

& .\buildSQLFile.ps1 "insertAutomationUsers.sql" $helpdeskDb $portalDb $testDb $testSchema

& .\buildSQLFile.ps1 "GasQuestionSet.sql" $helpdeskDb $portalDb $testDb $testSchema

& .\buildSQLFile.ps1 "createIanaTimezones.sql" $helpdeskDb $portalDb $testDb $testSchema

Write-Host "Completed the script creation"








