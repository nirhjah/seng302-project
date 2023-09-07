package nz.ac.canterbury.seng302.tab.authentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>Handles redirecting to login if you're not signed in.</p> 
 * 
 * Code adapted from https://stackoverflow.com/a/34091221
 * @author Rob Winch
 */
public class ContinueEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public static final String LOGIN_REDIRECT_URL_PARAM = "continue";

    public ContinueEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    /**
     * <p>If you were redirected to login because you're signed out, the page you were just on is added to the login URL.</p>
     * 
     * <p>This will eventually be picked up by {@link SecurityConfiguration#authenticationSuccessHandler()} to redirect you back.</p>
     * 
     * Example: Visiting <code>/someEndpoint</code> will redirect you to <code>/login?continue=/someEndpoint</code>
     * 
     * @return The login URL, with the page you were just on added as a parameter
     */
    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
        String continueParamValue = UrlUtils.buildRequestUrl(request);
        String redirect = super.determineUrlToUseForThisRequest(request, response, exception);
        return UriComponentsBuilder.fromPath(redirect).queryParam(LOGIN_REDIRECT_URL_PARAM, continueParamValue).toUriString();
    }
    
}
