package com.bookingaccomodation.controller;


import com.bookingaccomodation.model.CustomPricePerDay;
import com.bookingaccomodation.model.dto.AccomodationDTO;
import com.bookingaccomodation.model.dto.AccomodationWithPriceDTO;
import com.bookingaccomodation.model.dto.CustomPriceDTO;
import com.bookingaccomodation.service.AccomodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/accomodation")
@RequiredArgsConstructor
public class AccomodationController {

    private final AccomodationService accomodationService;

    @PostMapping
    public ResponseEntity<AccomodationDTO> create(@RequestBody AccomodationDTO accomodationDTO) {
        return ResponseEntity.ok(accomodationService.create(accomodationDTO));
    }

    @GetMapping
    public ResponseEntity<List<AccomodationDTO>> getAll() {
        return ResponseEntity.ok(accomodationService.getAll());
    }

    @DeleteMapping("/host/{id}")
    public ResponseEntity<String> deleteAllForHost(@PathVariable String id) {
        accomodationService.deleteForHost(id);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/search")
    public ResponseEntity<List<AccomodationWithPriceDTO>> search(@RequestParam String location,
                                                                 @RequestParam Integer numOfGuests,
                                                                 @RequestParam LocalDate startDate,
                                                                 @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(accomodationService.search(location, numOfGuests, startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccomodationDTO> getOne(@PathVariable String id) {
        return ResponseEntity.ok(accomodationService.getOne(id));
    }

    @PostMapping("/create-availability/{accomodationId}")
    public ResponseEntity<AccomodationDTO> createCustomPriceForAccomodation(@PathVariable String accomodationId,
                                                                            @RequestBody CustomPriceDTO customPriceDTO) {
        return ResponseEntity.ok(accomodationService.createCustomPriceForAccomodation(accomodationId, customPriceDTO));
    }

    @GetMapping("/availability/{accomodationId}")
    public ResponseEntity<List<CustomPricePerDay>> getAvailabilitiesForAccomodation(@PathVariable String accomodationId) {
        return ResponseEntity.ok(accomodationService.getAvailabilitiesForAccomodation(accomodationId));
    }

    @DeleteMapping("/remove-availability/{accomodationId}")
    public ResponseEntity<AccomodationDTO> removeCustomPriceForAccomodation(@PathVariable String accomodationId,
                                                                            @RequestBody CustomPriceDTO customPriceDTO) {
        return ResponseEntity.ok(accomodationService.removeCustomPriceForAccomodation(accomodationId, customPriceDTO));
    }
}
