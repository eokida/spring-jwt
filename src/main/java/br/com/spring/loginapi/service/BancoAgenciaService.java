package br.com.spring.loginapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.spring.loginapi.model.BancoAgencia;
import br.com.spring.loginapi.repository.BancoAgenciaRepository;

@Service
public class BancoAgenciaService {
	
	@Autowired
	private BancoAgenciaRepository repository;
	
	public List<BancoAgencia> getBancoAgencias(String banco) {
		return repository.findByBanco(banco);
	}

}
