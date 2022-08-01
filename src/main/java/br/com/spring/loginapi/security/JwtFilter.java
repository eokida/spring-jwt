package br.com.spring.loginapi.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JwtFilter extends OncePerRequestFilter {

	private static final Logger logger = LogManager.getLogger(JwtFilter.class);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
		String url = request.getRequestURI();
		String method = request.getMethod();
		logger.info(method +": "+ url);
		
		//obtem o token da request com AUTHORIZATION
        String token = JwtUtil.extractToken(request);
        
        // validando a integridade do token
        try {
        	
            if(token!=null && !token.isEmpty()) {
                JwtToken jwtToken = JwtUtil.validate(token);
                logger.info(jwtToken.getSubject());
                
                List<SimpleGrantedAuthority> authorities = authorities(jwtToken.getRoles());
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(null, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } else {
                SecurityContextHolder.clearContext();
            }
            
            filterChain.doFilter(request, response);
            
        } catch (ExpiredJwtException e) {
            //e.printStackTrace();
            writeResponse(response, request, e.getMessage());
            
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            //e.printStackTrace();
            writeResponse(response, request, e.getMessage());
        }
		
	}

	private List<SimpleGrantedAuthority> authorities(List<String> roles) {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		//return null;
    }
	
	private void writeResponse(HttpServletResponse response, HttpServletRequest request, String message) {
		
		try {
			
			response.setStatus(HttpStatus.FORBIDDEN.value());
			
			final Map<String, Object> body = new HashMap<>(4);
			body.put("status", HttpServletResponse.SC_FORBIDDEN);
			body.put("error", "Forbidden");
			body.put("message", message);
			body.put("path", request.getServletPath());
	
			final ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getOutputStream(), body);
		
		 } catch (IOException e) {
			logger.error("IOException: {}", e);
		}
		
	}
	
}
