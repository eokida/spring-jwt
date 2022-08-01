package br.com.spring.loginapi.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.spring.loginapi.model.Usuario;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = -1495808557294481450L;

	private Long id;

	private String nome;

	private String email;
	
	private String celular;

	@JsonIgnore
	private String senha;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Long id, String nome, String email, String senha, String celular, Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.celular = celular;
		this.authorities = authorities;
	}

	public static UserDetailsImpl create(Usuario usuario) {
		List<GrantedAuthority> authorities = usuario.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name()))
				.collect(Collectors.toList());
		return new UserDetailsImpl(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getSenha(), usuario.getCelular(), authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return nome;
	}
	
	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getCelular() {
		return celular;
	}

}
