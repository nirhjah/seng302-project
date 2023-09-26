@home_page
  Feature: U42 Home Page
    Scenario: Users can see the teams they are apart of on the home screen
    Given I am logged in
    And I am apart of at least one team
    When I am viewing the home page
    Then I can see the team(s) that I am apart of
    And I can click on each one to be taken to the team profile page


    Scenario: Users can see the their team’s upcoming activities on the home screen
      Given I am logged in
      And I am apart of at least one team
      And that team has an upcoming activity
      When I am viewing the home page
      Then I can see an overview of details for the upcoming activity
      And I can click on the activity to be taken to the view activity page


      Scenario: Users can see the their personal upcoming activities on the home screen
        Given I am logged in
        And I have created an upcoming personal activity
        When I am viewing the home page
        Then I can see an overview of details for the upcoming activity
        And I can click on the activity to be taken to the view activity page
