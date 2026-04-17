package com.example.customerapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

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
public class ErrorResponse {
	@Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> errors;
	

}

