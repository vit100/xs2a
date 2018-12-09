package de.adorsys.aspsp.xs2a.integtest;

import cucumber.api.*;
import cucumber.api.junit.*;
import de.adorsys.aspsp.xs2a.integtest.utils.*;
import de.adorsys.psd2.aspsp.profile.domain.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.client.*;

import javax.annotation.*;


@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "de.adorsys.aspsp.xs2a.integtest.stepdefinitions",
    format = {"json:cucumber-report/cucumber.json"},
    tags = {"~@ignore", "~@TestTag"})
public class CucumberRedirectIT {


}

