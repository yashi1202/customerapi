package com.example.customerapi.service;

import java.util.List;
import java.util.UUID;

import com.example.customerapi.dto.CustomerRequest;
import com.example.customerapi.dto.CustomerResponse;

public interface CustomerServiceInterface {
	
	CustomerResponse create(CustomerRequest req);
	
	CustomerResponse getById(UUID id);
	
	CustomerResponse getByEmail(String email);
	
	List<CustomerResponse> findByName(String name);
	
	CustomerResponse update(UUID id, CustomerRequest req);
	
	void  delete(UUID id);
	
	

}

