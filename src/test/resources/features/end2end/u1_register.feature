Feature: U1 - Register

  Scenario: AC1 - Register page is shown when register button clicked
    Given I have connected to the system's main URL
    When I hit the button to register
    Then I see a registration form

  Scenario: AC2 - Given I am on the registration form, and I enter valid values for my first name, last name, email address, date of birth, and type the same password twice, when I hit the register button, then a new user is created into the system, and I see my user profile page
    Given I have connected to the system's main URL
    And I hit the button to register
    And I see a registration form
    When I set my first and last name as "Mister" "Kennedy"
    And I set my date of birth as "2005-01-11"
    And I set my email as "las-plagas@example.com"
    And I set my password as "What're Y0u Buying?"
    And Click the next button
    And I set my city and country as "The Island" "Spain"
    And Click the register button
    Then I receive a message stating an email has been sent

  Scenario: AC3 - Given I am on the registration form, and I enter invalid values (i.e. empty strings or non-alphabetical characters except a hyphen or a space) for my first name and/or last name, when I hit the register button, then an error message tells me these fields contain invalid values.
    Given I have connected to the system's main URL
    And I hit the button to register
    And I see a registration form
    When I set my date of birth as "2005-01-11"
    # Illegal exclamation mark
    But I set my first and last name as "Las" "Plagas!"
    And I set my email as "las-plagas@example.com"
    And I set my password as "What're Y0u Buying?"
    And Click the next button
    And I set my city and country as "The Island" "Spain"
    And Click the register button
    Then I see an error message on "last-name" stating "Field contains invalid values"

  Scenario: AC4 - Given I am on the registration form, and I enter a malformed or empty email address, when I hit the register button, then an error message tells me the email address is invalid.
    Given I have connected to the system's main URL
    And I hit the button to register
    And I see a registration form
    When I set my first and last name as "Mister" "Kennedy"
    And I set my date of birth as "2005-01-11"
    # Not an email address
    But I set my email as "Lord Saddler"
    And I set my password as "What're Y0u Buying?"
    And Click the next button
    And I set my city and country as "The Island" "Spain"
    And Click the register button
    Then I see an error message on "email" stating "Must be a well-formed email"

  Scenario: AC5 - Given I am on the registration form, and I enter a date of birth for someone younger than 13 years old, when I hit the register button, then an error message tells me I cannot register into the system because I am too young.
    Given I have connected to the system's main URL
    And I hit the button to register
    And I see a registration form
    When I set my first and last name as "Mister" "Kennedy"
    # You must be 13 years old (as of writing this test)
    But I am only 10 years old
    And I set my email as "las-plagas@example.com"
    And I set my password as "What're Y0u Buying?"
    And Click the next button
    And I set my city and country as "The Island" "Spain"
    And Click the register button
    Then I see an error message on "date-of-birth" stating "You must be at least 13 years old"

  # We can't test this, because on `/test` the database is persistent.
  # Scenario: AC6 - Given I am on the registration form,
  #               and I enter an email address that already exists in the system,
  #               when I hit the register button,
  #               then an error message tells me the email address is already registered.

  Scenario: AC7 - Given I am on the registration form, and I enter two different passwords, when I hit the register button, then an error message tells me the passwords do not match
    Given I have connected to the system's main URL
    And I hit the button to register
    And I see a registration form
    When I set my first and last name as "Mister" "Kennedy"
    And I set my date of birth as "2005-01-11"
    And I set my email as "las-plagas@example.com"
    And I set my password as "What're Y0u Buying?"
    # Different confirm password
    But I set my confirm password as "S0mething_different!"
    And Click the next button
    And I set my city and country as "The Island" "Spain"
    And Click the register button
    Then I see an error message on "confirm-password" stating "Passwords do not match"
