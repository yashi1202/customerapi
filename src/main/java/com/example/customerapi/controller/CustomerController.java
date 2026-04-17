package com.example.customerapi.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.customerapi.dto.CustomerRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.exception.DataNotFoundException;
//import com.example.customerapi.model.Customer;
//import com.example.customerapi.service.CustomerService;
import com.example.customerapi.service.CustomerServiceImpl;

import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/customers", produces = "application/json")
@Validated
@RequiredArgsConstructor
public class CustomerController {
	
	private final CustomerServiceImpl service;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        log.info("HTTP POST /customers body=name:{} email:{}", request.getName(), request.getEmail());
        CustomerResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable("id") UUID id) {
        log.debug("HTTP GET /customers/{}", id);
        return service.getById(id);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable @Email(message="Invalid email format") String email) {
        log.debug("HTTP GET /customers?email={}", email);
        
        if (email != null) {
            return ResponseEntity.ok(service.getByEmail(email));
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Query param 'email' is required");
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<List<CustomerResponse>> getByName(
            @PathVariable @NotBlank(message = "Name cannot be empty") String name) {

        log.debug("HTTP GET /customers/name/{}", name);

        List<CustomerResponse> customers = service.findByName(name);

        if (customers.isEmpty()) {
            throw new DataNotFoundException("Customer not found with name: " + name);
        }

        return ResponseEntity.ok(customers);
    }

    
    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable("id") UUID id, @Valid @RequestBody CustomerRequest request) {
        log.info("HTTP PUT /customers/{}", id);
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") UUID id) {
        log.warn("HTTP DELETE /customers/{}", id);
        service.delete(id);
    }

    
}

