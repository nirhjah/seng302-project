package nz.ac.canterbury.seng302.tab.authentication;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AutoLogin {
    /**
     * <p>
     * <strong>Use this method sparingly</strong>
     * </p>
     * Logs the given user in, <em>bypassing</em> the authentication chain.
     * <ul>
     * <li>If you want to login normally, use {@link HttpServletRequest#login}</li>
     * <li>Make sure the <code>username</code> matches someone in the user database.
     * <em>This</em> won't check it, but some controllers do.
     * fail.</li>
     * </ul>
     * 
     * @param username    The username of the person we're logging in.
     * @param authorities The roles this user will have.
     * @param request     Your controller's request object, we bind the login to
     *                    this.
     */
    public void forceLogin(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        // Create a new Authentication with Username and Password.
        // Normally you'd need to authenticate this, but this requires a password, which
        // is hashed.
        Authentication token = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(token);
        // Add the token to the request session (needed so the authentication can be
        // properly used)
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
    }
}
