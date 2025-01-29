package com.effigo.springboot.web_app.todo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;

//@Controller
@SessionAttributes("name")
public class TodoController {

	private TodoService todoService;
	
	public TodoController(TodoService todo) {
		super();
		this.todoService=todo;
		// TODO Auto-generated constructor stub
	}

	@RequestMapping("list-todos")
	public String listAllTodos(ModelMap model) {
		String username=getUsername(model);
		List<Todo> todos = todoService.findByUsername(username);
		model.addAttribute("todos", todos);
		
		return "listTodos";
	}
	
	@RequestMapping(value="add-todo",method=RequestMethod.GET)
	public String showNewTodos(ModelMap model) {
		String username=getUsername(model);
		Todo todo = new Todo(0, username, "Default Desc", LocalDate.now().plusYears(1), false);
		model.put("todo", todo);
		return "addTodos";
	}
	
	@RequestMapping(value="add-todo",method=RequestMethod.POST)
	public String addNewTodos(ModelMap model,@Valid Todo todo,BindingResult result) {
		
		if(result.hasErrors()) {
			return "addTodos";
		}
		
		String username=getUsername(model);
		todoService.addTodo(username, todo.getDescription(), todo.getTargetDate(), false);
		return "redirect:list-todos";
	}
	
	@RequestMapping("delete-todo")
	public String deleteTodo(@RequestParam int id) {

		todoService.deleteById(id);
		return "redirect:list-todos";
		
	}
	
	@RequestMapping(value="update-todo",method=RequestMethod.GET)
	public String updateTodo(@RequestParam int id,ModelMap model) {
		
		Todo todo = todoService.findById(id);
		model.put("todo", todo);
		return "addTodos";
		
	}
	
	@RequestMapping(value="update-todo",method=RequestMethod.POST)
	public String updateNewTodo(ModelMap model,@Valid Todo todo,BindingResult result) {
		
		if(result.hasErrors()) {
			return "addTodos";
		}
		
		String username=getUsername(model);
		todo.setUsername(username);
		todoService.updateById(todo);
		return "redirect:list-todos";
	}
	
	private String getUsername(ModelMap model) {
		Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
	
}
