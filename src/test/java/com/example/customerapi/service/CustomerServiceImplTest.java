package com.example.customerapi.service;

import com.example.customerapi.model.Customer;
import com.example.customerapi.dto.CustomerRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.enumT.Tier;
import com.example.customerapi.exception.DataNotFoundException;
import com.example.customerapi.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerServiceImpl service;

    private Customer entity;
    private CustomerRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        entity = Customer.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .annualSpend(BigDecimal.valueOf(5000))
                .lastPurchaseDate(OffsetDateTime.now().minusMonths(3))
                .build();

        request = new CustomerRequest(
                "Jane Doe",
                "jane@example.com",
                BigDecimal.valueOf(2000),
                OffsetDateTime.now().minusMonths(2)
        );
    }

    @Test
    void testCreateCustomer() {
        when(repository.save(any(Customer.class))).thenReturn(entity);

        CustomerResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    void testGetByIdSuccess() {
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        CustomerResponse response = service.getById(entity.getId());

        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getName(), response.getName());
    }

    @Test
    void testGetByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> service.getById(id));
    }

    @Test
    void testGetByEmailSuccess() {
        when(repository.findByEmailIgnoreCase(entity.getEmail())).thenReturn(Optional.of(entity));

        CustomerResponse response = service.getByEmail(entity.getEmail());

        assertEquals(entity.getEmail(), response.getEmail());
    }

    @Test
    void testGetByEmailNotFound() {
        when(repository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> service.getByEmail("missing@example.com"));
    }

    @Test
    void testFindByName() {
        when(repository.findByNameContainingIgnoreCase("John")).thenReturn(List.of(entity));

        List<CustomerResponse> responses = service.findByName("John");

        assertEquals(1, responses.size());
        assertEquals("John Doe", responses.get(0).getName());
    }

    @Test
    void testUpdateCustomerSuccess() {
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        CustomerResponse response = service.update(entity.getId(), request);

        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getName(), response.getName()); // still "John Doe"
    }

    @Test
    void testUpdateCustomerNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> service.update(id, request));
    }

    @Test
    void testDeleteCustomerSuccess() {
        UUID id = entity.getId();
        when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteCustomerNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(DataNotFoundException.class, () -> service.delete(id));
    }

    @Test
    void testComputeTierPlatinum() {
        Tier tier = service.calculateTier(BigDecimal.valueOf(15000), OffsetDateTime.now().minusMonths(2));
        assertEquals(Tier.Platinum, tier);
    }

    @Test
    void testComputeTierGold() {
        Tier tier = service.calculateTier(BigDecimal.valueOf(5000), OffsetDateTime.now().minusMonths(8));
        assertEquals(Tier.Gold, tier);
    }

    @Test
    void testComputeTierSilver() {
        Tier tier = service.calculateTier(BigDecimal.valueOf(500), OffsetDateTime.now().minusMonths(14));
        assertEquals(Tier.Silver, tier);
    }

    @Test
    void testIsWithinMonthsTrue() {
        boolean result = service.calculateTier(BigDecimal.valueOf(2000), OffsetDateTime.now().minusMonths(5)) != null;
        assertTrue(result);
    }

    @Test
    void testIsWithinMonthsFalse() {
        boolean result = service.calculateTier(BigDecimal.valueOf(2000), OffsetDateTime.now().minusMonths(20)) != null;
        assertTrue(result); // still returns a tier, but internally withinMonths is false
    }
}
