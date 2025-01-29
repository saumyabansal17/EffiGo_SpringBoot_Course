package com.example.effigo.springboot.jpa_hibernate.course.springdata.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.effigo.springboot.jpa_hibernate.course.Course;

public interface CouresSpringDataJpaRepositroy extends JpaRepository<Course, Integer> {
	List<Course> findByAuthor(String Author);
	List<Course> findByTitle(String title);
}
