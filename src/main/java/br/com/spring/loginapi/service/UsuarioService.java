package br.com.spring.loginapi.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.spring.loginapi.model.Usuario;
import br.com.spring.loginapi.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository repository;
	
    @Autowired
    private PasswordEncoder encoder;
	
	public void salvar(Usuario usuario) {
		
		String passw = usuario.getSenha();
		usuario.setSenha(encoder.encode(passw));
		usuario.setDataCadastro(LocalDateTime.now());
		repository.save(usuario);
	}
	
	public Usuario getUsuario(Long id) {
		Optional<Usuario> usuario = repository.findById(id);
		if (!usuario.isPresent())
			return null;
		else
			return usuario.get();
	}

}
