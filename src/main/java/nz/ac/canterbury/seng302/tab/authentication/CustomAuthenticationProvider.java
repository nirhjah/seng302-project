package nz.ac.canterbury.seng302.tab.authentication;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider() {
        super();
    }

    /**
     * Custom Authentication
     * 
     * @param authentication the authentication request object.
     * @return a UsernamePasswordAuthenticationToken token if the users are valid.
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        String email = String.valueOf(authentication.getName());
        String password = String.valueOf(authentication.getCredentials());

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new BadCredentialsException("Bad Credentials");
        }

        Optional<User> matchingUser = userService.findUserByEmail(email);
        // This filter sweeps both "Username not in database" and "Passwords don't
        // match" into the same error message.
        if (matchingUser.isPresent() && passwordEncoder.matches(password, matchingUser.get().getPassword())) {
            User user = matchingUser.get();
            return new UsernamePasswordAuthenticationToken(
                    user.getEmail(), null, user.getAuthorities());
        }
        throw new BadCredentialsException("Invalid username or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
