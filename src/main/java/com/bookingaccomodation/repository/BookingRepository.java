package com.bookingaccomodation.repository;

import com.bookingaccomodation.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByStartDateBetween(LocalDate start, LocalDate end);
    List<Booking> findByEndDateBetween(LocalDate start, LocalDate end);

}
