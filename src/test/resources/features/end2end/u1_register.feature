Feature: U1 - Register

  Scenario: AC1 - Register page is shown when register button clicked
    Given I have connected to the system's main URL
    When I hit the button to register
    Then I see a registration form
