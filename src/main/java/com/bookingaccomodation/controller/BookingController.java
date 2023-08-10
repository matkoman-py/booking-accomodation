package com.bookingaccomodation.controller;

import com.bookingaccomodation.model.dto.BookingRequestDTO;
import com.bookingaccomodation.model.enums.BookingRequestStatus;
import com.bookingaccomodation.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingRequestDTO> create(@RequestBody BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.ok(bookingService.create(bookingRequestDTO));
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<BookingRequestDTO>> getBookingsForHost(@PathVariable String hostId) {
        return ResponseEntity.ok(bookingService.getBookingsForHost(hostId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingRequestDTO>> getBookingsForUser(@PathVariable String userId) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(userId));
    }

    @GetMapping("/request/host/{hostId}")
    public ResponseEntity<List<BookingRequestDTO>> getRequestsForHost(@PathVariable String hostId,
                                                                      @RequestParam BookingRequestStatus status) {
        return ResponseEntity.ok(bookingService.getRequestsForHost(hostId, status));
    }

    @GetMapping("/request/user/{userId}")
    public ResponseEntity<List<BookingRequestDTO>> getUserRequests(@PathVariable String userId,
                                                                   @RequestParam BookingRequestStatus status) {
        return ResponseEntity.ok(bookingService.getUserRequests(userId, status));
    }

    @PutMapping("/request/{id}/approve")
    public ResponseEntity<BookingRequestDTO> approveBookingRequest(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.approveBookingRequest(id));
    }

    @PutMapping("/request/{id}/reject")
    public ResponseEntity<BookingRequestDTO> rejectBookingRequest(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.rejectBookingRequest(id));
    }

    @DeleteMapping("/request/{id}")
    public ResponseEntity<BookingRequestDTO> cancelBookingRequest(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.cancelBookingRequest(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookingRequestDTO> cancelBooking(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }
}
