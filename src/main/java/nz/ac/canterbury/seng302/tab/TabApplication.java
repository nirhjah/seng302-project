package nz.ac.canterbury.seng302.tab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * TAB (not that TAB) entry-point
 * Note @link{SpringBootApplication} annotation
 */
@SpringBootApplication
public class TabApplication {
    /**
     * Main entry point, runs the Spring application
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        // Load the .env file
        Dotenv
            .configure()
            .ignoreIfMissing()
            .systemProperties()
            .load();
        SpringApplication.run(TabApplication.class, args);

    }

}
