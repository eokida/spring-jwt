package br.com.spring.loginapi.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.spring.loginapi.model.Usuario;
import br.com.spring.loginapi.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);
	
	@Autowired
	UsuarioRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

		logger.info("loadUserByUsername, "+login);
		
		Usuario usuario = userRepository.getUsuarioByAnyLogin(login)
				.orElseThrow(() -> new UsernameNotFoundException("Credentials not found for " + login));

		return UserDetailsImpl.create(usuario);
	}

}
