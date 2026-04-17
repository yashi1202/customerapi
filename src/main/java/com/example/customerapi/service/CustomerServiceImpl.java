package com.example.customerapi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.customerapi.dto.CustomerRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.enumT.Tier;
import com.example.customerapi.exception.DataNotFoundException;
import com.example.customerapi.model.Customer;
import com.example.customerapi.repository.CustomerRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerServiceInterface{
	
	private final CustomerRepository repository;
	
	private static final BigDecimal TEN_THOUSAND = BigDecimal.valueOf(10000);
    private static final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000);

	@Override
	public CustomerResponse create(CustomerRequest req) {
		// TODO Auto-generated method stub
		log.info("Creating customer: name={}, email={}", req.getName(), req.getEmail());

		if(repository.existsByEmail(req.getEmail())){
	        throw new IllegalArgumentException("Email already exists");
	    }
		
        Customer e = Customer.builder()
                .name(req.getName())
                .email(req.getEmail())
                .annualSpend(req.getAnnualSpend())
                .lastPurchaseDate(req.getLastPurchaseDate())
                .build();

        Customer saved = repository.save(e);
        log.debug("Created customer with id={}", saved.getId());
        return mapEntityToResponse(saved);
		
	}

	@Override
	@Transactional(readOnly=true)
	public CustomerResponse getById(UUID id) {
		// TODO Auto-generated method stub
		log.debug("Fetching customer by id={}", id);
        Customer e = repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
        return mapEntityToResponse(e);
	}

	@Override
	@Transactional(readOnly=true)
	public CustomerResponse getByEmail(String email) {
		// TODO Auto-generated method stub
		log.debug("Fetching customer by email={}", email);
        Customer e = repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
        
        return mapEntityToResponse(e);
		
	}

	@Override
	@Transactional(readOnly=true)
	public List<CustomerResponse> findByName(String name) {
		// TODO Auto-generated method stub
		log.debug("Searching customers by name contains={}", name);
        return repository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
		
	}

	@Override
	public CustomerResponse update(UUID id, CustomerRequest req) {
		// TODO Auto-generated method stub
		log.info("Updating customer id={} name={} email={}", id, req.getName(), req.getEmail());
		
        Customer e = repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
        
        if(!e.getEmail().equalsIgnoreCase(req.getEmail()) &&
        		   repository.existsByEmail(req.getEmail())){
        		    throw new IllegalArgumentException("Email already exists");
        		}

        
        e.setName(req.getName());
        e.setEmail(req.getEmail());
        e.setAnnualSpend(req.getAnnualSpend());
        e.setLastPurchaseDate(req.getLastPurchaseDate());
        
        Customer updated=repository.save(e);

        return mapEntityToResponse(updated);
	}

	@Override
	public void delete(UUID id) {
		// TODO Auto-generated method stub
		log.warn("Deleting customer id={}", id);
        if (!repository.existsById(id)) {
            throw new DataNotFoundException("Customer not found");
        }
        repository.deleteById(id);
		
	}
	
	private CustomerResponse mapEntityToResponse(Customer e) {
        CustomerResponse resp = CustomerResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .email(e.getEmail())
                .annualSpend(e.getAnnualSpend())
                .lastPurchaseDate(e.getLastPurchaseDate())
                .build();

        Tier tier = calculateTier(e.getAnnualSpend(), e.getLastPurchaseDate());
        resp.setTier(tier);
        return resp;
    }

	public Tier calculateTier(BigDecimal annualSpend, OffsetDateTime lastPurchaseDate) {
		// TODO Auto-generated method stub
		BigDecimal spend = annualSpend == null ? BigDecimal.ZERO : annualSpend;
        boolean within12Months = isWithinMonths(lastPurchaseDate, 12);
        boolean within6Months = isWithinMonths(lastPurchaseDate, 6);

        Tier tier;
        if (spend.compareTo(TEN_THOUSAND) >= 0 && within6Months) {
            tier = Tier.Platinum;
        } else if (spend.compareTo(ONE_THOUSAND) >= 0 && spend.compareTo(TEN_THOUSAND) < 0 && within12Months) {
            tier = Tier.Gold;
        } else {
            tier = Tier.Silver;
        }
        log.debug("Computed tier={} for spend={} lastPurchaseDate={} (6m={}, 12m={})",
                tier, spend, lastPurchaseDate, within6Months, within12Months);
        return tier;
	}

	private boolean isWithinMonths(OffsetDateTime date, int months) {
		// TODO Auto-generated method stub
		if (date == null) return false;
        OffsetDateTime threshold = OffsetDateTime.now().minus(months, ChronoUnit.MONTHS);
        return !date.isBefore(threshold);
		
	}
	
	
	


}

