package de.adorsys.aspsp.xs2a.integtest;

import cucumber.api.*;
import cucumber.api.junit.*;
import org.junit.runner.*;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "de.adorsys.aspsp.xs2a.integtest.stepdefinitions",
    format = {"json:cucumber-report/cucumber.json"},
    tags = {"~@ignore", "~@TestTag"})
public class CucumberEmbeddedIT {
}

