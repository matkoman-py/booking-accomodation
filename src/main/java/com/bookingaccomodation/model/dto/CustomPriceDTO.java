package com.bookingaccomodation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomPriceDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private double price;
}
