
# buildSQLFile.ps1
# Replaces the placeholders with the correct information for the environment under test

$fileName=$args[0]
$helpdeskDb=$args[1]
$portalDb=$args[2]
$testDb=$args[3]
$testSchema=$args[4]

write-host "`n"
write-host "`n"
Write-Host "Using the following input parameters:"
write-host "`n"
Write-Host "fileName: $fileName"
Write-Host "helpdeskDb: $helpdeskDb"
Write-Host "portalDb: $portalDb"
Write-Host "testDb: $testDb"
Write-Host "testSchema: $testSchema"

(Get-Content .\$fileName).replace('%helpdeskDb', $helpdeskDb) | Set-Content .\$testSchema\$fileName
(Get-Content .\$testSchema\$fileName).replace('%portalDb', $portalDb) | Set-Content .\$testSchema\$fileName
(Get-Content .\$testSchema\$fileName).replace('%testdb', $testDb) | Set-Content .\$testSchema\$fileName
(Get-Content .\$testSchema\$fileName).replace('%testSchema', $testSchema) | Set-Content .\$testSchema\$fileName
write-host "`n"
write-host "Completed $fileName"