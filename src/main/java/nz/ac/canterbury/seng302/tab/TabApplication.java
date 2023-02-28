package nz.ac.canterbury.seng302.tab;

import nz.ac.canterbury.seng302.tab.controller.DemoController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

/**
 * TAB (not that TAB) entry-point
 * Note @link{SpringBootApplication} annotation
 */
@SpringBootApplication
public class TabApplication {

	/**
	 * Main entry point, runs the Spring application
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		new File(DemoController.uploadDirectory).mkdir();
		SpringApplication.run(TabApplication.class, args);
	}

}
