package br.com.spring.loginapi.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.spring.loginapi.dto.Login;
import br.com.spring.loginapi.dto.LoginCadastro;
import br.com.spring.loginapi.dto.Response;
import br.com.spring.loginapi.dto.Sessao;
import br.com.spring.loginapi.model.ERole;
import br.com.spring.loginapi.model.Role;
import br.com.spring.loginapi.model.Usuario;
import br.com.spring.loginapi.repository.RoleRepository;
import br.com.spring.loginapi.repository.UsuarioRepository;
import br.com.spring.loginapi.security.JwtToken;
import br.com.spring.loginapi.security.JwtUtil;
import br.com.spring.loginapi.security.SecurityConfig;
import br.com.spring.loginapi.service.UserDetailsImpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

@RestController
public class LoginController {
	
	private static final Logger logger = LogManager.getLogger(LoginController.class);
	
    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
	AuthenticationManager authenticationManager;
    
    @PostMapping("/login")
    public Sessao logar(@RequestBody Login login){
        Usuario usuario = usuarioRepository.getUsuario(login.getLogin());
        if(usuario!=null) {
            boolean passwordOk = encoder.matches(login.getSenha(), usuario.getSenha());
            if (!passwordOk) {
                throw new RuntimeException("Senha invalida para o login: " + login.getLogin());
            }
            // Retorna objeto Sessao para ser salvo no cliente. Ex: cookie
            Sessao sessao = new Sessao();
            sessao.setLogin(usuario.getEmail());

            JwtToken jwtObject = new JwtToken();
            jwtObject.setIssuedAt(new Date(System.currentTimeMillis()));
            jwtObject.setExpiration((new Date(System.currentTimeMillis() + SecurityConfig.EXPIRATION)));
            jwtObject.setSubject(usuario.getEmail());
            //jwtObject.setRoles(usuario.getRoles());
            sessao.setToken(JwtUtil.createToken(SecurityConfig.PREFIX, SecurityConfig.SECRET_KEY, jwtObject));
            return sessao;
        }else {
            throw new RuntimeException("Erro ao tentar fazer login");
        }
    }
    
    @PostMapping("/signin")
	public ResponseEntity<?> signin(@Valid @RequestBody Login login) {

    	ResponseEntity<?> response;
    	
    	try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getLogin(), login.getSenha()));
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
		
			JwtToken jwtToken = new JwtToken();
            jwtToken.setIssuedAt(new Date(System.currentTimeMillis()));
            jwtToken.setExpiration((new Date(System.currentTimeMillis() + SecurityConfig.EXPIRATION)));
			jwtToken.setRoles(roles);
            jwtToken.setSubject(userDetails.getEmail());

			String jwt = JwtUtil.generateToken(authentication, jwtToken);
			jwtToken.setToken(jwt);

			SecurityContextHolder.getContext().setAuthentication(authentication);

			response = ResponseEntity.ok(jwtToken);
		
    	} catch (BadCredentialsException e) {
    		//throw new BadCredentialsException("Bad credentials for ".concat(login.getLogin()));
            response = ResponseEntity.accepted().body(new Response("Bad credentials for ".concat(login.getLogin())));
        }

		return response;
	}
    
    @PostMapping("/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody LoginCadastro loginCadastro) {
		if (usuarioRepository.existsByEmail(loginCadastro.getEmail())) {
			return ResponseEntity.badRequest().body(new Response("Email already registered"));
		}

		if (usuarioRepository.existsByCelular(loginCadastro.getCelular())) {
			return ResponseEntity.badRequest().body(new Response("Phone already registered"));
		}

		// Create new user's account
		Usuario user = new Usuario();
		user.setCelular(loginCadastro.getCelular());
		user.setDataCadastro(LocalDateTime.now());
		user.setEmail(loginCadastro.getEmail());
		user.setNome(loginCadastro.getNome());
		user.setSenha(encoder.encode(loginCadastro.getSenha()));
		
		ERole eRole = ERole.ROLE_USER;
		if (loginCadastro.getRole()!=null) {
			String roleName = loginCadastro.getRole().stream().findFirst().get();
			if ("ROLE_ADMIN".equals(roleName))
				eRole = ERole.ROLE_ADMIN;
			else
				eRole = ERole.ROLE_MODERATOR;
		}
		
		Role role = roleRepository.findByName(eRole);
		
		Set<Role> roles = new HashSet<>(1);
		roles.add(role);
		user.setRoles(roles);

		logger.info("Saving new user");
		usuarioRepository.save(user);

		return ResponseEntity.ok(new Response("User registered successfully!"));
	}
    
}
