package de.adorsys.aspsp.xs2a.integtest;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import cucumber.api.cli.Main;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "de.adorsys.aspsp.xs2a.integtest.stepdefinitions",
    format = {"json:cucumber-report/cucumber.json"},
    tags = {"~@ignore", "~@TestTag"})
public class CucumberTest {
    public static void main(String[] args) throws Throwable {

        JUnitCore.main("CucumberTest.class");
    }
}

