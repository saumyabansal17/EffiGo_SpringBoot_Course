package com.example.effigo.springboot.jpa_hibernate.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.effigo.springboot.jpa_hibernate.course.jdbc.CourseJdbcRepository;
import com.example.effigo.springboot.jpa_hibernate.course.jpa.CourseJpaRepository;
import com.example.effigo.springboot.jpa_hibernate.course.springdata.jpa.CouresSpringDataJpaRepositroy;

@Component
public class CourseCmdRunner implements CommandLineRunner{
	@Autowired
	private CouresSpringDataJpaRepositroy repository;
	
	@Override
	public void run(String... args) throws Exception{
//		
//		repository.insert(new Course(3,"Java","Telusko"));
//		repository.save(new Course(3,"Spring","Telusko"));
//		
//		repository.deleteById(1);
		
		System.out.println(repository.findById(2));
		System.out.println(repository.findByAuthor("Telusko"));
		System.out.println(repository.findAll());
	}
}
