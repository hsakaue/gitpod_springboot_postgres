package com.example.demo;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.TestUser;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface TestUserRepository extends CrudRepository<TestUser, Integer> {

}