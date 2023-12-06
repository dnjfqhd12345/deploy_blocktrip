package com.capstone.blocktrip.Travel;

import com.capstone.blocktrip.ChatGPT.ChatGPTService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TravelService {
    private final ChatGPTService chatGPTService;

    @Autowired
    public TravelService(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    public TravelResponse generatePlan(TravelRequest request) {
        String restaurantPrompt = createRestaurantPrompt(request);
        String placePrompt = createPlacePrompt(request);

        CompletableFuture<String> restaurantFuture = chatGPTService.callGPT3Async(restaurantPrompt);
        CompletableFuture<String> placeFuture = chatGPTService.callGPT3Async(placePrompt);

        CompletableFuture.allOf(restaurantFuture, placeFuture).join();

        String restaurantResponse = restaurantFuture.join();
        String placeResponse = placeFuture.join();

        return combineResponses(restaurantResponse, placeResponse);
    }

    private String createRestaurantPrompt(TravelRequest request) {
        TravelRequest.CommonRequest common = request.getCommon();
        TravelRequest.RestaurantRequest restaurant = request.getRestaurant();
        String foodTypes = String.join(", ", restaurant.getFoodType());
        String restaurantTypes = String.join(", ", restaurant.getRestaurantType());

        return String.format(
                "목적지: %s, 출발일: %s, 도착일: %s\n" +
                        "%s 지역에서 %s 요리를 제공하는 %s 음식점을 추천해주세요.\n" +
                        "목록 형식으로 최대 40개의 실제 존재하는 음식점를 제시해주세요. 각 음식점의 이름만 제공하고, newline을 절대 사용하지 말고 이를 예시와 같이 쉼표 only Comma(, )로만 구분해 나열해주세요. (예시: 광주형무소역사관, 아시아문화전당, 전남대학교, 광주황톳길)",
                common.getDestinationLocation(),
                common.getDepartureDate(),
                common.getArrivalDate(),
                common.getDestinationLocation(),
                foodTypes,
                restaurantTypes
        );
    }

    private String createPlacePrompt(TravelRequest request) {
        TravelRequest.CommonRequest common = request.getCommon();
        TravelRequest.PlaceRequest place = request.getPlace();
        String interests = String.join(", ", place.getInterests());
        String travelStyles = String.join(", ", place.getTravelStyle());

        return String.format(
                "목적지: %s, 출발일: %s, 도착일: %s\n" +
                        "%s 지역에서 %s 스타일의 여행에 적합한 관광지를 추천해주세요. 관심사는 다음과 같습니다: %s\n" +
                        "목록 형식으로 최대 40개의 관광지를 제시해주세요. 각 관광지의 이름만 제공하고, newline을 절대 사용하지 말고 이를 예시와 같이 쉼표 Comma(, )로만 구분해 나열해주세요. (예시: 일식당, 데미안, 일본삼시세끼, 일알리스)",
                common.getDestinationLocation(),
                common.getDepartureDate(),
                common.getArrivalDate(),
                common.getDestinationLocation(),
                travelStyles,
                interests
        );
    }

    private TravelResponse combineResponses(String restaurantResponse, String placeResponse) {
        TravelResponse response = new TravelResponse();
        response.setRecommendedRestaurants(parseResponse(restaurantResponse));
        response.setRecommendedPlaces(parseResponse(placeResponse));
        return response;
    }

    private List<String> parseResponse(String response) {
        List<String> results = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode choices = rootNode.path("choices");
            if (!choices.isMissingNode() && choices.isArray()) {
                for (JsonNode choice : choices) {
                    JsonNode messageNode = choice.path("message");
                    if (!messageNode.isMissingNode()) {
                        String content = messageNode.path("content").asText();
                        String[] items = content.split(",");
                        for (String item : items) {
                            results.add(item.trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }


}
