package com.example.LibraryMS.Authentication;

import com.example.LibraryMS.Response.DefaultResponse;
import com.example.LibraryMS.Utilities.JWTUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.naming.factory.SendMailFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CorporateApiAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTUtility jwtUtility;

    private static final Logger LOGGER = Logger.getLogger(CorporateApiAuthenticationFilter.class.getName());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().contains("api/v2/corporate")) {
            try {
                String authorization = request.getHeader("Authorization");
                String token = null;
                String userName = null;

                if (null != authorization && authorization.startsWith("Bearer ")) {
                    token = authorization.substring(7);
                    userName = jwtUtility.getUsernameFromToken(token);
                }

                if (null != userName && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                }
            } catch (JwtException e) {
                LOGGER.log(Level.SEVERE, "JWT Expired...Session timed out");
                DefaultResponse defaultResponse = new DefaultResponse(401, "Failed", "Session timed out");
                PrintWriter writer = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writer.print(convertObjectToJson(defaultResponse));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

}
