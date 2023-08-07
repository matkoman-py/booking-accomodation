package com.bookingaccomodation.service;

import com.bookingaccomodation.exception.BadRequestException;
import com.bookingaccomodation.model.Accomodation;
import com.bookingaccomodation.model.dto.AccomodationDTO;
import com.bookingaccomodation.repository.AccomodationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccomodationService {

    private final AccomodationRepository accomodationRepository;
    private final ModelMapper modelMapper;

    public AccomodationDTO create(AccomodationDTO accomodationDTO) {
        Optional<Accomodation> optionalAccommodation = accomodationRepository
                .findByName(accomodationDTO.getName());
        if (optionalAccommodation.isPresent()) {
            throw new BadRequestException(String.format("Name '%s' is already in use.", accomodationDTO.getName()));
        }
        Accomodation accommodation = modelMapper.map(accomodationDTO, Accomodation.class);
        accommodation.setCustomPricesPerDay(Collections.emptyList());
        accommodation.setBookings(Collections.emptyList());
        accommodation.setBookingRequests(Collections.emptyList());
        return modelMapper.map(accomodationRepository.save(accommodation), AccomodationDTO.class);
    }

    public List<AccomodationDTO> getAll() {
        List<AccomodationDTO> result = new ArrayList<>();
        for(Accomodation accomodation : accomodationRepository.findAll()){
            result.add(modelMapper.map(accomodation, AccomodationDTO.class));
        }
        return result;
    }

    public AccomodationDTO getOne(String id) {
        Optional<Accomodation> optionalAccomodation = accomodationRepository.findById(id);
        if (optionalAccomodation.isEmpty()) {
            throw new BadRequestException(String.format("ID: '%s' doesn't exist.", id));
        }
        return modelMapper.map(optionalAccomodation.get(), AccomodationDTO.class);
    }
}
