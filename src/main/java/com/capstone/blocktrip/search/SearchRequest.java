package com.capstone.blocktrip.search;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class SearchRequest {

    @Getter
    @Setter
    public static class searchDTO {

        private String search;
    }
}
