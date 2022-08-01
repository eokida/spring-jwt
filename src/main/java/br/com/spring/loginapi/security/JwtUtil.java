package br.com.spring.loginapi.security;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import br.com.spring.loginapi.service.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JwtUtil {

	public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String ROLES_AUTHORITIES = "authorities";

    public static String createToken(String prefix, String key, JwtToken jwtObject) {
        //String token = Jwts.builder().setSubject(jwtObject.getSubject()).setIssuedAt(jwtObject.getIssuedAt()).setExpiration(jwtObject.getExpiration())
          //      .claim(ROLES_AUTHORITIES, checkRoles(jwtObject.getRoles())).signWith(SignatureAlgorithm.HS512, key).compact();
        String token = Jwts.builder().setSubject(jwtObject.getSubject()).setIssuedAt(jwtObject.getIssuedAt()).setExpiration(jwtObject.getExpiration()).signWith(SignatureAlgorithm.HS512, key).compact();
        return prefix + " " + token;
    }
    
    public static JwtToken validate(String token, String prefix, String key) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException {
        JwtToken object = new JwtToken();
        token = token.replace(prefix, "");
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody(); // this validates
        object.setSubject(claims.getSubject());
        object.setExpiration(claims.getExpiration());
        object.setIssuedAt(claims.getIssuedAt());
        object.setRoles((List) claims.get(ROLES_AUTHORITIES));
        return object;
    }
    
    public static JwtToken validate(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException {
    	JwtToken jwtToken = new JwtToken();
        Claims claims = Jwts.parser().setSigningKey(SecurityConfig.SECRET_KEY).parseClaimsJws(token).getBody(); // this validates
        jwtToken.setSubject(claims.getSubject());
        jwtToken.setExpiration(claims.getExpiration());
        jwtToken.setIssuedAt(claims.getIssuedAt());
        jwtToken.setRoles((List) claims.get(ROLES_AUTHORITIES));
        return jwtToken;
    }
    
    private static List<String> checkRoles(List<String> roles) {
        return roles.stream().map(s -> "ROLE_".concat(s.replaceAll("ROLE_",""))).collect(Collectors.toList());
    }

    public static String generateToken(Authentication authentication, JwtToken jwtToken) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		return Jwts.builder().setSubject((userPrincipal.getEmail())).setIssuedAt(jwtToken.getIssuedAt()).setExpiration(jwtToken.getExpiration())
				.claim(ROLES_AUTHORITIES, jwtToken.getRoles())
				.signWith(SignatureAlgorithm.HS512, SecurityConfig.SECRET_KEY).compact();
	}
    
    public static String extractToken(HttpServletRequest request) {
		String headerAuth = request.getHeader(HEADER_AUTHORIZATION);

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(SecurityConfig.PREFIX))
			return headerAuth.substring(7, headerAuth.length());

		return null;
	}
}
