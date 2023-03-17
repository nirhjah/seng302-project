package nz.ac.canterbury.seng302.tab.authentication;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    public CustomAuthenticationProvider()
    {
        super();
    }

    /**
     * Custom Authentication
     * @param authentication the authentication request object.
     * @return a UsernamePasswordAuthenticationToken token if the users are valid.
     */
    @Override
    public Authentication authenticate(Authentication authentication)
    {
        String email = String.valueOf(authentication.getName());
        String password = String.valueOf(authentication.getCredentials());

        if (email == null || email.isEmpty() || password == null || password.isEmpty())
        {
            throw new BadCredentialsException("Bad Credentials");
        }

        User u = userService.getUserByEmailAndPassword(email, password);
        if (u == null)
        {
            throw new BadCredentialsException("Invalid username or password");
        }
        // Because the username is their email, which they can change, we instead set
        // their principal to their database ID, which can't change.
        return new UsernamePasswordAuthenticationToken(u.getUserId(), null, u.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
