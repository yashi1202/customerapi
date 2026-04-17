package com.example.customerapi.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customers_email", columnNames = {"email"})
})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    
    private UUID id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(precision = 19, scale = 2)

    private BigDecimal annualSpend;

    private OffsetDateTime lastPurchaseDate;

    
}


