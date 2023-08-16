package com.bookingaccomodation.service;

import com.bookingaccomodation.exception.BadRequestException;
import com.bookingaccomodation.model.Accomodation;
import com.bookingaccomodation.model.Booking;
import com.bookingaccomodation.model.BookingRequest;
import com.bookingaccomodation.model.CustomPricePerDay;
import com.bookingaccomodation.model.dto.BookingRequestDTO;
import com.bookingaccomodation.model.enums.BookingRequestStatus;
import com.bookingaccomodation.model.enums.PriceType;
import com.bookingaccomodation.repository.AccomodationRepository;
import com.bookingaccomodation.repository.BookingRepository;
import com.bookingaccomodation.repository.BookingRequestRepository;
import com.bookingaccomodation.repository.CustomPricePerDayRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final AccomodationRepository accomodationRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final BookingRepository bookingRepository;
    private final CustomPricePerDayRepository customPricePerDayRepository;
    private final ModelMapper modelMapper;

    public boolean doActiveBookingsExistForGuest(String id) {
        return bookingRepository.countBookingsAfterTodayForGuest(LocalDate.now(), id) > 0;
    }

    public boolean doActiveBookingsExistForHost(String id) {
        return bookingRepository.countBookingsAfterTodayForHost(LocalDate.now(), id) > 0;
    }

    public boolean doesBookingExistInRangeForAccomodation(LocalDate startDate, LocalDate endDate, String accomodationId){
        Set<String> accomodationWithBookingsInRangeIds = bookingRepository
                .findByStartDateBetween(startDate, endDate)
                .stream()
                .map(booking -> booking.getAccomodation().getId())
                .collect(Collectors.toSet());

        accomodationWithBookingsInRangeIds.addAll(
                bookingRepository
                        .findByEndDateBetween(startDate, endDate)
                        .stream()
                        .map(booking -> booking.getAccomodation().getId())
                        .collect(Collectors.toSet())
        );

        return accomodationWithBookingsInRangeIds.contains(accomodationId);
    }

    public boolean isAccomodationAvailable(LocalDate startDate, LocalDate endDate, String accomodationId) {
        while (!startDate.isAfter(endDate)) {
            Optional<CustomPricePerDay> optionalCustomPricePerDay = customPricePerDayRepository
                    .findByDateSlugAndAccomodationId(startDate.toString(), accomodationId);

            if (optionalCustomPricePerDay.isEmpty()) {
                return false;
            }
            startDate = startDate.plusDays(1);
        }
        return true;
    }

    public double calculatePriceOfAccomodationForDateRange(Accomodation accomodation, LocalDate startDate, LocalDate endDate, int numOfGuests){
        double price = 0.0;
        while (!startDate.isAfter(endDate)) {
            Optional<CustomPricePerDay> optionalCustomPricePerDay = customPricePerDayRepository
                    .findByDateSlugAndAccomodationId(startDate.toString(), accomodation.getId());

            double priceForDay = optionalCustomPricePerDay.get().getPrice();

            if(accomodation.getPriceType().equals(PriceType.PRICE_OF_PROPERTY)){
                price += priceForDay;
            }
            else{
                price += priceForDay * numOfGuests;
            }

            startDate = startDate.plusDays(1);
        }
        return price;
    }

    public BookingRequestDTO create(BookingRequestDTO bookingRequestDTO) {
        Optional<Accomodation> optionalAccomodation = accomodationRepository.findById(bookingRequestDTO.getAccomodationId());
        if (optionalAccomodation.isEmpty()) {
            throw new BadRequestException(String.format("ID: '%s' doesn't exist.", bookingRequestDTO.getAccomodationId()));
        }
        if(bookingRequestDTO.getStartDate().isAfter(bookingRequestDTO.getEndDate())){
            throw new BadRequestException("Start date can't be after end date!");
        }
        Accomodation accomodation = optionalAccomodation.get();

        if(accomodation.getMaxGuests() < bookingRequestDTO.getNumOfGuests() || accomodation.getMinGuests() > bookingRequestDTO.getNumOfGuests()){
            throw new BadRequestException("Number of guests isn't valid!");
        }

        if(!isAccomodationAvailable(bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate(), accomodation.getId())){
            throw new BadRequestException("Accomodation isn't available in this range!");
        }

        if(doesBookingExistInRangeForAccomodation(bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate(), accomodation.getId())){
            throw new BadRequestException("There are already some bookings in the date range!");
        }

        BookingRequest bookingRequest = BookingRequest
                .builder()
                .userId(bookingRequestDTO.getUserId())
                .accomodation(accomodation)
                .startDate(bookingRequestDTO.getStartDate())
                .endDate(bookingRequestDTO.getEndDate())
                .status(BookingRequestStatus.PENDING)
                .numOfGuests(bookingRequestDTO.getNumOfGuests())
                .price(calculatePriceOfAccomodationForDateRange(accomodation, bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate(), bookingRequestDTO.getNumOfGuests()))
                .build();
        bookingRequestRepository.save(bookingRequest);

        if(accomodation.isAutomaticApprove()){
            approveBookingRequest(bookingRequest.getId());
        }
        return modelMapper.map(bookingRequest, BookingRequestDTO.class);
    }

    public List<BookingRequestDTO> getBookingsForHost(String hostId) {
        return bookingRepository
                .findAll()
                .stream()
                .filter(br -> br.getAccomodation().getHostId().equals(hostId))
                .map(br -> modelMapper.map(br, BookingRequestDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookingRequestDTO> getBookingsForUser(String userId) {
        return bookingRepository
                .findAll()
                .stream()
                .filter(br -> br.getUserId().equals(userId))
                .map(br -> modelMapper.map(br, BookingRequestDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookingRequestDTO> getRequestsForHost(String hostId, BookingRequestStatus status) {
        return bookingRequestRepository
                .findAll()
                .stream()
                .filter(br -> br.getAccomodation().getHostId().equals(hostId))
                .filter(br -> br.getStatus().equals(status))
                .map(br -> modelMapper.map(br, BookingRequestDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookingRequestDTO> getUserRequests(String userId, BookingRequestStatus status) {
        return bookingRequestRepository
                .findAll()
                .stream()
                .filter(br -> br.getUserId().equals(userId))
                .filter(br -> br.getStatus().equals(status))
                .map(br -> modelMapper.map(br, BookingRequestDTO.class))
                .collect(Collectors.toList());
    }

    public BookingRequestDTO approveBookingRequest(String id) {
        Optional<BookingRequest> optionalBookingRequest = bookingRequestRepository.findById(id);

        if(optionalBookingRequest.isEmpty()){
            throw new BadRequestException(String.format("ID: '%s' doesn't exist.", id));
        }

        BookingRequest bookingRequest = optionalBookingRequest.get();
        bookingRequest
                .setStatus(BookingRequestStatus.APPROVED);
        bookingRequestRepository.save(bookingRequest);

        Booking booking = Booking
                .builder()
                .accomodation(bookingRequest.getAccomodation())
                .endDate(bookingRequest.getEndDate())
                .startDate(bookingRequest.getStartDate())
                .price(bookingRequest.getPrice())
                .userId(bookingRequest.getUserId())
                .build();
        bookingRepository.save(booking);

        rejectAllBookingRequestsInRangeForAccomodation(booking.getStartDate(), booking.getEndDate(), booking.getAccomodation());
        return modelMapper.map(bookingRequest, BookingRequestDTO.class);
    }

    private void rejectAllBookingRequestsInRangeForAccomodation(LocalDate startDate, LocalDate endDate, Accomodation accomodation) {
        List<BookingRequest> bookingRequestsForAccomodation = bookingRequestRepository.findByAccomodation(accomodation);

        for(BookingRequest br : bookingRequestsForAccomodation){
            if(isBetweenInclusive(br.getStartDate(), startDate, endDate) || isBetweenInclusive(br.getEndDate(), startDate, endDate)){
                if(br.getStatus().equals(BookingRequestStatus.PENDING)){
                    br.setStatus(BookingRequestStatus.REJECTED);
                    bookingRequestRepository.save(br);
                }
            }
        }
    }

    public BookingRequestDTO rejectBookingRequest(String id) {
        Optional<BookingRequest> optionalBookingRequest = bookingRequestRepository.findById(id);

        if(optionalBookingRequest.isEmpty()){
            throw new BadRequestException(String.format("ID: '%s' doesn't exist.", id));
        }

        BookingRequest bookingRequest = optionalBookingRequest.get();
        bookingRequest
                .setStatus(BookingRequestStatus.REJECTED);
        bookingRequestRepository.save(bookingRequest);

        return modelMapper.map(bookingRequest, BookingRequestDTO.class);
    }

    public boolean isBetweenInclusive(LocalDate dateToCheck, LocalDate startDate, LocalDate endDate) {
        return !dateToCheck.isBefore(startDate) && !dateToCheck.isAfter(endDate);
    }

    public BookingRequestDTO cancelBookingRequest(String id) {
        Optional<BookingRequest> optionalBookingRequest = bookingRequestRepository.findById(id);

        if(optionalBookingRequest.isEmpty()){
            throw new BadRequestException(String.format("ID: '%s' doesn't exist.", id));
        }

        if(!optionalBookingRequest.get().getStatus().equals(BookingRequestStatus.PENDING)){
            throw new BadRequestException("This booking request has already been accepted or rejected!");
        }

        BookingRequestDTO br = modelMapper.map(optionalBookingRequest.get(), BookingRequestDTO.class);
        bookingRequestRepository.delete(optionalBookingRequest.get());
        return br;
    }

    public BookingRequestDTO cancelBooking(String id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if(optionalBooking.isEmpty()){
            throw new BadRequestException(String.format("ID: '%s' doesn't exist.", id));
        }
        Booking booking = optionalBooking.get();

        if(!LocalDate.now().isBefore(booking.getStartDate())){
            throw new BadRequestException("You can't cancel your booking anymore!");
        }

        BookingRequestDTO br = modelMapper.map(booking, BookingRequestDTO.class);
        bookingRepository.delete(booking);
        return br;
    }
}
