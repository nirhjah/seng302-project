Feature: U21 - Confirm registration email
  Scenario: AC1 - Given I submit a fully valid registration form, when I click on register, then a confirmation email is sent to my email address, and a unique registration token is attached to the email in the form of a confirmation link.
    When I submit a valid form on the register page
    Then I receive an email containing a valid registration link

#  Scenario: AC2 - Given a confirmation link has been created for a new user, when two hours have passed after the confirmation link was created, then the token and account are deleted.
  #  Not going to test this one, due to time constraints. Will favour manual tests instead.

  Scenario: AC3 - Given I received a registration link with a unique token, when I want to log for the first time on the system, then I must use the confirmation link to access to the system.
    Given there is a valid registration link
    When I click on the registration link
    Then I am redirected to the login page and the account is activated

  Scenario: AC4 - Given I have accessed the system with the URL of the confirmation link, when the system validates the confirmation link successfully, then I am redirected to the login page, and a confirmation message tells me that my account is activated, and the confirmation token is not usable anymore.
    Given there is a valid registration link
    When I click on the registration link
    Then I am redirected to the login page and the account is activated
    When I click on the registration link
    Then I am redirected to NOT FOUND page

  Scenario: AC5 - Given I use an invalid confirmation link, when the system detects the invalid link, then the system sends me to a generic “Not Found” page (i.e. HTTP 404).
    Given there is not a valid registration link
    When I click on the registration link
    Then I am redirected to NOT FOUND page

