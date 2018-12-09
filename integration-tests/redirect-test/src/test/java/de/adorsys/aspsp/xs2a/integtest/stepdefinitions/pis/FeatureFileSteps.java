package de.adorsys.aspsp.xs2a.integtest.stepdefinitions.pis;

import de.adorsys.aspsp.xs2a.integtest.*;
import org.junit.runner.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;

import java.lang.annotation.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RedirectTestApp.class, loader = SpringBootContextLoader.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public @interface FeatureFileSteps {
}
