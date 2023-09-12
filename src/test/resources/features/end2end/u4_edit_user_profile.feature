@edit_user
Feature: U4 - Edit User Profile
  Scenario: AC 1 - Can access the Edit Team page through the edit button
    Given I am on the user profile page,
    When I hit the edit button
    Then I can see the edit profile form with all my details prepopulated except the passwords.


  Scenario: AC 2: Changes are saved when entering valid values
    Given I am on the edit profile form,
    And I enter valid values for my first name, last name, email address, and date of birth,
    When I hit the save button
    Then my new details are saved.

  Scenario: AC 3: An error message appears when invalid values are entered
    Given I am on the edit profile form,
    And I enter invalid values (i.e. empty strings or non-alphabetical characters except a hypen or space) for my firstname,
    When I hit the save button
    Then an error message tells me these fields contain invalid values.

  Scenario: AC 4: An error message appears when invalid email address is entered
    Given I am on the edit profile form,
    And I enter a malformed or empty email address,
    When I hit the save button
    Then an error message tells me the email address is invalid