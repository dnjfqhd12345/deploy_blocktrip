package com.capstone.blocktrip.Travel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "travels")
public class Travel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "departure_location")
    private String departureLocation;

    @Column(name = "destination_location")
    private String destinationLocation;

    @Column(name = "departure_date")
    private String departureDate;

    @Column(name = "arrival_date")
    private String arrivalDate;

    @ElementCollection
    @Column(name = "interests")
    private List<String> interests;

    @ElementCollection
    @Column(name = "travel_styles")
    private List<String> travelStyles;

    @ElementCollection
    @Column(name = "food_types")
    private List<String> foodTypes;

    @ElementCollection
    @Column(name = "restaurant_types")
    private List<String> restaurantTypes;


    @Builder
    public Travel(String departureLocation, String destinationLocation,
                  String departureDate, String arrivalDate,
                  List<String> interests, List<String> travelStyles,
                  List<String> foodTypes, List<String> restaurantTypes) {
        this.departureLocation = departureLocation;
        this.destinationLocation = destinationLocation;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.interests = interests;
        this.travelStyles = travelStyles;
        this.foodTypes = foodTypes;
        this.restaurantTypes = restaurantTypes;
    }
}
