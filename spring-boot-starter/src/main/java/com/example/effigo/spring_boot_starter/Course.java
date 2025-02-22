package com.example.effigo.spring_boot_starter;

public class Course {

	int id;
	String title;
	String author;
	
	public Course(int id, String title, String author) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	@Override
	public String toString() {
		return "Course [id=" + id + ", title=" + title + ", author=" + author + "]";
	}
	
	
	
}
