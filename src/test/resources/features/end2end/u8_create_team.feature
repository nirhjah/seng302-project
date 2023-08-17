@create_team
Feature: U8 - Create Team
  Scenario: AC1 - Can access Create Team from anywhere
    Given I am on the home page
    When I click on the Create Team button
    Then I am brought to the Create Team page
  
  Scenario: AC2 - Creating a valid team brings you to that team's page.
    Given I am on the Create Team page
    When I enter a team name of "TestingTeam"
    And I enter a team sport of "Soccer"
    And I enter a team city of "Christchurch" in the country "New Zealand"
    And I submit the Create Team form
    Then I am brought to this team's page
    And the team name, sport, and location are "TestingTeam", "Soccer", and "Christchurch, New Zealand"

  Scenario: AC3 - Invalid team name
    Given I am on the Create Team page
    When I enter a team name of "!!!"
    And I enter a team sport of "Soccer"
    And I enter a team city of "Christchurch" in the country "New Zealand"
    And I submit the Create Team form
    Then the Create Team form is not submitted

  Scenario: AC4 - Invalid location
    Given I am on the Create Team page
    When I enter a team name of "TestingTeam"
    And I enter a team sport of "Soccer"
    And I enter a team city of "!!!" in the country "!!!"
    And I submit the Create Team form
    Then the Create Team form is not submitted

  Scenario: AC5 - Invalid sport
    Given I am on the Create Team page
    When I enter a team name of "TestingTeam"
    And I enter a team sport of "K!CK!NG S0CC3R B@LLS"
    And I enter a team city of "Christchurch" in the country "New Zealand"
    And I submit the Create Team form
    Then the Create Team form is not submitted

  Scenario: AC6 - Cancel Button
    Given I am on the Create Team page
    When I click on the Cancel button
    Then I am brought to the home page

  # --- Non ACs ---

  Scenario: Issue - The Create Team page should say "Create Team"
    Given I am on the Create Team page
    Then the page's title is "Create Team"
    And the page's header is "Create Team"
    And the page's submit button is "Create"

  # TODO: Figure out how to post an invalid form when all the fields are client-side validated
  # Scenario: Issue - The page should still say "Create Team" after a posting failure
  #   Given I am on the Create Team page
  #   And I submit an invalid form
  #   Then the page's title is "Create Team"
  #   And the page's header is "Create Team"
  #   And the page's submit button is "Create"