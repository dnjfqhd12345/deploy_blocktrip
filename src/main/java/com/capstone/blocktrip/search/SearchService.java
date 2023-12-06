package com.capstone.blocktrip.search;

import com.capstone.blocktrip.Travel.TravelRequest;
import com.capstone.blocktrip.Travel.TravelResponse;
import com.capstone.blocktrip.Travel.response.TravelResponseDTO;
import com.capstone.blocktrip._core.errors.exception.Exception500;
import com.capstone.blocktrip.search.algorithm.Coordinate;
import com.capstone.blocktrip.search.algorithm.ShortestPath;
import com.capstone.blocktrip.search.algorithm.SortPath;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Component
@EnableConfigurationProperties
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SearchService {

    private final SearchJPARepository searchJPARepository;

    // Google Maps API 키
    @Value("${api.google.maps.key}")
    private String apiKey;

    @Transactional
    public void valid(String mySearch ){
        String keyword = mySearch;
        try {
            // Google 검색 결과 페이지 URL을 생성
            String googleSearchUrl = "https://www.google.com/search?q=" + keyword;

            // Google 검색 결과 페이지를 JSoup를 사용하여 가져옴
            Document document = Jsoup.connect(googleSearchUrl).get();

            // 검색 결과 링크에 해당하는 CSS 선택자를 사용하여 검색 결과 링크 엘리먼트를 선택
            Elements searchResults = document.select(".tF2Cxc");

            // 검색 결과를 순회하며 특정 키워드가 있는지 확인
            for (Element result : searchResults) {
                String title = result.select("h3").text(); // 검색 결과의 제목을 가져옴
                String snippet = result.select(".st").text(); // 검색 결과의 스니펫(요약)을 가져옴

                // 검색 결과에 특정 키워드가 포함되어 있는지 확인
                if (title.contains(keyword) || snippet.contains(keyword)) {
                    System.out.println(keyword + "는 Google 검색 결과에서 키워드를 찾았습니다.");
                    searchJPARepository.save(Search.builder().searchKeyword(keyword).searchResult(keyword).build());
                    return;
                }
            }

            // 키워드를 찾지 못한 경우
            System.out.println(keyword + "는 Google 검색 결과에서 키워드를 찾지 못했습니다.");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Coordinate getCoordinate(String myLocation, String mySearch){
        Coordinate myCoordinate = new Coordinate();
        String keyword = mySearch;
        String location = myLocation + " " + mySearch;
        System.out.println("디버깅용 keyword: " + keyword);
        System.out.println("디버깅용 location: " + location);


        // 사용자로부터 지역과 검색어 입력 받기

        // Google Maps 객체 생성
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
        // 지역의 좌표 얻기
        LatLng coordinates = new LatLng();
        coordinates = getCoordinates(context, location);
        if(coordinates != null) {
            myCoordinate.setName(mySearch);
            myCoordinate.setLatitude(coordinates.lat);
            myCoordinate.setLongitude(coordinates.lng);
            return myCoordinate;
        }
        return myCoordinate;
    }

    @Transactional
    public boolean mapSearchValid(String myLocation, String mySearch) {
        String keyword = mySearch;
        String location = myLocation;


        // 사용자로부터 지역과 검색어 입력 받기

        // Google Maps 객체 생성
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
        // 지역의 좌표 얻기
        LatLng coordinates = getCoordinates(context, location);
        System.out.println("========================");
        System.out.println("좌표값: " + coordinates.toString());
        System.out.println("========================");

        // 주변 장소 검색
        PlacesSearchResponse placesSearchResponse = searchNearbyPlaces(context, coordinates, keyword);
        System.out.println("검색 결과 출력");
        System.out.println("========================");

        // 검색 결과 출력
        boolean isTrue = printSearchResults(placesSearchResponse);
        return isTrue;

    }

    private static LatLng getCoordinates(GeoApiContext context, String location) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, location).await();
            if(results.length == 0){
                return null;
            }
            return results[0].geometry.location;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PlacesSearchResponse searchNearbyPlaces(GeoApiContext context, LatLng location, String keyword) {
        try {
            PlacesSearchResponse response = PlacesApi.nearbySearchQuery(context, location)
                    .radius(5000) // 반경 50km
                    .language("ko")
                    .keyword(keyword)
                    .await();

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean printSearchResults(PlacesSearchResponse response) {
        if (response.results.length != 0) {
            for (PlacesSearchResult result : response.results) {
                System.out.println("해당 위치에 존재하는 가게입니다.");
                System.out.println(result.name);
            }
            return true;
        } else {
            System.out.println("해당 위치에 존재하지 않는 가게입니다.");
            return false;
        }
    }

    public void crawlingHotel(String dest, String checkin, String checkout, String option, String adult, String room, String child ,TravelResponseDTO travelResponseDTO){

        // WebDriver 설정
        String WEB_DRIVER_ID = "webdriver.chrome.driver";
        String WEB_DRIVER_PATH = "chromedriver.exe";
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

        // WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("disable-popup-blocking");
        options.addArguments("disable-defult-apps");

        // WebDriver 인스턴스 생성
        WebDriver driver = new ChromeDriver(options);


        try {
            // 여행 지역 입력 받기
            String destination = dest;
            String encodedDestination = URLEncoder.encode(destination, "UTF-8");

            // 체크인/체크아웃 날짜 입력 받기
            String checkinDate = checkin;
            String checkoutDate = checkout;

            // 리뷰 순, 가격 순, 추천 순 옵션 입력 받기
            String orderOption = option;

            // URL 조합
            String url = "https://www.booking.com/searchresults.en-gb.html?ss=" + encodedDestination +
                    "&label=gen173nr-1BCAEoggI46AdIM1gEaH2IAQGYAQm4ARfIAQzYAQHoAQGIAgGoAgO4Ao_V-6oGwAIB0gIkZjlhYTI3MTMtNjBiYi00NGE2-LWE1MTQtZTRhOTgwMmVkMmEy2AIF4AIB" +
                    "&sid=986d075024858043272bea5d90b0d8d1&aid=304142&lang=en-gb&sb=1&src_elem=sb&src=searchresults&dest_type=region" +
                    "&checkin=" + checkinDate + "&checkout=" + checkoutDate +
                    "&group_adults=" + adult + "&no_rooms=" + room + "&group_children=" + child + "&order=" + orderOption;

            // WebDriver로 URL 열기
            driver.get(url);

            // 페이지 소스 가져오기
            String pageSource = driver.getPageSource();

            // Jsoup을 사용하여 페이지 소스 파싱
            Document document = Jsoup.parse(pageSource);
            System.out.println("====== 사용자 선호도를 고려한 호텔 검색 =====");
            // 호텔 정보가 있는 요소를 선택
            Elements hotelElements = document.select("div[data-testid=title]");
            Elements hotelPriceElements = document.select("span[data-testid=price-and-discounted-price]");
            if(hotelElements.size()!=0) {
                System.out.println("호텔 이름: " + hotelElements.get(0).text());
                System.out.println("호텔 가격: " + hotelPriceElements.get(0).text());
                travelResponseDTO.getHotel().setName(hotelElements.get(0).text());
                travelResponseDTO.getHotel().setPrice(hotelPriceElements.get(0).text());
            } else {
                System.out.println("해당 지역의 호텔을 찾지 못했습니다.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("검색 완료!");

    }

    @Transactional
    public void crawlingFlight(String depart, String dest, String departDate, String destDate,String flightType, String seatClass, String quantity, String childqty, String babyqty, TravelResponseDTO travelResponseDTO, int idx ) throws InterruptedException {
        String WEB_DRIVER_ID = "webdriver.chrome.driver";
        String WEB_DRIVER_PATH = "chromedriver.exe";


        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

        //WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("disable-popup-blocking");
        options.addArguments("disable-defult-apps");

        ChromeDriver driver = new ChromeDriver(options);

        // 쿼리스트링에 임의의 값을 넣어주었습니다. ( 서울 -> 오사카 )
        String url = String.format("https://kr.trip.com/flights/%s-to-%s/tickets-sel-dad?dcity=%s&acity=%s&ddate=%s&rdate=%s&flighttype=%s&class=%s&lowpricesource=searchform&quantity=%s&childqty=%s&childqty=%s&searchboxarg=t",
                depart, dest, depart,dest,departDate, destDate,flightType,seatClass,quantity,childqty,babyqty);

        driver.get(url);

        // 트립닷컴의 항공권 최저가 검색이 완료된 후 크롤링을 진행하기 위해서 10초동안 쉬고 크롤링을 진행합니다.
        Thread.sleep(8000);

        // [class="item-con-price"] span 의 CSS 선택자 요소가 나타날 때까지 대기합니다. (최대 10초)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));

        // 최저가 항공권의 가격을 추출하기 위한 CSS 선택자 "[class="item-con-price"] span"
        WebElement priceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class=\"item-con-price\"] span")));

        // 항공권의 출발/도착 시간을 추출하기 위한 CSS 선택자 ".flight-info-airline__timer_RWx"
        List<WebElement> flightInfoElements = driver.findElements(By.cssSelector("[class^=\"flight-info-airline__timer\"]"));

        List<WebElement> flightNameElements = driver.findElements(By.cssSelector("[class^=\"flights-name\"]"));


        // 최저가 항공권의 출발 시간을 추출합니다.
        WebElement timeElement = flightInfoElements.get(0).findElement(By.cssSelector(".time"));
        String departureTime = timeElement.getText();

        // 최저가 항공권의 도착 시간을 추출합니다.
        WebElement timeElement2 = flightInfoElements.get(1).findElement(By.cssSelector(".time"));
        String arrivalTime = timeElement2.getText();

        // 최저가 항공권의 항공편 이름을 추출합니다.
        String flightName = flightNameElements.get(0).getText();

        // 최저가 항공권의 비행 소요 시간
        WebElement durationElement = driver.findElement(By.cssSelector("[class^=\"flight-info-duration\"]"));
        String duration = durationElement.getText();

        // 결과를 출력합니다.
        System.out.println("====== 최저가 항공권에 대한 정보 ======");
        System.out.println("항공편 이름: " + flightName);
        System.out.println("가격: " + priceElement.getText());
        System.out.println("출발시간: " + departureTime);
        System.out.println("도착시간: " + arrivalTime);
        System.out.println("소요시간: " + duration);

        System.out.println("사이즈 디버깅: " + travelResponseDTO.getFlightList().size());
        travelResponseDTO.getFlightList().get(idx).setFlightname(flightName);
        travelResponseDTO.getFlightList().get(idx).setPrice(priceElement.getText());
        travelResponseDTO.getFlightList().get(idx).setDepart(departureTime);
        travelResponseDTO.getFlightList().get(idx).setArrive(arrivalTime);
        travelResponseDTO.getFlightList().get(idx).setDuration(duration);

    }

    // GPT로부터 추천 받은 요소들을 최단 거리로 변환
    public TravelResponseDTO shortestPath(TravelRequest travelRequestDTO, TravelResponse travelPlan){
        TravelResponseDTO travelResponseDTO = new TravelResponseDTO();
        travelResponseDTO.getFlightList().add(new TravelResponseDTO.Flight()); // 또는 다른 방법으로 초기화
        travelResponseDTO.getFlightList().add(new TravelResponseDTO.Flight()); // 또는 다른 방법으로 초기화

        // 출발일
        String departDate = travelRequestDTO.getCommon().getDepartureDate();
        // 도착일
        String arrivalDate = travelRequestDTO.getCommon().getArrivalDate();
        // 출발지
        String departureLocation = travelRequestDTO.getCommon().getDepartureLocation();
        // 도착지
        String destinationLocation = travelRequestDTO.getCommon().getDestinationLocation();

        // *** 항공권 옵션
        // 좌석 옵션
        String flightSeatClass = travelRequestDTO.getFlight().getSeatClass();
        // 비행 타입
        String flightFype = travelRequestDTO.getFlight().getFlightType();
        // 항공권 출발지
        String flightDepart = travelRequestDTO.getFlight().getDepart();
        // 항공권 도착지
        String flightArrive = travelRequestDTO.getFlight().getArrive();
        // 항공권 출국 날짜
        String flightDepartDate = travelRequestDTO.getFlight().getDepartDate();
        // 항공권 도착 날짜
        String flightArriveDate = travelRequestDTO.getFlight().getArriveDate();
        // 항공권 인원
        String flightQuantity = travelRequestDTO.getFlight().getQuantity();
        // 항공권 아이 인원
        String flightChildQuantity = travelRequestDTO.getFlight().getChildQuantity();
        // 항공권 영유아 인원
        String flightBabyQuantity = travelRequestDTO.getFlight().getBabyQuantity();

        // 호텔 지역
        String hotelRegion = travelRequestDTO.getHotel().getRegion();
        // 호텔 체크인 날짜
        String hotelCheckin = travelRequestDTO.getHotel().getCheckin();
        // 호텔 체크아웃 날짜
        String hotelCheckout = travelRequestDTO.getHotel().getCheckout();
        // 호텔 성인 인원
        String hotelAdult = travelRequestDTO.getHotel().getAdult();
        // 호텔 방 개수
        String hotelRoom = travelRequestDTO.getHotel().getRoom();
        // 호텔 영유아 인원
        String hotelChild = travelRequestDTO.getHotel().getChild();
        // 호텔 정렬 옵션
        String hotelSort = travelRequestDTO.getHotel().getSort();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // String -> Data 형으로 변환
        LocalDate departDateFormat = LocalDate.parse(departDate, formatter);
        LocalDate arrivalDateFormat = LocalDate.parse(arrivalDate, formatter);

        // 두 날짜 간의 일수 계산
        long daysBetween = ChronoUnit.DAYS.between(departDateFormat, arrivalDateFormat);

        // 항공권 및 호텔 추천 받기
        try {
            // 여행지로 가는 항공권 크롤링
            crawlingFlight(flightDepart, flightArrive, flightDepartDate, flightDepartDate,flightFype,flightSeatClass,flightQuantity,flightChildQuantity,flightBabyQuantity , travelResponseDTO, 0);
            // 여행지로 도착하는 항공권 크롤링
            crawlingFlight(flightArrive, flightDepart, flightArriveDate, flightArriveDate,flightFype,flightSeatClass,flightQuantity,flightChildQuantity,flightBabyQuantity , travelResponseDTO, 1);
            crawlingHotel(hotelRegion, hotelCheckin, hotelCheckout, hotelSort,hotelAdult,hotelRoom,hotelChild, travelResponseDTO);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Coordinate airportCoordinate = getCoordinate(destinationLocation, destinationLocation + " 공항");
        travelResponseDTO.getFlightList().get(0).setLongitude(String.valueOf(airportCoordinate.getLongitude()));
        travelResponseDTO.getFlightList().get(0).setLatitude(String.valueOf(airportCoordinate.getLatitude()));
        travelResponseDTO.getFlightList().get(1).setLongitude(String.valueOf(airportCoordinate.getLongitude()));
        travelResponseDTO.getFlightList().get(1).setLatitude(String.valueOf(airportCoordinate.getLatitude()));

        Coordinate hotelCoordinate = getCoordinate(destinationLocation,travelResponseDTO.getHotel().getName());
        travelResponseDTO.getHotel().setLatitude(String.valueOf(hotelCoordinate.getLatitude()));
        travelResponseDTO.getHotel().setLongitude(String.valueOf(hotelCoordinate.getLongitude()));

        List<Coordinate> realRestaurant = new ArrayList<>();
        List<Coordinate> realPlace = new ArrayList<>();
        // 식당 할루시네이션 처리
        for(int i=0; i<travelPlan.getRecommendedRestaurants().size(); i++){
            if(mapSearchValid(destinationLocation,travelPlan.getRecommendedRestaurants().get(i))){
                Coordinate restaurantCoordinate = getCoordinate(destinationLocation, travelPlan.getRecommendedRestaurants().get(i));
                realRestaurant.add(restaurantCoordinate);
                System.out.println("Restaurant 추가되었습니다!: " + restaurantCoordinate.getName() + "위도와 경도: " + restaurantCoordinate.getLatitude() + ", " + restaurantCoordinate.getLongitude());
            }
        }
        // 관광명소 할루시네이션 처리
        for(int i=0; i<travelPlan.getRecommendedPlaces().size(); i++){
            if(mapSearchValid(destinationLocation,travelPlan.getRecommendedPlaces().get(i))){
                Coordinate placeCoordinate = getCoordinate(destinationLocation, travelPlan.getRecommendedPlaces().get(i));
                realPlace.add(placeCoordinate);
                System.out.println("Place 추가되었습니다!: " + placeCoordinate.getName() + "위도와 경도: " + placeCoordinate.getLatitude() + ", " + placeCoordinate.getLongitude());
            }
        }

        System.out.println("디버깅용 realRestaurant size : " + realRestaurant.size() );
        System.out.println("디버깅용 realPlace size : " + realPlace.size() );

        // 여행지 도착 시간을 추출합니다.
        String arriveTime = travelResponseDTO.getFlightList().get(0).getArrive();
        int arriveHour = Integer.parseInt(arriveTime.split(":")[0]);

        // 귀국을 위한 항공권  탑승 시간을 추출합니다.
        String departTime = travelResponseDTO.getFlightList().get(1).getDepart();
        int departHour = Integer.parseInt(arriveTime.split(":")[0]);
        try {
            for (long i = 0; i <= daysBetween; i++) {
                if (i == 0) { // 첫 날
                    SortPath.firstDaySort(arriveHour, travelResponseDTO, airportCoordinate, realRestaurant, realPlace);
                } else if (i == daysBetween) { // 마지막 날
                    SortPath.lastDaySort(departHour, travelResponseDTO, hotelCoordinate, realRestaurant, realPlace);
                } else { // 나머지 날
                    SortPath.restDaySort(travelResponseDTO, hotelCoordinate, realRestaurant, realPlace);
                }
            }
        }
        catch (Exception e){
            throw new Exception500("서버에서 처리 과정 중 오류가 발생하였습니다.");
        }

        return travelResponseDTO;


        // GPT로부터 식당과 관광명소는 추천 받은 상태
        // 여기서 더 추가로 받아야 할 것은 항공권, 호텔 정보
        // GPT로부터 받은 식당과 관광명소에서 할루시네이션이 존재하는지 확인하기

        // 존재하지 않으면 리스트에서 삭제처리 한다.
        // 그 다음 위도 경도를 검색하기 위해 getCoordinates 를 이용해서 좌표값 받고 각 리스트 요소에 매달기
        // 첫 날은 항공권에서부터 두 번째 날은 호텔에서부터 좌표값을 기준으로 ..
        // 중간 시점에서 최단 경로를 찾을 땐 그리디 방식으로 해당 지점에서 가까운 장소/식당이 어디인지
        // 찾으면 리스트에 넣는 식으로
        // 첫 날 , 둘째 날 , 날이 바뀔 때마다 리스트를 추가하기(하루 일정은 리스트 안에 리스트가 있는 식으로..)
    }



}