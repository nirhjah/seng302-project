package nz.ac.canterbury.seng302.tab.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;


/**
 * Adapted from:
 * <a href="https://codingnconcepts.com/spring-boot/send-email-with-thymeleaf-template/">...</a>
 */
@Configuration
public class EmailTemplateConfig {

    /**
     * Creates a template
     * @return a template engine
     */
    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.addTemplateResolver(emailTemplateResolver());
        springTemplateEngine.addTemplateResolver(genericTemplateResolver());
        return springTemplateEngine;
    }

    public ClassLoaderTemplateResolver genericTemplateResolver() {
        ClassLoaderTemplateResolver generic = new ClassLoaderTemplateResolver();
        generic.setPrefix("/templates/");
        generic.setSuffix(".html");
        generic.setOrder(0);
        generic.setCheckExistence(false);
        return generic;
    }

    /**
     * This is how the email templates are located and loaded for use in emails
     * @return a loader for email resources
     */
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix("/template/mail/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
        emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        emailTemplateResolver.setCacheable(false);
        emailTemplateResolver.setOrder(1);
        return emailTemplateResolver;
    }

}
