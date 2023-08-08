@edit_lineup_for_game_integration
Feature: U33 - Edit line-up for game
  Scenario: AC1: Add line-up to activity from existing team formations as manager
    Given I am the manager of a team
    And the team has a formation "4-5-6"
    And viewing the edit page for a team activity for that team
    And the activity has type game or friendly
    When I select the line-up "4-5-6" from the list of existing team formations
    Then the saved activity has the formation "4-5-6"
