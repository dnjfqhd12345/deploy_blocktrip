package com.capstone.blocktrip.Travel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

public class TravelRequest {

    @Getter
    @Setter
    private CommonRequest common;

    @Getter
    @Setter
    private RestaurantRequest restaurant;

    @Getter
    @Setter
    private PlaceRequest place;

    @Getter
    @Setter
    private Hotel hotel;

    @Getter
    @Setter
    private Flight flight;


    @Getter
    @Setter
    @ToString
    public static class CommonRequest {
        private String departureLocation;
        private String destinationLocation;
        private String departureDate;
        private String arrivalDate;
    }

    @Getter
    @Setter
    @ToString
    public static class RestaurantRequest {
        private List<String> foodType;
        // 선호하는
        private List<String> restaurantType;
        // 카테고리
    }

    @Getter
    @Setter
    @ToString
    public static class PlaceRequest {
        private List<String> interests;
        // 관심있는
        private List<String> travelStyle;
        // 여행 스타일
    }

    @Getter
    @Setter
    @ToString
    public static class Hotel {
        private String region;
        private String checkin;
        private String checkout;
        private String adult;
        private String room;
        private String child;
        private String sort;
    }

    @Getter
    @Setter
    @ToString
    public static class Flight {
        private String depart;
        private String arrive;
        private String departDate;
        private String arriveDate;
        private String flightType;
        private String seatClass;
        private String quantity;
        private String childQuantity;
        private String babyQuantity;
    }

}
