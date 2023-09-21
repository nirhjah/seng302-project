Feature: U23 - Reset Password
  Scenario: AC1 - Given I am on the login page, when I hit the “lost password” button, then I see a form asking me for my email address.
    Given I am on the login page
    When I hit the lost password button
    Then I see a form asking me for my email address

  Scenario: AC2 - Given I am on the lost password form, when I enter an email with an invalid format, then an error message tells me the email address is invalid.
    Given I am on the lost password form
    When I enter an email with invalid format
    Then An error message tells me the email address is invalid

  Scenario: AC3 - Given I am on the lost password form, when I enter a valid email that is not known to the system, then a confirmation message tells me that an email was sent to the address if it was recognised.
    Given I am on the lost password form
    When I enter a valid email that is not known to the system
    Then A confirmation message tells me that an email was sent to the address if it was recognised
