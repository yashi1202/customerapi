package com.example.customerapi.dto;

import java.math.BigDecimal;
//import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.customerapi.enumT.Tier;
import com.example.customerapi.model.Customer;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
	
	private UUID id;
    private String name;
    private String email;
    private BigDecimal annualSpend;
    private OffsetDateTime lastPurchaseDate;
    private Tier tier;

    

}

