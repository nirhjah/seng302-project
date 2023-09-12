Feature: U3 - View User Profile

  Scenario: AC1 - Can see the User Profile
  Given I am logged in,
  When I click on the my profile button,
  Then I see all my details

  Scenario: AC2 - Can't directly edit details on User Profile
  Given I am on my user profile page,
  Then I see all my details
  And I cannot edit any of my details that are shown to me


    # Enter steps here