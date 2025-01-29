package com.example.effigo.spring_boot_starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyConfigController {

	@Autowired
	private CurrencyServiceConfig configuration; 
	
	@RequestMapping("/currency-config")
	public CurrencyServiceConfig retrieveAll() {
		return configuration;
	}
}
