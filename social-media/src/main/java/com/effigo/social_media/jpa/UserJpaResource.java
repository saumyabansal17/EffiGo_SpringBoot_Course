package com.effigo.social_media.jpa;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.effigo.social_media.user.Post;
import com.effigo.social_media.user.User;
import com.effigo.social_media.user.UserDaoService;
import com.effigo.social_media.user.UserNotFoundException;

import jakarta.validation.Valid;

@RestController
public class UserJpaResource {

	private UserRepository userRepository;
	private PostRepository postRepository;



	public UserJpaResource(UserRepository userRepository, PostRepository postRepository) {
			super();
			this.userRepository = userRepository;
			this.postRepository = postRepository;
		}


//	public UserJpaResource(UserRepository userRepository) {
//		super();
//		this.userRepository = userRepository;
//	}
//	

	@GetMapping("/jpa/users")
	public List<User> retrieveAllUsers(){
		return userRepository.findAll();
	}
	
	@GetMapping("/jpa/users/{id}")
	public User retrieveById(@PathVariable int id){
		User user = userRepository.findById(id).get();
		
		if(user==null)
			throw new UserNotFoundException("id:"+id);
		
		return user;
	}
	
	
	@GetMapping("/jpa/users/{id}/posts")
	public List<Post> retrievePostById(@PathVariable int id){
		User user = userRepository.findById(id).get();
		
		if(user==null)
			throw new UserNotFoundException("id:"+id);
		
		return user.getPosts();
	}
	
	@DeleteMapping("/jpa/users/{id}")
	public void deleteById(@PathVariable int id){
		userRepository.deleteById(id);
	}
	
	@PostMapping("/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		
		User savedUser = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
						.path("/{id}")
						.buildAndExpand(savedUser.getId())
						.toUri();   
		
		return ResponseEntity.created(location).build();
	}
	
	@PostMapping("/jpa/users/{id}/posts")
	public ResponseEntity<User> createPost(@PathVariable int id,@Valid @RequestBody Post post) {
		User user = userRepository.findById(id).get();
				
		if(user==null)
			throw new UserNotFoundException("id:"+id);
				
		post.setUser(user);
		Post savedPost= postRepository.save(post);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
						.path("/{id}")
						.buildAndExpand(savedPost.getId())
						.toUri();   
		
		return ResponseEntity.created(location).build();
	}
	
}
