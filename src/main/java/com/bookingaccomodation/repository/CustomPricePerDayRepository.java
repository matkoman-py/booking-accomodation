package com.bookingaccomodation.repository;

import com.bookingaccomodation.model.CustomPricePerDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface CustomPricePerDayRepository extends JpaRepository<CustomPricePerDay, String> {
    Optional<CustomPricePerDay> findByDateSlugAndAccomodationId(String s, String id);
    long deleteByDateSlugAndAccomodationId(String s, String id);

}
