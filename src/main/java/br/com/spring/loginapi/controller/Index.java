package br.com.spring.loginapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Index {
	
	// public class IndexController shows end point "index-controller" at swagger-ui
	
	@GetMapping
    public String index(){
        return "End point inicial";
    }
	
}
