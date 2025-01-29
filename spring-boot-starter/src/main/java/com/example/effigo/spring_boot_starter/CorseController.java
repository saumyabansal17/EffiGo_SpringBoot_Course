package com.example.effigo.spring_boot_starter;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CorseController {
	
	@RequestMapping("/courses")
	public List<Course> retrieveAll(){
		return Arrays.asList(
				new Course(1,"Java","saumya"),
				new Course(2,"C++","varshita")
				);
	}
}
