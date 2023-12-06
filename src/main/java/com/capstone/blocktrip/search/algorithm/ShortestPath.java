package com.capstone.blocktrip.search.algorithm;

import java.util.List;

public class ShortestPath {

    // 해당되는 인덱스를 반환.
    public static int shortestPlace(Coordinate coordinate, List<Coordinate> place){
        double currentLatitude = coordinate.getLatitude();
        double currentLongitude = coordinate.getLatitude();
        double shortestDistance = 10000000;
        int shortestIdx = 0;
        System.out.println("디버깅용 place size: " + place.size());
        for(int i=0; i<place.size(); i++){
            double distance = calculateDistance(currentLatitude, currentLongitude, place.get(i).getLatitude(), place.get(i).getLongitude());
            if(distance < shortestDistance){
                shortestDistance = distance;
                shortestIdx = i;
            }
        }
        return shortestIdx;
    }

    public static int shortestRestaurant(Coordinate coordinate, List<Coordinate> restaurant){
        double currentLatitude = coordinate.getLatitude();
        double currentLongitude = coordinate.getLatitude();
        double shortestDistance = 10000000;
        int shortestIdx = 0;
        System.out.println("디버깅용 place size: " + restaurant.size());
        for(int i=0; i<restaurant.size(); i++){
            double distance = calculateDistance(currentLatitude, currentLongitude, restaurant.get(i).getLatitude(), restaurant.get(i).getLongitude());
            System.out.println("현재 비교하는 distance: " + distance);
            System.out.println("현재 shortestDistance: " + shortestDistance);
            if(distance < shortestDistance){
                System.out.println("shortestDistance 업데이트: " + shortestDistance);
                shortestDistance = distance;
                shortestIdx = i;
            }
        }
        return shortestIdx;
    }

    // 위도 경도를 기반으로 두 지점 간의 거리를 계산하는 메서드
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 지구 반지름 (단위: km)
        double earthRadius = 6371.0;

        // 위도 및 경도를 라디안으로 변환
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // 하버사인 공식
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

}
