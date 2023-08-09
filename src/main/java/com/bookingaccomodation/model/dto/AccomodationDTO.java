package com.bookingaccomodation.model.dto;

import com.bookingaccomodation.model.enums.PriceType;
import com.bookingaccomodation.model.enums.Utility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccomodationDTO {
    private String id;
    private String name;
    private String location;
    private boolean automaticApprove;
    private List<String> photographs;
    private List<Utility> utilities;
    private int minGuests;
    private int maxGuests;
    private PriceType priceType;
    private double defaultPrice;
    private String hostId;
}
