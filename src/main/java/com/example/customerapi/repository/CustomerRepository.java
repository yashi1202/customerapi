package com.example.customerapi.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.customerapi.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID>{
	 Optional<Customer> findByName(String name);
	   
	 Optional<Customer> findByEmail(String email);
	 
	 boolean existsByEmail(String email);

	Optional<Customer> findByEmailIgnoreCase(String email);

	List<Customer> findByNameContainingIgnoreCase(String name);

}

