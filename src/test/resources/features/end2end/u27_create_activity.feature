Feature: U27 Create Activity: As Julie, Ashley I want to schedule an activity so that my team/ I can gather to do some sport

  Scenario: AC1- Given I am anywhere on the system, when I click on a UI element to create an activity, then I see a form to create an activity
    Given I have connected to the system's main URL
    And I am logged in
    When I hit the button to create an activity
    Then I see the create activity form

    Scenario Outline: AC2 - Given I am on the create activity page and I enter valid values for the team it relates to (optional),
    the activity type, a short description and the activity start and end time, when I hit the create activity button,
    then an activity is created into the system and I see the details of the activity
      Given I am on the create activity page
      And I am logged in
      And I manage at least 1 team
      When I select <activityType> activity type, <team> team, <desc> decscription, <start> start time, <end> end time
      And I press submit
      Then I am taken to "My Activites" page
      Examples:
      |activityType|team|desc|start|end|
      |Game        |1   |"A game with the team"| "02-02-2024 04:30"|"02-02-2024 06:30"|
      |Other        |-1   |"A meeting with the team"| "02-03-2024 04:30"|"02-03-2024 06:30"|