Feature: U1 - Register

  Scenario: AC1 - Register page is shown when register button clicked
    Given I have connected to the system's main URL
    When I hit the button to register
    Then I see a registration form


  Scenario: AC2 - Given I am on the registration form, and I enter valid values for my first name, last name, email address, date of birth, and type the same password twice, when I hit the register button, then a new user is created into the system, and I see my user profile page
    Given I have connected to the system's main URL
    And I hit the button to register
    And I see a registration form
    When I enter valid values for <firstName>, <lastName>, <emailAddress>, <dateOfBirth>, <password>, <confirmPassword>, <city> and <country>
    And Click the register button
    Then I see my user page


  Scenario: AC3 - Given I am on the registration form, and I enter invalid values (i.e. empty strings or non-alphabetical characters except a hyphen or a space) for my first name and/or last name, when I hit the register button, then an error message tells me these fields contain invalid values.
    Given I have connected to the system's main URL
    And I hit the button to register
    And I see a registration form
    When I enter invalid values for <firstName> or <lastName>
    And Click the register button
    Then I see an error message on the register page

  Scenario: AC4 - Given I am on the registration form, and I enter a malformed or empty email address, when I hit the register button, then an error message tells me the email address is invalid.
    Given I have connected to the system's main URL
    And I hit the button to register
    And I see a registration form
    When I enter an invalid email address <email>
    And Click the register button
    Then I see an error message on the register page

  Scenario: AC5 -Given I am on the registration form, and I enter a date of birth for someone younger than 13 years old, when I hit the register button, then an error message tells me I cannot register into the system because I am too young.
    Given I have connected to the system's main URL
    And I hit the button to register
    And I see a registration form
    When I enter an invalid date of birth <dateOfBirth>
    And Click the register button
    Then I see an error message on the register page

  Scenario: AC6 - Given I am on the registration form, and I enter an email address that already exists in the system, when I hit the register button, then an error message tells me the email address is already registered.
    Given I have connected to the system's main URL
    And I hit the button to register
    And I see a registration form
    When I enter an invalid email <email>
    And Click the register button
    Then I see an error message on the register page telling me the email is already in use


  Scenario: AC7 - AC7: Given I am on the registration form, and I enter two different passwords, when I hit the register button, then an error message tells me the passwords do not match
    Given I have connected to the system's main URL
    And I hit the button to register
    And I see a registration form
    When I enter a different password for <password> and <confirmPassword>
    And Click the register button
    Then I get an error message on the register page telling me the passwords do not match



