@edit_team
Feature: Edit Team Details
  Scenario: AC1 - Can access the Edit Team page through the edit button
    Given I am on my team profile page,
    When I hit the edit button,
    Then I see the edit team details form with all its details prepopulated.

  Scenario: AC2 - Able to save new details after valid values are entered
    Given I am on the edit team profile form,
    And I enter valid values for the name, location and sport,
    When I hit the save button, then the new details are saved.

  Scenario: AC3 - Error message is displayed when the team's name is an invalid value
    Given I am on the edit team profile form,
    And I enter an invalid value for the team’s name (e.g., non-alphanumeric other than dots or curly brackets, name made of only acceptable non-alphanumeric),
    When I hit the save button,
    Then an error message tells me the name contains invalid characters.

  Scenario: AC4 - Error message is displayed when invalid values are entered
    Given I am on the edit team profile form,
    And I enter invalid values (i.e. empty strings or non-alphabetical characters) for the location or sport,
    When I hit the save button,
    Then an error message tells me these fields contain invalid values.


  Scenario: AC5
    Given I am on the edit team profile form,
    When I hit the cancel button, I come back to the team’s profile page,
    And no changes have been made to its profile