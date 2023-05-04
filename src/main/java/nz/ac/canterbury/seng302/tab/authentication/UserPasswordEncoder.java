package nz.ac.canterbury.seng302.tab.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * <p>
 * This bean defines the default "password encoder" of the project.
 * By default this is <em>Bcrypt</em>.
 * </p>
 * To use, simply
 * {@code @Autowired
 * private PasswordEncoder passwordEncoder
 * }
 * in your class.
 */
@Configuration
public class UserPasswordEncoder {

    /**
     * <strong>Please, only autowire this class. If you can read this,
     * then you're calling it directly
     * </strong>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
