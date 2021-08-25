package com.revature.schoolDatabase.web.filters;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  The CorsFilter class serves mainly to address the following message:
 *
 *  Access to fetch at '<TomCatServer/Servlet>' from origin '<IP>' has been blocked by CORS policy:
 *      Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin'
 *      header is present on the requested resource. If an opaque response serves your needs, set the request's
 *      mode to 'no-cors' to fetch the resource with CORS disabled.
 *
 *  In short, the CorsFilter allows requests to be made to the provided server from an external source, in this case,
 *      our UI.
 *
 *  Specifically, our CorsFilter allows requests of types 'GET, POST, PUT, PATCH, DELETE', with headers specifying
 *      'Content-Type, Authorization', to be made from any source.
 */


@WebFilter("/*")
public class CorsFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        req.setAttribute("filtered", true);
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Expose-Headers", "Authorization");
        chain.doFilter(req, resp);
    }
}
