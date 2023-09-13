@view_team
Feature: U9 - View Team Profile

  Scenario: AC1: Able to view team profile
  Given I am anywhere on the system,
  When I click on one of my team's name,
  Then I see the team's details (a.k.a. profile), i.e. it's name, location and sport


  Scenario: AC2: Unable to edit the team details while on the view team profile page
  Given I am on my team details page,
  When I see the team's details (a.k.a. profile), i.e. it's name, location and sport
  Then I cannot edit any of the details that are shown to me.


    # Enter steps here