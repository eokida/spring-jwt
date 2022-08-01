package br.com.spring.loginapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.spring.loginapi.model.BancoAgencia;
import br.com.spring.loginapi.service.BancoAgenciaService;

@RestController
@RequestMapping("/bancosAgencias")
public class BancoAgenciaController {

	@Autowired
    private BancoAgenciaService service;
	
    @GetMapping
    public String get() {
        return "Just Get BancoAgenciaController";
    }
    
    @GetMapping("/{bancoCd}")
	public List<BancoAgencia> getBancoAgencias(@PathVariable("bancoCd") String bancoCd) {
		return service.getBancoAgencias(bancoCd);
	}
}
