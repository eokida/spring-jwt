package br.com.spring.loginapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.spring.loginapi.model.BancoAgencia;

@Repository
public interface BancoAgenciaRepository extends JpaRepository<BancoAgencia, Long> {
	
	List<BancoAgencia> findByBanco(String banco);
  
}
