package com.effigo.social_media.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.effigo.social_media.user.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {

}
