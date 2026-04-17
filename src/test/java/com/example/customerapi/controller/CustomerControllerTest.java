package com.example.customerapi.controller;

import com.example.customerapi.dto.CustomerRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.service.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
	@MockBean
    private CustomerServiceImpl service;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerResponse sampleResponse() {
        return CustomerResponse.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .annualSpend(BigDecimal.valueOf(5000))
                .lastPurchaseDate(OffsetDateTime.now())
                .build();
    }

    @Test
    void testCreateCustomer() throws Exception {
        CustomerRequest request = new CustomerRequest("John Doe", "john@example.com", BigDecimal.valueOf(5000), OffsetDateTime.now());
        CustomerResponse response = sampleResponse();

        Mockito.when(service.create(any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testGetById() throws Exception {
        CustomerResponse response = sampleResponse();
        UUID id = response.getId();

        Mockito.when(service.getById(eq(id))).thenReturn(response);

        mockMvc.perform(get("/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testGetByEmail() throws Exception {
        CustomerResponse response = sampleResponse();

        Mockito.when(service.getByEmail(eq("john@example.com"))).thenReturn(response);

        mockMvc.perform(get("/customers").param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testGetByName() throws Exception {
        CustomerResponse response = sampleResponse();

        Mockito.when(service.findByName(eq("John"))).thenReturn(List.of(response));

        mockMvc.perform(get("/customers").param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void testGetByNameNotFound() throws Exception {
        Mockito.when(service.findByName(eq("Unknown"))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers").param("name", "Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetByQueryBadRequest() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Query param 'email' or 'name' is required"));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        CustomerRequest request = new CustomerRequest("Jane Doe", "jane@example.com", BigDecimal.valueOf(2000), OffsetDateTime.now());
        CustomerResponse response = sampleResponse();

        Mockito.when(service.update(eq(response.getId()), any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(put("/customers/{id}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe")); // sampleResponse name
    }

    @Test
    void testDeleteCustomer() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/customers/{id}", id))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(eq(id));
    }
}
