Feature: U26- Edit team members role

  Scenario: AC1 - Given I am in the team profile page of a team I manage (e.g., created), when I click on a UI element
  to edit the members role, then I see the list of members and their roles in the team.
    Given I have created a team
    And I am a manager of the team
    When I click on the edit team role button
    Then I am taken to the edit team members role page

  Scenario: AC2 - When I see all team members with their roles, then their roles can be one of “manager”, “coach”,
  or “member”.
    Given I am on the edit team members role page
    When I see all members of the team
    Then I can select their role to be any of the options

  Scenario: Given I see all team members with their roles, when I want to change the role of a team member to any of
  the acceptable roles, then I need to confirm the change.
    Given I am on the edit team members role page
    When I change one team members role
    And I save the changes in team roles
    Then the change in team roles is saved


  Scenario: AC4 - Given I see all team members with their roles, when I want to change the role of multiple team members
  to any of the acceptable roles, then I only need to save the change once for all members.
  Given I am on the edit team members role page
  When I make change the roles of multiple team members
  And I save the changes in team roles
  Then The change in team roles is saved

#  Scenario: AC5 - When a team member has the role “manager”, then that team member can edit the team profile, and that
#  team member can generate new invitation tokens.
#
#  Scenario: AC6 - Given I update the roles of team members, when I don’t have any managers anymore, then an error
#  message tells me that I need at least one manager for the team, and I cannot save the changes.
#
#  Scenario: AC7 - Given I update the roles of team members, when I have more than three managers, then an error message
#  tells me that I cannot have more than 3 managers for the team, and I cannot save the changes.