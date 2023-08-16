package com.bookingaccomodation.model.dto;

import com.bookingaccomodation.model.enums.BookingRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingRequestDTO {
    private String id;
    private LocalDate startDate;
    private LocalDate endDate;
    private double price;
    private int numOfGuests;
    private String userId;
    private String accomodationId;
    private BookingRequestStatus status;
}
