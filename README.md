# Main
This package contains all code that is dependent on the application under test.

## mercury.api.models
Classes defining Json objects used when making REST API calls

## mercury.database
All class objects retuired for database access.

![alt text](/BDD/src/main/java/diagrams/database.png "Database class diagram")

### mercury.database.config
Classes defining the connection to the database and setting up of the EntityManager

### mercury.database.dao
Classes mapped to mercury.database.models containing methods to perform CRUD operations

### mercury.database.models
POJO's defining the database objects and fields retrieved and updating by the class objects in mercury.database.dao

## mercury.databuilders
Builder class objects which are used to create data items utilised in the test class objects

## mercury.driverfactory
Class objects used to initialise the webdriver for the desired configuration

![alt text](/BDD/src/main/java/diagrams/driverFactory.png "Driver factory class diagram")

## mercury.helpers
utility class objects providing methods to assist the main purpose of the main class objects within the package.  These should be declared as static methods and can be called by any class within the package.

See https://en.wikipedia.org/wiki/Helper_class for more information.

## mercury.pageFactory
The custom loadable component class which all other page object's are extended from

## mercury.pageobject
Base page class, which extends the custom loadable component class, which includes common methods to all page objects.  The base page is extended by all other page object classes.

![alt text](/BDD/src/main/java/diagrams/pageObjects.png "Page Object class diagram")

### mercury.pageobject.helpdesk
Package containing all help desk related page object class objects.

### mercury.pageobject.portal
A package containing all Portal related page object class objects.

## mercury.pageobject.helpdesk
Package containing all Helpdesk page objects.

## mercury.pageobject.portal
Package containing all Portal page objects.

## mercury.rest
Class object containing methods used to interact with REST api's

# Test
This package contains all code that is used to control the test framework.

## mercury.config
Class object initialising all the Beans that are required for the step definition classes.  For example if a DAO class object requires to be shared as a autowired bean in a step definition class file then declare it in this file as:

```java
	@Bean
	QuoteLineDao quoteLineDao() {
		return new QuoteLineDao();
	}
```

It can then be referenced in a step definition class with the following code:

```
@Autowired private QuoteLineDao quoteLineDao;
```

Note: This class breaks the rule defined for the package

## mercury.helpers
utility class objects providing methods to assist the main purpose of the main class objects within the package.  These should be declared as static methods and can be called by any class within the package.

See https://en.wikipedia.org/wiki/Helper_class for more information.

## mercury.helpers.asserter
A framework to enable database assertions to be executed until either the assertion passes or a set timelimit is reached.

![alt text](/BDD/src/main/java/diagrams/assertionFactory.png "Asserter class diagram")

### mercury.helpers.asserter.common
Shared class objects utilised by the dbassertions class objects.

### mercury.helpers.asserter.dbassertions
Database assertion classes interacting with main.mercury.database.dao class objects.

## mercury.runners
Serial and Parallel runner classes to defining which steps should be executed.

## mercury.runtime
Class object declaring all the page objects as public variables.  This class is utilised by the step files to allow coupling between the page objects and the feature files.

## mercury.steps
Class objects containing the code behind the gherkin feature files.
