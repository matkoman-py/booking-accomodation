package com.bookingaccomodation.repository;

import com.bookingaccomodation.model.Accomodation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccomodationRepository extends JpaRepository<Accomodation, String> {
    Optional<Accomodation> findByName(String name);
}
