
1. [Coding Standards]
    1. [Step Definition Methods]
    1. [Declaring Autowired variables]
    1. [Declaring private WebElement variables]
    1. [Feature Files]
1. [Writing page objects]

# Coding Standards

All pull requests will be compared against the following standards defined in this document.

## Step Definition Methods

Please use the following naming rules when defining methods in step class.

* Method name should match as close as possible to the gherkin step
* Underscores to separate words
* All words in lower case

The following is an acceptable example:

```java
    @Then("^some action on the page is performed$")
    public void some_action_on_the_page_is_performed() throws Exception {
        runtimeState.superHelpdeskPage.performAction();
    }
```

The following will result in a pull request being rejected

```java
    @Then("^some action on the page is performed$")
    public void someActionOnThePageIsPerformed() throws Exception {
        runtimeState.superHelpdeskPage.performAction();
    }
```


## Declaring Autowired variables

When defining Autowired variable please use the following formatting:

```java
    @Autowired private RuntimeState runtimeState;
    @Autowired private User user;
    @Autowired private TestDataRequirements testData;
```

The following will result in a pull request being rejected:

```java
    @Autowired 
    private RuntimeState runtimeState;
    @Autowired 
    private User user;
    @Autowired 
    private TestDataRequirements testData;
```

## Declaring private WebElement variables

To enhance readability please put a line between each declaration.

```java
    @FindBy(css = SUBHEAD_CSS)
    WebElement subHeadline;

    @FindBy(css = SUBHEAD_LEFT_CSS)
    WebElement subHeadline_left;

    @FindBy(css = SUBHEAD_RIGHT_CSS)
    WebElement subHeadline_right;
```    

The following will result in a pull request being rejected:

```java
    @FindBy(css=SUBHEAD_CSS)
    WebElement subHeadline
    @FindBy(css=SUBHEAD_LEFT_CSS)
    WebElement subHeadline_left;
    @FindBy(css=SUBHEAD_RIGHT_CSS)
    WebElement subHeadline_right;
```    

## Declaring variables

Please ensure a space is used when defining static variables if they are being built from others.

```java
    private static final String SUBHEAD_RIGHT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-subheader__right";
```

The following will result in a pull request being rejected
```java
    private static final String SUBHEAD_RIGHT_CSS = ACTIVE_WORKSPACE_CSS+" div.view-subheader__right";
```

## Feature Files

Tags which reference a Jira ticket or Bit Bucket branch should be defined using all upper case characters.  All other tags should be defined using lower case characters.

Each feature should be described by first stating the area of the Application, for example "Helpdesk - " then followed by a descriptive name of the feature as a whole.

Indentation should be two spaces. The exception to this is that the Given, When, Then and And should be right aligned.

All scenarios not signed of by the business should contain the @notsignedoff tag.
All scenarios signed off by the business should contain the @signedoff tag.
All scenarios that are currently in development or are defined as placed holders for future development should have the @wip tag.

```java
@helpdesk @logajob @vendorStore
Feature: Helpdesk - Log a Job for a non Contract store without City Tech
  
  @signedoff @MTA-137  
  Scenario Outline: log job for vendor store - P1 fault, contractor configured, helpdesk <hours>
    Given a "Helpdesk Operator" has logged in "<hours>"
      And a vendor store with a priority "P1" fault
     When some action on the page is performed
     Then some element on the page is updated
      And a notification email is received

```

## Writing page objects

All page objects should defined with the following structure

- Define Static variables
- Elements found consistently on each page, eg Headers, Main Content, Footers
- Elements unique to each pag
- Define WebElements
- Define methods common to all pages
  - page constructor
  - isLoaded
  - getHeaderText
  - getPageTitle
- Define page interaction methods
- selectResourceType
- SelectResource
- getSelectedResource


# Source control

## Branch names
All branch names should match the Jira ticket that it relates to.  This enables hyper-linking between Bit Bucket and Jira. Due to the small amount of Jira tickets in-flight and size of the team it is not required to attach a description to the branch name.

```java
MTA-321
```

```jira
MTA-321-some-work-getting-done
```


## Commit Messages

The first line of the Commit messages should contain the branch name followed by a blank line.  The remaining of the message should be descriptive of the worked carried out in the commit.  This will allow linking between the Bit Bucket commit screen and Jira and will also make it easy to see what each commit was pertaining to.  Show screen shot here.

```java
MTA-321

Updated superHelpdeskPage class to include the new super control definition and actions.
Re-factored step definition to use new class
```

## Commit Frequency

Ideally the more often the better, this will ensure that the code is safely on the server and can serve as a point to rollback too.  At a minimum commits should take place daily.

## Merging Master to Local Branch

The script **getstatus.sh** should be used to pull the latest origin/master into the local working branch.  This will ensure everyone is using a consistent method.  The script can be executed from the git bash command prompt using the following

```bash
./getstatus.sh
```

```bash
#!/bin/bash
git status
git status --porcelain|awk '{if($1=="??") {print "git add " $2}}'
git fetch origin
git merge origin/master
```

## Pulling Master

The script **getmaster.sh** pulls down the latest version of Master to you working directory.   The script can be executed from the git bash command prompt using the following

```bash
./getmaster.sh
```

```
#!/bin/bash
git checkout master
git fetch origin
git reset --hard origin/master
```




