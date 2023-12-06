package com.capstone.blocktrip.Travel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
public class TravelResponse {
    private List<String> recommendedRestaurants;
    private List<String> recommendedPlaces;

}
