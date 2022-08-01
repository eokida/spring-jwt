package br.com.spring.loginapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.spring.loginapi.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("select U from Usuario U where U.email=(:login) or U.celular=(:login)")
    public Usuario getUsuario(@Param("login") String login);
    
    @Query("select U from Usuario U where U.email=(:login) or U.celular=(:login)")
    public Optional<Usuario> getUsuarioByAnyLogin(@Param("login") String login);
    
    Boolean existsByEmail(String email);
    
    Boolean existsByCelular(String celular);
	
}
