@edit_lineup_for_game

#Feature: U33 – Edit line-up for game
#  Scenario: AC1: Add line-up to activity from existing team formations as manager
#    Given I am the manager of a team
#    And the team has a formation "1-2-3"
#    And the team has a formation "4-5-6"
#    And viewing the edit page for a team activity for that team
#    And the activity has type game or friendly
#    When I select the line-up "4-5-6" from the list of existing team formations
#    Then the saved activity has the formation "4-5-6"

#  Scenario: AC2: Display selected formation of team
#    Given I am the manager of a team
#    And viewing the edit page for a team activity for that team
#    And the activity has type game or friendly and the has a selected formation
#    When I select that formation for the game
#    Then the formation is displayed in the activity page
#
#
#  Scenario: AC3: Set player from team to a selected position on a formation
#    Given I am the manager of a team
#    And viewing the edit page for a team activity for that team
#    And the activity has type game or friendly and has a selected formation
#    When I select a position
#    Then I can set a player from the team to that position
#
#
#  Scenario: AC4: Formation can be edited to add a player
#    Given I am the manager of a team
#    And viewing the edit page for a team activity for that team
#    And the activity has type game or friendly
#    And the activity has a selected formation
#    When I add a player from the team to selected the formation
#    Then that player is unable to be added to the formation again
#    And that player’s picture and name are displayed at the correct position
#
#  Scenario: AC5 Add selected player to substitutes
#      Given I am the manager of a team
#      And viewing the edit page for a team activity for that team
#      And the activity has type game or friendly
#      And the activity has a selected formation
#      And all starting positions on the formation a filled with players
#      When I select another player
#      Then I can add the selected player to the list of substitutes
#
#  Scenario AC6: Player can be added to list of substitutes
#    Given I am the manager of a team
#    And viewing the edit page for a team activity for that team
#    And the activity has type game or friendly
#    And the activity has a selected formation
#    And all starting positions on the formation a filled with players
#    When I select another player
#    And add them to the list of substitutes
#    Then that players name and profile picture are displayed
#
#
#  Scenario AC7: User cannot save an empty formation
#    Given I am the manager of a team
#    And viewing the edit page for a team activity for that team
#    And the activity has type game or friendly
#    And the activity has a selected formation
#    When I attempt to save an empty formation
#    Then the formation is not saved and an error message is shown telling me the line-up is not complete
#
#
#  Scenario: AC8: Activity not updated if editing is cancelled
#    Given I am the manager of a team
#    And viewing the edit page for a team activity for that team
#    And the activity has type game or friendly
#    And the activity has a selected formation
#    When I attempt to cancel editing the activity
#    Then the activity will return to the state it was prior to editing
