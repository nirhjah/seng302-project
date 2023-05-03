Feature: U21 - Confirm registration email

  As Julie, Ashley, Teka, I want to confirm my email address when I register to the system so that my account is more secure.

  Scenario: AC1 - Generate unique registration token when a user clicks on the register an account.
    Given I submit a fully valid registration form
    When I click on register
    Then A confirmation email is sent to my email address
    And A unique registration token is attached to the email in the form of a confirmation link.