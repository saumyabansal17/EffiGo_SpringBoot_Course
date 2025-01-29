package com.example.effigo.springboot.jpa_hibernate.course.jpa;

import org.springframework.stereotype.Repository;

import com.example.effigo.springboot.jpa_hibernate.course.Course;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class CourseJpaRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public void insert(Course course) {
		entityManager.merge(course);
	}
	
	public void delete(int id) {
		Course c= entityManager.find(Course.class, id);
		entityManager.remove(c);
		
	}
	
	public Course findById(int id) {
		return entityManager.find(Course.class, id);
	}
	
}
