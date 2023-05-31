Feature: U23 - View my activities
#  Scenario: AC1 Given I am anywhere on the system, when I click on a UI element to see all my activities, then I see a list of all activities from all the teams I belong to and all the personal activities I created for myself.
#    Given I have personal and team activities
#    And I am on the home form
#    When I click the profile box and then select the My Activities button
#    Then I see a list of all my activities, both team and personal

#  Scenario: AC2 Given I see the list of all my activities, when I have personal activities, then these activities are shown first.
#    Given I have personal and team activities
#    When I load the my activities form
#    Then Personal activities are shown first
#
#  Scenario:  AC3 Given I see the list of all my activities, when I have scheduled team activities, then these activities are grouped by teams, and teams are shown in alphabetical order based on their name.
#    Given I have personal and team activities
#    When I load the my activities form
#    Then Team activities are grouped in alphabetical order

#  Scenario:  AC4 Given I see the list of all my activities, when I see the scheduled activities inside a group (i.e. individual or grouped by teams), then these activities are sorted by start time in ascending order (i.e. from earlier to later).
#    Given I have personal and team activities
#    When I load the my activities form
#    Then Grouped activities are sorted by time in ascending order

#
#  Scenario:  AC5 Given I see the list of all my activities, when there are more than 20 activities, then the activities are grouped in pages of at most 10 activities per page with pagination buttons.
#    Given I have a mix of 21 personal and team activities
#    When I load the my activities form
#    Then 10 activities are shown
#    And pagination is active

  Scenario: AC6 Given I see the list of all my activities and there are activities from teams I belong to, when I click on the team's name, then I am redirected to the team's profile page.
    Given I have personal and team activities
    And I am on the my activities form
    When I click on an activity team name
    Then I'm taken to the teams profile page



