package com.bookingaccomodation.repository;

import com.bookingaccomodation.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByStartDateBetween(LocalDate start, LocalDate end);
    List<Booking> findByEndDateBetween(LocalDate start, LocalDate end);


    @Query("SELECT COUNT(b) FROM Booking b WHERE b.endDate > :currentDate AND b.userId = :userId")
    long countBookingsAfterTodayForGuest(@Param("currentDate") LocalDate currentDate, @Param("userId") String userId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.endDate > :currentDate AND b.accomodation.hostId = :hostId")
    long countBookingsAfterTodayForHost(@Param("currentDate") LocalDate currentDate, @Param("hostId") String hostId);

}
