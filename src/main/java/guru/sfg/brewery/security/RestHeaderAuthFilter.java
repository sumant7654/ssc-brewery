package guru.sfg.brewery.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class RestHeaderAuthFilter extends AbstractAuthenticationProcessingFilter {

    public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (logger.isDebugEnabled()) {
            logger.debug("Request is to process authentication");
        }
        try {
            Authentication authResult = attemptAuthentication(request, response);

            if (authResult != null) {
                successfulAuthentication(request, response, chain, authResult);
            } else {
                chain.doFilter(request, response);
            }
        }catch (AuthenticationException exception){
            log.error("Authentication Failed : "+exception);
            unsuccessfulAuthentication(request, response, exception);
        }


    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        String userName = getUserName(httpServletRequest);
        String password = getPassword(httpServletRequest);

        if (userName == null) {
            userName = "";
        }
        if (password == null) {
            password = "";
        }
        log.warn("Authenticating User::" + userName);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
        if (!StringUtils.isEmpty(userName)) {
            return getAuthenticationManager().authenticate(token);
        } else {
            return null;
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        if (log.isDebugEnabled()) {
            log.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        SecurityContextHolder.clearContext();
        if (log.isDebugEnabled()) {
            log.debug("Authentication request failed: " + failed.toString(), failed);
            log.debug("Updated SecurityContextHolder to contain null Authentication");
        }
        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
       // this.rememberMeServices.loginFail(request, response);
       // this.failureHandler.onAuthenticationFailure(request, response, failed);
    }



    private String getPassword(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("Api-Secret");
    }

    private String getUserName(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("Api-Key");
    }
}
