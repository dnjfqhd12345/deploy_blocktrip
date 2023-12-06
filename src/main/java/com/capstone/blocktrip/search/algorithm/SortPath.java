package com.capstone.blocktrip.search.algorithm;

import com.capstone.blocktrip.Travel.response.TravelResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class SortPath {
    public static void firstDaySort(int arriveHour, TravelResponseDTO travelResponseDTO, Coordinate airportCoordinate , List<Coordinate> realRestaurant, List<Coordinate> realPlace){
        List<TravelResponseDTO.Place> placeList = new ArrayList<>();
        TravelResponseDTO.Place place = new TravelResponseDTO.Place();
        Coordinate lastVisitedCoordinate = new Coordinate();
        int idx;
        int tempArriveHour = arriveHour;
        if(tempArriveHour < 8){
            tempArriveHour = 8;
        }
        // 공항 도착 시간이 식사시간과 겹칠 때 식당으로 안내.
        if((arriveHour>=8&&arriveHour<9) || (arriveHour>=12&&arriveHour<13) || (arriveHour>=19&&arriveHour<20)) {
            idx = ShortestPath.shortestRestaurant(airportCoordinate, realRestaurant);
            place = new TravelResponseDTO.Place();
            place.setName(realRestaurant.get((int) idx).getName() + ": " + arriveHour + "시 식당");
            place.setLatitude(String.valueOf(realRestaurant.get((int) idx).getLatitude()));
            place.setLongitude(String.valueOf(realRestaurant.get((int) idx).getLongitude()));
            place.setTime(String.valueOf(tempArriveHour));
            placeList.add(place);
            lastVisitedCoordinate.setLongitude(realRestaurant.get((int)idx).getLongitude());
            lastVisitedCoordinate.setLatitude(realRestaurant.get((int)idx).getLatitude());
            realRestaurant.remove(idx);
            tempArriveHour++;
        } else { // 공항 도착 시간이 식사시간이 아닐 때 관광명소로 안내.
            idx = ShortestPath.shortestRestaurant(airportCoordinate, realPlace);
            place = new TravelResponseDTO.Place();
            place.setName(realPlace.get((int) idx).getName()+ ": " + arriveHour + "시 관광지");
            place.setLatitude(String.valueOf(realPlace.get((int) idx).getLatitude()));
            place.setLongitude(String.valueOf(realPlace.get((int) idx).getLongitude()));
            place.setTime(String.valueOf(tempArriveHour));
            placeList.add(place);
            lastVisitedCoordinate.setLongitude(realRestaurant.get((int)idx).getLongitude());
            lastVisitedCoordinate.setLatitude(realRestaurant.get((int)idx).getLatitude());
            realPlace.remove(idx);
            if(tempArriveHour >= 13) {
                if ((tempArriveHour == 14) || (tempArriveHour == 16) || (tempArriveHour == 18) || (tempArriveHour == 21)) {
                    tempArriveHour++;
                } else {
                    tempArriveHour = tempArriveHour + 2;
                }
            } else {
                if(!(tempArriveHour>=22)) {
                    tempArriveHour++;
                }
            }
        }

        for(int i=tempArriveHour; i<=22; ){
            // 아침 / 점심 / 저녁 식사 시간
            if( (i>=8&&i<9) || (i>=12&&i<13) || (i>=19&&i<20)){
                idx = ShortestPath.shortestRestaurant(lastVisitedCoordinate,realRestaurant);
                place = new TravelResponseDTO.Place();
                place.setName(realRestaurant.get((int)idx).getName() + ": " + i + "시 식당");
                place.setLatitude(String.valueOf(realRestaurant.get((int)idx).getLatitude()));
                place.setLongitude(String.valueOf(realRestaurant.get((int)idx).getLongitude()));
                place.setTime(String.valueOf(i));
                placeList.add(place);
                lastVisitedCoordinate.setLongitude(realRestaurant.get((int)idx).getLongitude());
                lastVisitedCoordinate.setLatitude(realRestaurant.get((int)idx).getLatitude());
                realRestaurant.remove(idx);
                i = i+1;
            }else if((i>=9) && (i<12)){
                // (9시 ~ 10시 30분, 10시 30분 ~ 12시: 관광지 1, 2)
                if(i==10){
                    i++;
                    continue;
                }
                idx = ShortestPath.shortestRestaurant(lastVisitedCoordinate,realPlace);
                place = new TravelResponseDTO.Place();
                place.setName(realPlace.get((int)idx).getName() + ": " + i + "시 관광지");
                place.setLatitude(String.valueOf(realPlace.get((int)idx).getLatitude()));
                place.setLongitude(String.valueOf(realPlace.get((int)idx).getLongitude()));
                place.setTime(String.valueOf(i));
                placeList.add(place);
                lastVisitedCoordinate.setLongitude(realPlace.get((int)idx).getLongitude());
                lastVisitedCoordinate.setLatitude(realPlace.get((int)idx).getLatitude());
                realPlace.remove(idx);
                i = i+1;
            } else { // 관광지 3, 4, 5, 6
                idx = ShortestPath.shortestRestaurant(lastVisitedCoordinate,realPlace);
                System.out.println("firstday idx: " + idx);
                place = new TravelResponseDTO.Place();
                place.setName(realPlace.get((int)idx).getName() + ": " + i + "시 관광지");
                place.setLatitude(String.valueOf(realPlace.get((int)idx).getLatitude()));
                place.setLongitude(String.valueOf(realPlace.get((int)idx).getLongitude()));
                place.setTime(String.valueOf(i));
                placeList.add(place);
                lastVisitedCoordinate.setLongitude(realPlace.get((int)idx).getLongitude());
                lastVisitedCoordinate.setLatitude(realPlace.get((int)idx).getLatitude());
                realPlace.remove(idx);
                i = i+2;
            }
        }
        for (int i=0; i<realPlace.size(); i++){
            System.out.println( i + " 디버깅용 realPlace: " + realPlace.get(i).getName());
        }
        for (int i=0; i<realRestaurant.size(); i++){
            System.out.println( i + " 디버깅용 realRestaurant: " + realRestaurant.get(i).getName());
        }

        travelResponseDTO.getPlaceList().add(placeList);

    }
    public static void lastDaySort(int departHour, TravelResponseDTO travelResponseDTO,Coordinate hotelCoordinate , List<Coordinate> realRestaurant, List<Coordinate> realPlace){
        // departHour까지 비행기 탑승하면 된다.
        List<TravelResponseDTO.Place> placeList = new ArrayList<>();
        TravelResponseDTO.Place place = new TravelResponseDTO.Place();
        Coordinate lastVisitedCoordinate = new Coordinate();
            // 호텔 좌표에서 가장 가까운 곳
            int idx = ShortestPath.shortestRestaurant(hotelCoordinate,realRestaurant);
            place.setName(realRestaurant.get((int)idx).getName() + ": " + 8 + "시 식당");
            place.setLatitude(String.valueOf(realRestaurant.get((int)idx).getLatitude()));
            place.setLongitude(String.valueOf(realRestaurant.get((int)idx).getLongitude()));
        place.setTime(String.valueOf(8));
        placeList.add(place);
            realRestaurant.remove(idx);
        for(int i=9; i<=departHour; ){
            // 아침 / 점심 / 저녁 식사 시간
            if(i==12 || i==19){
                idx = ShortestPath.shortestRestaurant(hotelCoordinate,realRestaurant);
                place = new TravelResponseDTO.Place();
                place.setName(realRestaurant.get((int)idx).getName() + ": " + i + "시 식당");
                place.setLatitude(String.valueOf(realRestaurant.get((int)idx).getLatitude()));
                place.setLongitude(String.valueOf(realRestaurant.get((int)idx).getLongitude()));
                place.setTime(String.valueOf(i));
                placeList.add(place);
                lastVisitedCoordinate.setLongitude(realRestaurant.get((int)idx).getLongitude());
                lastVisitedCoordinate.setLatitude(realRestaurant.get((int)idx).getLatitude());
                realRestaurant.remove(idx);
                i = i+1;
            }else if(i==9 || i == 10 || i == 11){
                // (9시 ~ 10시 30분, 10시 30분 ~ 12시: 관광지 1, 2)
                if(i==10){
                    i++;
                    continue;
                }
                idx = ShortestPath.shortestRestaurant(lastVisitedCoordinate,realPlace);
                place = new TravelResponseDTO.Place();
                place.setName(realPlace.get((int)idx).getName() + ": " + i + "시 관광지");
                place.setLatitude(String.valueOf(realPlace.get((int)idx).getLatitude()));
                place.setLongitude(String.valueOf(realPlace.get((int)idx).getLongitude()));
                place.setTime(String.valueOf(i));
                placeList.add(place);
                lastVisitedCoordinate.setLongitude(realPlace.get((int)idx).getLongitude());
                lastVisitedCoordinate.setLatitude(realPlace.get((int)idx).getLatitude());
                realPlace.remove(idx);
                i = i+1;
            } else { // 관광지 3, 4, 5, 6
                idx = ShortestPath.shortestRestaurant(lastVisitedCoordinate,realPlace);
                place = new TravelResponseDTO.Place();
                System.out.println("lastday idx: " + idx);
                place.setName(realPlace.get((int)idx).getName() + ": " + i + "시 관광지");
                place.setLatitude(String.valueOf(realPlace.get((int)idx).getLatitude()));
                place.setLongitude(String.valueOf(realPlace.get((int)idx).getLongitude()));
                place.setTime(String.valueOf(i));
                placeList.add(place);
                lastVisitedCoordinate.setLongitude(realPlace.get((int)idx).getLongitude());
                lastVisitedCoordinate.setLatitude(realPlace.get((int)idx).getLatitude());
                realPlace.remove(idx);
                i = i+2;
            }
        }
        for (int i=0; i<realPlace.size(); i++){
            System.out.println( i + " 디버깅용 realPlace: " + realPlace.get(i).getName());
        }
        for (int i=0; i<realRestaurant.size(); i++){
            System.out.println( i + " 디버깅용 realRestaurant: " + realRestaurant.get(i).getName());
        }
        travelResponseDTO.getPlaceList().add(placeList);
    }
    public static void restDaySort(TravelResponseDTO travelResponseDTO,Coordinate hotelCoordinate, List<Coordinate> realRestaurant, List<Coordinate> realPlace){
        // 8시 기상부터 차례대로 하면 된다.
        List<TravelResponseDTO.Place> placeList = new ArrayList<>();
        TravelResponseDTO.Place place = new TravelResponseDTO.Place();
        Coordinate lastVisitedCoordinate = new Coordinate();
        // 호텔 좌표에서 가장 가까운 곳
        int idx = ShortestPath.shortestRestaurant(hotelCoordinate,realRestaurant);
        place = new TravelResponseDTO.Place();
        place.setName(realRestaurant.get((int)idx).getName() + ": " + 8 + "시 식당");
        place.setLatitude(String.valueOf(realRestaurant.get((int)idx).getLatitude()));
        place.setLongitude(String.valueOf(realRestaurant.get((int)idx).getLongitude()));
        place.setTime(String.valueOf(8));
        placeList.add(place);
        realRestaurant.remove(idx);
        for(int i=9; i<=22; ){
            // 아침 / 점심 / 저녁 식사 시간
            if(i==12 || i==19){
                idx = ShortestPath.shortestRestaurant(hotelCoordinate,realRestaurant);
                place = new TravelResponseDTO.Place();
                place.setName(realRestaurant.get((int)idx).getName() + ": " + i + "시 식당");
                place.setLatitude(String.valueOf(realRestaurant.get((int)idx).getLatitude()));
                place.setLongitude(String.valueOf(realRestaurant.get((int)idx).getLongitude()));
                place.setTime(String.valueOf(i));
                placeList.add(place);
                lastVisitedCoordinate.setLongitude(realRestaurant.get((int)idx).getLongitude());
                lastVisitedCoordinate.setLatitude(realRestaurant.get((int)idx).getLatitude());
                realRestaurant.remove(idx);
                i = i+1;
            }else if(i==9 || i == 10 || i == 11){
                // (9시 ~ 10시 30분, 10시 30분 ~ 12시: 관광지 1, 2)
                if(i==10){
                    i++;
                    continue;
                }
                idx = ShortestPath.shortestRestaurant(lastVisitedCoordinate,realPlace);
                place = new TravelResponseDTO.Place();
                place.setName(realPlace.get((int)idx).getName() + ": " + i + "시 관광지");
                place.setLatitude(String.valueOf(realPlace.get((int)idx).getLatitude()));
                place.setLongitude(String.valueOf(realPlace.get((int)idx).getLongitude()));
                place.setTime(String.valueOf(i));
                placeList.add(place);
                lastVisitedCoordinate.setLongitude(realPlace.get((int)idx).getLongitude());
                lastVisitedCoordinate.setLatitude(realPlace.get((int)idx).getLatitude());
                realPlace.remove(idx);
                i = i+1;
            } else { // 관광지 3, 4, 5, 6
                idx = ShortestPath.shortestRestaurant(lastVisitedCoordinate,realPlace);
                System.out.println("restday idx: " + idx);
                place = new TravelResponseDTO.Place();
                place.setName(realPlace.get((int)idx).getName() + ": " + i + "시 관광지");
                place.setLatitude(String.valueOf(realPlace.get((int)idx).getLatitude()));
                place.setLongitude(String.valueOf(realPlace.get((int)idx).getLongitude()));
                place.setTime(String.valueOf(i));
                placeList.add(place);
                lastVisitedCoordinate.setLongitude(realPlace.get((int)idx).getLongitude());
                lastVisitedCoordinate.setLatitude(realPlace.get((int)idx).getLatitude());
                realPlace.remove(idx);
                i = i+2;
            }
        }
        for (int i=0; i<realPlace.size(); i++){
            System.out.println( i + " 디버깅용 realPlace: " + realPlace.get(i).getName());
        }
        for (int i=0; i<realRestaurant.size(); i++){
            System.out.println( i + " 디버깅용 realRestaurant: " + realRestaurant.get(i).getName());
        }
        travelResponseDTO.getPlaceList().add(placeList);
    }
}
