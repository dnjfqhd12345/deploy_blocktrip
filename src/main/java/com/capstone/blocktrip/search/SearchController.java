package com.capstone.blocktrip.search;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/index")
    public String indexPage(){
        return "index";
    }

    @PostMapping("/crawl")
    public String crawlWebsite(@RequestParam("search") String mySearch) {
        searchService.valid(mySearch);
        return "index";
    }

    @PostMapping("/crawl2")
    public String crawlWebsite2(@RequestParam("myLocation") String myLocation,@RequestParam("mySearch") String mySearch) {
        searchService.mapSearchValid(myLocation, mySearch);
        return "index";
    }

    @PostMapping("/flight")
    public String flightCrawl(@RequestParam("depart") String depart,@RequestParam("dest") String dest,@RequestParam("departDate") String departDate,@RequestParam("destDate") String destDate) throws InterruptedException {
       // searchService.crawlingFlight(depart, dest, departDate, destDate);
        return "index";
    }

    @PostMapping("/hotel")
    public String hotelCrawl(@RequestParam("location") String location,@RequestParam("checkin") String checkin,@RequestParam("checkout") String checkout,@RequestParam("option") String option) throws InterruptedException {
       // searchService.crawlingHotel(location, checkin, checkout, option);
        return "index";
    }

}
