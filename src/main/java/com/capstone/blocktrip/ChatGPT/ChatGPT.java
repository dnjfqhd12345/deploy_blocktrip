package com.capstone.blocktrip.ChatGPT;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatGPT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 1024, nullable=false)
    private String destination;
    @Column(length = 1024, nullable=false)
    private String travelPlan;

    @Builder
    public ChatGPT(String destination, String travelPlan){
        this.destination = destination;
        this.travelPlan = travelPlan;
    }
}
