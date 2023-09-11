package com.bookingaccomodation.service;

import com.bookingaccomodation.exception.BadRequestException;
import com.bookingaccomodation.model.Accomodation;
import com.bookingaccomodation.model.CustomPricePerDay;
import com.bookingaccomodation.model.dto.AccomodationDTO;
import com.bookingaccomodation.model.dto.AccomodationWithPriceDTO;
import com.bookingaccomodation.model.dto.CustomPriceDTO;
import com.bookingaccomodation.repository.AccomodationRepository;
import com.bookingaccomodation.repository.CustomPricePerDayRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccomodationService {

    private final AccomodationRepository accomodationRepository;
    private final BookingService bookingService;
    private final CustomPricePerDayRepository customPricePerDayRepository;
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

    public void deleteForHost(String hostId) {
        accomodationRepository.deleteAll(accomodationRepository.findByHostId(hostId));
    }

    public List<AccomodationDTO> getAll() {
        return accomodationRepository
                .findAll()
                .stream()
                .map(a -> modelMapper.map(a, AccomodationDTO.class))
                .collect(Collectors.toList());
    }

    public AccomodationDTO getOne(String id) {
        Optional<Accomodation> optionalAccomodation = accomodationRepository.findById(id);
        if (optionalAccomodation.isEmpty()) {
            throw new BadRequestException(String.format("ID: '%s' doesn't exist.", id));
        }
        return modelMapper.map(optionalAccomodation.get(), AccomodationDTO.class);
    }

    public AccomodationDTO createCustomPriceForAccomodation(String accomodationId, CustomPriceDTO customPriceDTO) {
        Optional<Accomodation> optionalAccomodation = accomodationRepository.findById(accomodationId);

        if (optionalAccomodation.isEmpty()) {
            throw new BadRequestException(String.format("ID: '%s' doesn't exist.", accomodationId));
        }

        if(customPriceDTO.getStartDate().isAfter(customPriceDTO.getEndDate())){
            throw new BadRequestException("Start date can't be after end date!");
        }

        Accomodation accomodation = optionalAccomodation.get();

        if(bookingService.doesBookingExistInRangeForAccomodation(customPriceDTO.getStartDate(), customPriceDTO.getEndDate(), accomodationId)){
            throw new BadRequestException("A booking already exists in this date range!");
        }

        LocalDate currentDate = customPriceDTO.getStartDate();

        while (!currentDate.isAfter(customPriceDTO.getEndDate())) {
            Optional<CustomPricePerDay> optionalCustomPricePerDay = customPricePerDayRepository
                    .findByDateSlugAndAccomodationId(currentDate.toString(), accomodationId);

            CustomPricePerDay customPricePerDay;
            if (optionalCustomPricePerDay.isEmpty()) {
                customPricePerDay = CustomPricePerDay
                        .builder()
                        .date(currentDate)
                        .accomodation(accomodation)
                        .dateSlug(currentDate.toString())
                        .price(customPriceDTO.getPrice())
                        .build();
            } else {
                customPricePerDay = optionalCustomPricePerDay.get();
                customPricePerDay.setPrice(customPriceDTO.getPrice());
            }
            customPricePerDayRepository.save(customPricePerDay);

            currentDate = currentDate.plusDays(1);
        }

        return modelMapper.map(accomodation, AccomodationDTO.class);
    }

    public AccomodationDTO removeCustomPriceForAccomodation(String accomodationId, CustomPriceDTO customPriceDTO) {
        Optional<Accomodation> optionalAccomodation = accomodationRepository.findById(accomodationId);

        if (optionalAccomodation.isEmpty()) {
            throw new BadRequestException(String.format("ID: '%s' doesn't exist.", accomodationId));
        }

        if(customPriceDTO.getStartDate().isAfter(customPriceDTO.getEndDate())){
            throw new BadRequestException("Start date can't be after end date!");
        }

        Accomodation accomodation = optionalAccomodation.get();

        if(bookingService.doesBookingExistInRangeForAccomodation(customPriceDTO.getStartDate(), customPriceDTO.getEndDate(), accomodationId)){
            throw new BadRequestException("A booking already exists in this date range!");
        }

        LocalDate currentDate = customPriceDTO.getStartDate();
        while (!currentDate.isAfter(customPriceDTO.getEndDate())) {
            Optional<CustomPricePerDay> optionalCustomPricePerDay = customPricePerDayRepository.findByDateSlugAndAccomodationId(currentDate.toString(), accomodationId);
            if(optionalCustomPricePerDay.isPresent()){
//                customPricePerDayRepository.delete(optionalCustomPricePerDay.get());
                customPricePerDayRepository.deleteById(optionalCustomPricePerDay.get().getId());
                customPricePerDayRepository.flush();
            }
            currentDate = currentDate.plusDays(1);
        }

        return modelMapper.map(accomodation, AccomodationDTO.class);
    }

    public List<AccomodationWithPriceDTO> search(String location, Integer numOfGuests, LocalDate startDate, LocalDate endDate) {
        if(startDate.isAfter(endDate)){
            throw new BadRequestException("Start date can't be after end date!");
        }

        return accomodationRepository
                .findAll()
                .stream()
                .filter(a -> a.getLocation().toLowerCase().equals(location.toLowerCase()))
                .filter(a -> a.getMaxGuests() >= numOfGuests)
                .filter(a -> a.getMinGuests() <= numOfGuests)
                .filter(a -> bookingService.isAccomodationAvailable(startDate, endDate, a.getId()))
                .filter(a -> !bookingService.doesBookingExistInRangeForAccomodation(startDate, endDate, a.getId()))
                .map(a-> new AccomodationWithPriceDTO(a, bookingService.calculatePriceOfAccomodationForDateRange(a, startDate, endDate, numOfGuests)))
                .collect(Collectors.toList());
    }

    public List<CustomPricePerDay> getAvailabilitiesForAccomodation(String accomodationId) {
        return customPricePerDayRepository.findByAccomodationId(accomodationId);
    }

    public List<AccomodationDTO> getAllByHost(String id) {
        return accomodationRepository.findByHostId(id)
                .stream()
                .map(a -> modelMapper.map(a, AccomodationDTO.class))
                .collect(Collectors.toList());
    }
}
