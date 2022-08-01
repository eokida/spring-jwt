package br.com.spring.loginapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.spring.loginapi.model.ERole;
import br.com.spring.loginapi.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(ERole name);
	
}
