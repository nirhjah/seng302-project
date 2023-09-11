package nz.ac.canterbury.seng302.tab.authentication;

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

import nz.ac.canterbury.seng302.tab.controller.LoginController;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;



/**
 * Custom Security Configuration
 * Adapted from
 * <a href="https://eng-git.canterbury.ac.nz/men63/spring-security-example-2023/">Morgan English's Security Handout</a>
 */
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "com.baeldung.security")
public class SecurityConfiguration {

    public static final String DEFAULT_LOGIN_REDIRECT_URL = "/user-info/self";
    public static final String LOGIN_URL = "/login";

    /**
     * Create an Authentication Manager
     * @param http http security config
     * @return the created auth manager
     * @throws Exception when unable to create auth manager
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http, CustomAuthenticationProvider authProvider) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }

    /**
     * Configures the URL you're redirected to after login
     * <ul>
     *  <li>IF you clicked the login button, then after login you're redirected to the default URL</li>
     *  <li>IF you were forced to login after accessing a URL that requires it, you'll be redirect back there</li>
     * </ul>
     * @return the successHandler which spring security configuration can use
     * to redirect user to the url with successful authentication
     * @see ContinueEntryPoint#determineUrlToUseForThisRequest()
     * @see LoginController#form()
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        // Default return URL if you simply clicked 'login'
        successHandler.setDefaultTargetUrl(DEFAULT_LOGIN_REDIRECT_URL);
        successHandler.setAlwaysUseDefaultTargetUrl(false);
        /** 
         * If you were FORCED to login, then said URL will be added as a parameter by ContinueEntryPoint,
         * saved as a session variable by LoginController, and used by this to redirect you back there.
         */
        successHandler.setTargetUrlParameter(ContinueEntryPoint.LOGIN_REDIRECT_URL_PARAM);
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
        http
            .authorizeHttpRequests(auth -> auth.requestMatchers(
                AntPathRequestMatcher.antMatcher("/favicon.ico"),
                AntPathRequestMatcher.antMatcher("/geocode/autocomplete"),
                AntPathRequestMatcher.antMatcher("/h2/**"),
                AntPathRequestMatcher.antMatcher("/resources/**"),
                AntPathRequestMatcher.antMatcher("/static/**"),
                AntPathRequestMatcher.antMatcher("/css/**"),
                AntPathRequestMatcher.antMatcher("/js/**"),
                AntPathRequestMatcher.antMatcher("/image/**"),
                AntPathRequestMatcher.antMatcher("/mail/**")
            ).permitAll())
            .headers(headers -> headers.frameOptions().disable())
            .exceptionHandling(exceptionHandling -> exceptionHandling
                            .authenticationEntryPoint(new ContinueEntryPoint(LOGIN_URL)))
            .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2/**"), AntPathRequestMatcher.antMatcher("/geocode/autocomplete")))
            .authorizeHttpRequests()
            // accessible to anyone
            .requestMatchers("/", "/register", LOGIN_URL, "/home",
                    "/geocode/autocomplete", "/lost-password", "/reset-password", "/confirm")
            .permitAll()
            // Only Federation Managers (maybe admins) can access this
            .requestMatchers("/create-competition", "/inviteToFederationManager")
            .hasRole(AuthorityType.FEDERATION_MANAGER.name())
            // Only allow admins to reach the "/admin" and "/populate_database" page
            .requestMatchers("/admin", "/populate_database")
            // note we do not need the "ROLE_" prefix as we are calling "hasRole()"
            .hasRole(AuthorityType.ADMIN.name())
            // Any other request requires authentication
            .anyRequest()
            .authenticated()
            .and()
            // Define logging in, a POST "/login" endpoint now exists under the hood, after login redirect to user page
            .formLogin(login -> login
                                .loginPage(LOGIN_URL)
                                .loginProcessingUrl(LOGIN_URL)
                                .successHandler(authenticationSuccessHandler())
                                .failureUrl(LOGIN_URL + "?error")
                                )
            // Define logging out, a POST "/logout" endpoint now exists under the hood, redirect to "/login", invalidate session and remove cookie
            .logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl(LOGIN_URL)
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID"));
        return http.build();

    }

}
