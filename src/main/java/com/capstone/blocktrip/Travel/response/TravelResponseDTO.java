package com.capstone.blocktrip.Travel.response;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class TravelResponseDTO {
    public TravelResponseDTO() {
        this.flightList = new ArrayList<>(); // flightList 초기화
        this.placeList = new ArrayList<>();
        this.hotel = new Hotel();
    }

    @Data
    public static class Flight {
        private String flightname;
        private String price;
        private String depart;
        private String arrive;
        private String duration;
        private String latitude;
        private String longitude;
    }

    @Data
    public static class Hotel {
        private String name;
        private String price;
        private String latitude;
        private String longitude;
    }

    /*@Data
    public static class Restaurant {
        private String name;
        private String latitude;
        private String longitude;
    }*/

    @Data
    public static class Place {
        private String name;
        private String latitude;
        private String longitude;
        private String time;
    }

    private List<Flight> flightList;
    private Hotel hotel;
    private List<List<Place>> placeList;

}
