package org.sec.secureapp.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String msg = "Username or Password is wrong";

        if (exception instanceof DisabledException) {
            msg = "DisabledException";
        } else if (exception instanceof CredentialsExpiredException) {
            msg = "CredentialsExpiredException";
        } else if (exception instanceof BadCredentialsException) {
            msg = "BadCredentialsException";
        }
        System.out.println(msg);

        setDefaultFailureUrl("/user/login?error=true&exception=" + msg);

        super.onAuthenticationFailure(request, response, exception);
    }
}
