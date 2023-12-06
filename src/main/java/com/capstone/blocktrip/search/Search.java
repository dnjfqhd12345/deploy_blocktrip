package com.capstone.blocktrip.search;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="search_tb")
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 100, nullable=false)
    private String searchKeyword;

    @Column(length = 256, nullable = false)
    private String searchResult;

    @Builder
    public Search(String searchKeyword, String searchResult){
        this.searchKeyword = searchKeyword;
        this.searchResult = searchResult;
    }

}
