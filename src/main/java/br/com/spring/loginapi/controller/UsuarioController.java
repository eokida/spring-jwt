package br.com.spring.loginapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.spring.loginapi.model.Usuario;
import br.com.spring.loginapi.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	@Autowired
    private UsuarioService service;
	
    @PostMapping
    public void post(@RequestBody Usuario usuario) {
        service.salvar(usuario);
    }
	
    @GetMapping
    public String get() {
        return "Just Get";
    }
    
    @GetMapping("/{userId}")
	public Usuario getUsuario(@PathVariable("userId") Long userId) {
		return service.getUsuario(userId);
	}
}
