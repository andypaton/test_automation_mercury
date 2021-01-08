node {
    def mvnHome
    def WORKSPACE
    stage('Pull Repo') { 
        // Clone the GIT repo
        git branch: '${branch}', credentialsId: 'gitUser', url: 'git@bitbucket.org:cityfm/mercury-mcp-automation.git'

        // Configure the Maven tool.
        // NOTE: This 'M3' Maven tool must be configured in the global configuration.           
        mvnHome = tool 'Maven'
    }
        
    stage('Generate Runners') {
        /*
         * Generate the runners to be used in the Parallel non toggle tests
         * These are generated in Folder BDD/target /generated-test-sources 
         * Tags to include and exclude are passed in the parameter cucumberOptions which should have the following values for each 
         * suite from Jenkins. 
         *
         * Not Signed off
         * --tags @notsignedoff --tags ~@rework --tags ~@bug --tags ~@maintenance --tags ~@wip --tags ~@datasetup --tags ~@admin_toggles --tags ~@Invoicing --tags ~@AutoAssign --tags ~@AutoApproveContractorFundingRequests
         *  
         * Bug
         * --tags @bug --tags ~@rework --tags ~@notsignedoff --tags ~@maintenance --tags ~@wip --tags ~@datasetup --tags ~@admin_toggles --tags ~@Invoicing --tags ~@AutoAssign --tags ~@AutoApproveContractorFundingRequests
         *   
         * Post5am
         * --tags ~@rework --tags ~@notsignedoff --tags ~@bug --tags ~@maintenance --tags ~@wip --tags ~@datasetup --tags ~@admin_toggles --tags ~@Invoicing --tags ~@AutoAssign --tags ~@AutoApproveContractorFundingRequests
         * 
         * Rework
         * --tags @rework --tags ~@notsignedoff --tags ~@bug --tags ~@maintenance --tags ~@wip --tags ~@datasetup --tags ~@admin_toggles --tags ~@Invoicing --tags ~@AutoAssign --tags ~@AutoApproveContractorFundingRequests
         * 
         * Signed off site
         * --tags ~@rework --tags ~@notsignedoff --tags ~@bug --tags ~@maintenance --tags ~@wip --tags ~@datasetup --tags ~@admin_toggles --tags ~@Invoicing --tags ~@AutoAssign --tags ~@AutoApproveContractorFundingRequests
         * 
         * Smoke
         * --tags @smoke --tags ~@wip
         * --tags ~@admin_toggles --tags ~@Invoicing --tags ~@AutoAssign --tags ~@AutoApproveContractorFundingRequests
         */
        
        dir('BDD') {
            // Run the maven build
            if (isUnix()) {
               sh "'${mvnHome}/bin/mvn' -f generator.xml -Dmaven.test.failure.ignore clean generate-test-sources -Dcucumber.options='${cucumberOptions}'"
            } else {
               bat(/"${mvnHome}\bin\mvn" -f generator.xml -Dmaven.test.failure.ignore clean generate-test-sources -Dcucumber.options="${cucumberOptions}"/)
            }
        }
    }
    
    stage('Execute Thread Safe Tests') {
//        //Reset the system toggles so the default before the test run
//        dir('BDD') {
//            // Run the maven build
//            if (isUnix()) {
//               sh "'${mvnHome}/bin/mvn' -f pom.xml -Dmaven.test.failure.ignore verify -DthreadCount=1 -Dwebdriver=Chromeheadless -Denv=${testEnv} -Duser.name=jenkins -Dset.SystemToggles=true -DwebdriverConfig=${driverConfig} -Dcucumber.options='--tags @reset_toggles'"
//            } else {
//               bat(/"${mvnHome}\bin\mvn" -f pom.xml -Dmaven.test.failure.ignore verify -DthreadCount=1 -Dwebdriver=Chromeheadless -Denv=${testEnv} -Duser.name=jenkins -Dset.SystemToggles=true -DwebdriverConfig=${driverConfig} -Dcucumber.options="--tags @reset_toggles"/)
//            }
//        }
        
        //Executes all parallel non toggle tests using the runners created in the previous step
        //All cucumber json results are stored in folder BDD/target/cucumber-parallel 
        //Eventually this block will change to execute each runner on a separate jenkins docker instance
        dir('BDD') {
            // Run the maven build
            if (isUnix()) {
               sh "'${mvnHome}/bin/mvn' -f pom.xml -Dmaven.test.failure.ignore test -DthreadCount=${threadCount} -Pparallel_cucumber -Dwebdriver=Chromeheadless -Denv=${testEnv} -Duser.name=jenkins -Dset.SystemToggles=true -DwebdriverConfig=${driverConfig} -Dcucumber.options='${cucumberOptions}'"
            } else {
               bat(/"${mvnHome}\bin\mvn" -f pom.xml -Dmaven.test.failure.ignore test -DthreadCount=${threadCount} -Pparallel_cucumber -Dwebdriver=Chromeheadless -Denv=${testEnv} -Duser.name=jenkins -Dset.SystemToggles=true -DwebdriverConfig=${driverConfig} -Dcucumber.options="${cucumberOptions}"/)
            }
        }
    }
    
    stage('Backup Results') {
        //Copy all cucumber json results to the folder below. This is a temp holding location for the json files.
        fileOperations(
           [folderCreateOperation('\\BDD\\target\\resultstemp'),
            folderCreateOperation('\\BDD\\target\\resultstemp\\parallel'),
           fileCopyOperation(excludes: 'BDD/results/*/*.json',
                             flattenFiles: true,
                             includes: 'BDD/target/cucumber-parallel/*.json',
                             targetLocation: '\\BDD\\target\\resultstemp\\parallel')
        ])
        
        fileOperations([fileDeleteOperation(excludes: 'BDD/results/*/*.json', includes: 'BDD/target/cucumber-parallel/*')])
    }
      
    stage('Execute Non Thread Safe Tests') {
//        //Reset the system toggles so the default before the test run
//        dir('BDD') {
//            // Run the maven build
//            if (isUnix()) {
//               sh "'${mvnHome}/bin/mvn' -f pom.xml -Dmaven.test.failure.ignore verify -DthreadCount=1 -Dwebdriver=Chromeheadless -Denv=${testEnv} -Duser.name=jenkins -Dset.SystemToggles=true -DwebdriverConfig=${driverConfig} -Dcucumber.options='--tags @reset_toggles'"
//            } else {
//               bat(/"${mvnHome}\bin\mvn" -f pom.xml -Dmaven.test.failure.ignore verify -DthreadCount=1 -Dwebdriver=Chromeheadless -Denv=${testEnv} -Duser.name=jenkins -Dset.SystemToggles=true -DwebdriverConfig=${driverConfig} -Dcucumber.options="--tags @reset_toggles"/)
//            }
//        }
        
        //Executes toggle tests using the runners defined in the given maven profile
        //All cucumber json results are stored in folder BDD/target/cucumber-parallel 
        //Eventually this block will change to execute each runner on a separate jenkins docker instance
        dir('BDD') {
            // Run the maven build
            if (isUnix()) {
               sh "'${mvnHome}/bin/mvn' -f pom.xml -Dmaven.test.failure.ignore verify -DthreadCount=${threadCount} -P${mavenProfile} -Dwebdriver=Chromeheadless -Denv=${testEnv} -Duser.name=jenkins -Dset.SystemToggles=true -DwebdriverConfig=${driverConfig}"
            } else {
               bat(/"${mvnHome}\bin\mvn" -f pom.xml -Dmaven.test.failure.ignore verify -DthreadCount=${threadCount} -P${mavenProfile} -Dwebdriver=Chromeheadless -Denv=${testEnv} -Duser.name=jenkins -Dset.SystemToggles=true -DwebdriverConfig=${driverConfig} /)
            }
        }
    }
    
    stage('Merge Results') {
        //Copy all cucumber json results from the temp folder to the main results folder.
        fileOperations(
           [folderCreateOperation('\\BDD\\target\\results\\parallel'),
        ])
        fileOperations(
            [fileCopyOperation(excludes: '',
                             flattenFiles: true,
                             includes: 'BDD/target/resultstemp/parallel/*.json',
                             targetLocation: '\\BDD\\target\\results\\parallel')
                
                ])
        
        fileOperations([fileDeleteOperation(excludes: 'BDD/results/*/*.json', includes: 'BDD/target/resultstemp/*.json')])
    }   

    stage('Generate Cucumber Reports') {
        //Generate cucumber report
        cucumber failedFeaturesNumber: -1, failedScenariosNumber: -1, failedStepsNumber: -1, fileExcludePattern: 'BDD/target/cucumber-parallel/*.json', fileIncludePattern: '**/*.json', jsonReportDirectory: 'bdd/target/results', pendingStepsNumber: -1, skippedStepsNumber: -1, sortingMethod: 'ALPHABETICAL', undefinedStepsNumber: -1
    }

    stage('Reset Toggles') {
        //Reset the system toggles so the default
        dir('BDD') {
            // Run the maven build
            if (isUnix()) {
               sh "'${mvnHome}/bin/mvn' -f pom.xml -Dmaven.test.failure.ignore verify -DthreadCount=1 -Dwebdriver=Chromeheadless -Denv=${testEnv} -Duser.name=jenkins -Dset.SystemToggles=true -DwebdriverConfig=${driverConfig} -Dcucumber.options='--tags @reset_toggles'"
            } else {
               bat(/"${mvnHome}\bin\mvn" -f pom.xml -Dmaven.test.failure.ignore verify -DthreadCount=1 -Dwebdriver=Chromeheadless -Denv=${testEnv} -Duser.name=jenkins -Dset.SystemToggles=true -DwebdriverConfig=${driverConfig} -Dcucumber.options="--tags @reset_toggles"/)
            }
        }
    }

}