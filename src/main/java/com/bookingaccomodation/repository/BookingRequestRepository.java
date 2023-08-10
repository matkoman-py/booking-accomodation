package com.bookingaccomodation.repository;

import com.bookingaccomodation.model.Accomodation;
import com.bookingaccomodation.model.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, String> {
    List<BookingRequest> findByAccomodation(Accomodation accomodation);
}
