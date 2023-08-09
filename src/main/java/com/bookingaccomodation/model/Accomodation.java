package com.bookingaccomodation.model;

import com.bookingaccomodation.model.enums.PriceType;
import com.bookingaccomodation.model.enums.Utility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "accomodations")
public class Accomodation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String hostId;
    private boolean automaticApprove;
    private String location;
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(columnDefinition = "TEXT")
    private List<String> photographs;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Utility> utilities;
    private int minGuests;
    private int maxGuests;
    @Enumerated(EnumType.STRING)
    private PriceType priceType;
    private double defaultPrice;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "accomodation")
    private List<CustomPricePerDay> customPricesPerDay;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "accomodation")
    private List<Booking> bookings;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "accomodation")
    private List<BookingRequest> bookingRequests;
    // TODO: Ocene
}
