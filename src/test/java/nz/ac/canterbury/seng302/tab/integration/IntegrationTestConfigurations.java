package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import nz.ac.canterbury.seng302.tab.TabApplication;
import org.springframework.test.context.TestPropertySource;
/**
 * Implementation from Playwright Example Repository provided by teaching team
 *
 * Integration tests test the applications function, including the application context.
 * The annotations on this file tell cucumber how the context of the integration tests are to be setup. You do not need
 * to know how they work, just that they set up the application to run in the tests.
 * The @CucumberContextConfiguration annotation is required to provide the context of the tests, in this case the spring
 * context. This is so that the glue can argument can find these files, and understand how the tests are to be run.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = TabApplication.class)
@AutoConfigureMockMvc
@CucumberContextConfiguration
public class IntegrationTestConfigurations {
}
