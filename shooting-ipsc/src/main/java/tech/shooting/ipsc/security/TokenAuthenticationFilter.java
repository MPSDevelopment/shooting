package tech.shooting.ipsc.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import tech.shooting.commons.utils.HeaderUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class TokenAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired(required = true)
    private TokenUtils tokenUtils;

    //	@Autowired
    //	private IpscSettings settings;

    public TokenAuthenticationFilter () {

        setAuthenticationSuccessHandler((request, response, authentication) -> {
            log.debug("GO SuccessHandler !!!");
            // SecurityContextHolder.getContext().setAuthentication(authentication);
            // authentication.setAuthenticated(false);
            // SecurityContextHolder.getContext().setAuthentication(null);
        });
        setAuthenticationFailureHandler((request, response, authenticationException) -> {
            log.debug("GO FailureHandler !!!");
            response.getOutputStream().print(authenticationException.getMessage());
        });
    }

    @Autowired
    private void setAuthorizationManager (TokenAuthenticationManager manager) {
        super.setAuthenticationManager(manager);
    }

    @Override
    protected boolean requiresAuthentication (HttpServletRequest request, HttpServletResponse response) {
        log.debug("  Start work filter = %s ", request.getRequestURI());
        Authentication authResult = null;
        try {
            authResult = attemptAuthentication(request, response);
        } catch(AuthenticationException failed) {
            try {
                unsuccessfulAuthentication(request, response, failed);
            } catch(IOException e) {
                log.error(" requiresAuthentication %s", e);
            } catch(ServletException e) {
                log.error(" requiresAuthentication %s", e);
            }
        }
        try {
            successfulAuthentication(request, response, null, authResult);
        } catch(IOException e) {
            log.error(" requiresAuthentication %s", e);
        } catch(ServletException e) {
            log.error(" requiresAuthentication %s", e);
        }
        return false;
    }

    @Override
    public Authentication attemptAuthentication (HttpServletRequest request, HttpServletResponse response) {
        log.debug("Start attempt Authentication!!!");
        boolean badCredential = false;
        String token = HeaderUtils.getAuthToken(request);
        if(StringUtils.isBlank(token)) {
            badCredential = true;
        } else if(!tokenUtils.verifyToken(token)) {
            log.error("Bad credentials in token %s", token);
            badCredential = true;
        }
        if(badCredential) {
            TokenAuthentication authentication = new TokenAuthentication(tokenUtils, null);
            authentication.setAuthenticated(false);
            Authentication authenticationEmpty = getAuthenticationManager().authenticate(authentication);
            log.debug("RETURN AUTH EMPTY= %s ", authenticationEmpty);
            return authenticationEmpty;
        }

        TokenAuthentication tokenAuthentication = new TokenAuthentication(tokenUtils, token);
        log.debug("Start creating AUTH ");
        Authentication authentication = getAuthenticationManager().authenticate(tokenAuthentication);
        log.debug("RETURN AUTH = %s ", authentication);
        return authentication;
    }

    @Override
    public void doFilter (ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            super.doFilter(req, res, chain);
            log.debug("DO filter !!! %s ", chain);
        } catch(NullPointerException e) {
            log.error("ERROR in Filter Chain -  %s ", e);
        }
    }

}