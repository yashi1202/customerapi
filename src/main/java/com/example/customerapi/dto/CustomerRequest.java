package com.example.customerapi.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
public class CustomerRequest {
	
	 @NotBlank(message = "name is required")
	    private String name;

	    @NotBlank(message = "email is required")
	    @Email(message = "email must be valid")
	    private String email;

	    @PositiveOrZero(message = "annualSpend must be positive or zero")
	    private BigDecimal annualSpend;

	    @PastOrPresent(message = "lastPurchaseDate must be in the past or present")
	    private OffsetDateTime lastPurchaseDate;

}

