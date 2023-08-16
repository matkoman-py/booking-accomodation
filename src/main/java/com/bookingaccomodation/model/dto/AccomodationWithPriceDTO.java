package com.bookingaccomodation.model.dto;

import com.bookingaccomodation.model.Accomodation;
import com.bookingaccomodation.model.enums.PriceType;
import com.bookingaccomodation.model.enums.Utility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccomodationWithPriceDTO {
    private String id;
    private String name;
    private String location;
    private boolean automaticApprove;
    private List<String> photographs;
    private List<Utility> utilities;
    private int minGuests;
    private int maxGuests;
    private PriceType priceType;
    private String hostId;
    private double price;

    public AccomodationWithPriceDTO(Accomodation a, double price) {
        this.id = a.getId();
        this.name = a.getLocation();
        this.location = a.getLocation();
        this.automaticApprove = a.isAutomaticApprove();
        this.photographs = a.getPhotographs();
        this.utilities = a.getUtilities();
        this.minGuests = a.getMinGuests();
        this.maxGuests = a.getMaxGuests();
        this.priceType = a.getPriceType();
        this.hostId = a.getHostId();
        this.price = price;
    }
}
