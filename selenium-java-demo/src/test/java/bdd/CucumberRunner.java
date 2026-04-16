package bdd;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * TestNG-based Cucumber runner. Allure steps are generated via the
 * allure-cucumber7-jvm plugin so the BDD runs land in the same report
 * as the rest of the suite.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"bdd.steps"},
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        }
)
public class CucumberRunner extends AbstractTestNGCucumberTests {
}
