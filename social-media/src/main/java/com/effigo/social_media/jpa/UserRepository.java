package com.effigo.social_media.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.effigo.social_media.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}