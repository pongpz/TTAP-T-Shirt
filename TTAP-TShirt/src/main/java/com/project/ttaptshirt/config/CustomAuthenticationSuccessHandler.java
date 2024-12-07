package com.project.ttaptshirt.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    protected Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String urlTarget = determineTargetUrl(authentication);
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + urlTarget);
            return;
        }
        logger.info("Redirecting to URL: " + urlTarget);
        redirectStrategy.sendRedirect(request, response, urlTarget);
    }

    public String determineTargetUrl(final Authentication authentication) {
        HashMap<String, String> roleTargetUrlMap = new HashMap<>();
        roleTargetUrlMap.put("ROLE_ADMIN", "/admin/ban-hang");
        roleTargetUrlMap.put("ROLE_EMPLOYEE", "/admin/ban-hang");
        roleTargetUrlMap.put("ROLE_CUSTOMER", "/TTAP/trang-chu");

        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            logger.info("Authority: " + authorityName);
            if (roleTargetUrlMap.containsKey(authorityName)) {
                return roleTargetUrlMap.get(authorityName);
            }
        }
        throw new IllegalStateException();
    }

    public void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
