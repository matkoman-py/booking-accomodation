package com.bookingaccomodation.repository;

import com.bookingaccomodation.model.Accomodation;
import com.bookingaccomodation.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AccomodationRepository extends JpaRepository<Accomodation, String> {
    Optional<Accomodation> findByName(String name);
    List<Accomodation> findByHostId(String hostId);

}
