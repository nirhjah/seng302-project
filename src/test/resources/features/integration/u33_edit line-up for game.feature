@edit_lineup_for_game

Feature: U33 – Edit line-up for game
#  Scenario: AC1: Add line-up to activity from existing team formations as manager
#    Given I am the manager of a team
#    And the team has a formation "1-2-3"
#    And the team has a formation "4-5-6"
#    And viewing the edit page for a team activity for that team
#    And the activity has type game or friendly
#    When I select the line-up "4-5-6" from the list of existing team formations
#    Then the saved activity has the formation "4-5-6"

  Scenario: AC8: Activity not updated if editing is cancelled
    Given I am the manager of a team
    And viewing the edit page for a team activity for that team
    And the activity has type game or friendly
    And the team has a formation "4-5-6"
    And the activity has a selected formation "4-5-6"
    When I attempt to cancel editing the activity
    Then the activity will return to the state it was prior to editing
