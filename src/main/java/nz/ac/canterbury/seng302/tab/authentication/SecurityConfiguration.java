package nz.ac.canterbury.seng302.tab.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



/**
 * Custom Security Configuration
 * Adapted from Morgan English's Security Handout
 * https://eng-git.canterbury.ac.nz/men63/spring-security-example-2023/
 */
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "com.baeldung.security")
public class SecurityConfiguration {

    /**
     * Custom Authentication Manager
     * {@Link CustomAuthenticationProvider}
     */
    @Autowired
    private CustomAuthenticationProvider authProvider;

    /**
     * Create an Authentication Manager
     * @param http http security config
     * @return the created auth manager
     * @throws Exception when unable to create auth manager
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }


    /**
     * filters requests being made on the website
     * @param http http security config
     * @return custom security filter chain
     * @throws Exception thrown if the filter chain can't be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Allow h2 console through security. Note: Spring 6 broke the nicer way to do this (i.e. how the authorisation is handled below)
        // See https://github.com/spring-projects/spring-security/issues/12546
        http.authorizeHttpRequests(auth -> auth.requestMatchers(AntPathRequestMatcher.antMatcher("/geocode/autocomplete"),AntPathRequestMatcher.antMatcher("/h2/**"), AntPathRequestMatcher.antMatcher("/resources/**"), AntPathRequestMatcher.antMatcher("/static/**"), AntPathRequestMatcher.antMatcher("/css/**"), AntPathRequestMatcher.antMatcher("/js/**"), AntPathRequestMatcher.antMatcher("/image/**")).permitAll())
                .headers(headers -> headers.frameOptions().disable())
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2/**"),AntPathRequestMatcher.antMatcher("/geocode/autocomplete")))
                .authorizeHttpRequests()
                // accessible to anyone
                .requestMatchers("/", "/register", "/login", "/demo", "/populate_database", "/home", "/geocode/autocomplete", "/forgot-password", "/reset-password")
                .permitAll()
                // Only allow admins to reach the "/admin" page
                .requestMatchers("/admin")
                // note we do not need the "ROLE_" prefix as we are calling "hasRole()"
                .hasRole("ADMIN")
                // Any other request requires authentication
                .anyRequest()
                .authenticated()
                .and()
                // Define logging in, a POST "/login" endpoint now exists under the hood, after login redirect to user page
                .formLogin().loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/user-info/self").failureUrl("/login?error=true")
                .and()
                // Define logging out, a POST "/logout" endpoint now exists under the hood, redirect to "/login", invalidate session and remove cookie
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login").invalidateHttpSession(true).deleteCookies("JSESSIONID");
        return http.build();

    }
}
