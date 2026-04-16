Feature: Login flow on the-internet.herokuapp.com
  The same login scenarios expressed in Gherkin for stakeholders who
  prefer a business-language view of the regression suite.

  Background:
    Given I am on the login page

  Scenario: Successful login with the known good credentials
    When I submit username "tomsmith" and password "SuperSecretPassword!"
    Then I should see the secure area
    And the flash message should contain "You logged into a secure area"

  Scenario Outline: Invalid credentials are rejected
    When I submit username "<username>" and password "<password>"
    Then I should remain on the login page
    And the flash message should contain "<expected>"

    Examples:
      | username        | password         | expected                 |
      | tomsmith        | WrongPass123     | Your password is invalid |
      | nobody          | SuperSecret!     | Your username is invalid |
