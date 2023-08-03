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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



/**
 * Custom Security Configuration
 * Adapted from Morgan English's Security Handout
 * <a href="https://eng-git.canterbury.ac.nz/men63/spring-security-example-2023/">...</a>
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
     * create a custom success url handler which sets
     * the default url to the user profile when logging in
     * @return the successHandler which spring security configuration can use
     * to redirect user to the url with successful authentication
     */

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setUseReferer(false);
        successHandler.setDefaultTargetUrl("/user-info/self");
        return successHandler;
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
        http.authorizeHttpRequests(auth -> auth.requestMatchers(AntPathRequestMatcher.antMatcher("/geocode/autocomplete"),
                        AntPathRequestMatcher.antMatcher("/h2/**"), AntPathRequestMatcher.antMatcher("/resources/**"),
                        AntPathRequestMatcher.antMatcher("/static/**"), AntPathRequestMatcher.antMatcher("/css/**"),
                        AntPathRequestMatcher.antMatcher("/js/**"), AntPathRequestMatcher.antMatcher("/image/**"), AntPathRequestMatcher.antMatcher("/mail/**")).permitAll())
                .headers(headers -> headers.frameOptions().disable())
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2/**"),AntPathRequestMatcher.antMatcher("/geocode/autocomplete")))
                .authorizeHttpRequests()
                // accessible to anyone
                .requestMatchers("/", "/register", "/login", "/home",
                "/geocode/autocomplete", "/lost-password", "/reset-password", "/confirm")
                .permitAll()
                // Only allow admins to reach the "/admin" and "/populate_database" page
                .requestMatchers("/admin", "/populate_database")
                // note we do not need the "ROLE_" prefix as we are calling "hasRole()"
                .hasRole("ADMIN")
                // Any other request requires authentication
                .anyRequest()
                .authenticated()
                .and()
                // Define logging in, a POST "/login" endpoint now exists under the hood, after login redirect to user page
                .formLogin().loginPage("/login").loginProcessingUrl("/login").successHandler(authenticationSuccessHandler()).failureUrl("/login?error=true")
                .and()
                // Define logging out, a POST "/logout" endpoint now exists under the hood, redirect to "/login", invalidate session and remove cookie
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login").invalidateHttpSession(true).deleteCookies("JSESSIONID");
        return http.build();

    }

}
