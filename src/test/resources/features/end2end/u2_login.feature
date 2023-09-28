Feature: U2 - Login
  As Teka, I want to log into the system so that I can have a personalised experience with it and enjoy its features.

#  Scenario: AC1: Given I connect to the system's main URL, when I hit the button to login, then I see a login form
#    Given I have connected to the system's main URL
#    When I hit the button to login
#    Then I see a login form

  # I'm assuming there's no AC2 (Successful login) because it's constantly
  # used during development, and the data persistence issues

  Scenario Outline: AC3:  Given I am on the login form, and I enter a malformed or empty email address, when I hit the login button, then an error message tells me the email address is invalid
    Given I am on the login form
    When I enter an invalid <emailAddress> email and <password> password
    And I hit the login button
    Then I see an error message on the login page telling me the email or password is invalid
    Examples:
      | emailAddress | password   |
      | "test"       | "123"      |
      | ""           | "password" |

  Scenario: AC4: Given I am on the login form, and I enter an email address that is unknown to the system, when I hit the login button, then an error message tells me the email address is unknown or the password is invalid
    Given I am on the login form
    When I enter a email not known to the system
    And I hit the login button
    Then I see an error message on the login page telling me the email or password is invalid

  Scenario Outline: AC5: Given I am on the login form, and I enter an empty password or the wrong password for the corresponding email address, then an error message tells me the email address is unknown or the password is invalid.
    Given I am on the login form
    When I enter an invalid password <password> for an email that is known
    And I hit the login button
    Then I see an error message on the login page telling me the email or password is invalid
    Examples:
      | password   |
      | ""         |
      | "password" |
