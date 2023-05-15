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

  Scenario: AC4 - Given I am on the lost password form, when I enter an email that is known to the system, then a confirmation message tells me that an email was sent to the address if it was recognised, and an email is sent to the email address with a link containing a unique reset token to update the password of the profile associated to that email.
    Given I am on the lost password form
    When I enter a email known to the system
    Then An email is sent with a unique link to update the password of the associated email


# Scenario: AC5 - Given I received an email to reset my password, when I go to the given URL passed in the link, then I am asked to supply a new password with “new password” and “retype password” fields.
#  Given I received a reset password email
#  When I go to the URL in the link
#  Then I see the reset password page

#  Scenario: AC6 - Given I am on the reset password form, and I enter two different passwords in “new” and “retype password” fields, when I hit the save button, then an error message tells me the passwords do not match.
#    Given I am on the reset password page
#    When I enter two different passwords in "password" and "confirm password" and click save
#    Then An error message tells me the passwords do not match

