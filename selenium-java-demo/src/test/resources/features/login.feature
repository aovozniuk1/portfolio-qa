Feature: Login on the-internet.herokuapp.com
  Business-language mirror of the TestNG login suite. Runs with
  `mvn -Pbdd test` or via the CucumberRunner.

  Background:
    Given I am on the login page

  Scenario: Valid credentials
    When I log in with "tomsmith" and "SuperSecretPassword!"
    Then I should be on the secure area
    And the flash message should contain "You logged into a secure area"

  Scenario Outline: Invalid credentials
    When I log in with "<username>" and "<password>"
    Then I should still be on the login page
    And the flash message should contain "<expected>"

    Examples:
      | username | password       | expected                 |
      | tomsmith | WrongPass123   | Your password is invalid |
      | nobody   | SuperSecret!   | Your username is invalid |
