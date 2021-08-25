package com.revature.schoolDatabase.web.filters;

import com.revature.schoolDatabase.web.dtos.Principal;
import com.revature.schoolDatabase.web.util.security.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class AuthFilter extends HttpFilter {

    private final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    private final JwtConfig jwtConfig;

    public AuthFilter(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        parseToken(req);
        chain.doFilter(req, resp);
    }

    public void parseToken(HttpServletRequest req) {

        try {

            String header = req.getHeader(jwtConfig.getHeader());

            System.out.println("Header value: " + header);

            if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
                System.out.println("Request originates from an unauthenticated source.");
                logger.warn("Request originates from an unauthenticated source.");
                return;
            }

            String token = header.replaceAll(jwtConfig.getPrefix(), "");

            Claims jwtClaims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();

            req.setAttribute("principal", new Principal(jwtClaims));
            System.out.println("Principal added as attribute to request!");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
