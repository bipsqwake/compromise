package com.bipsqwake.compromise_ws.service.bggservice;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rating")
public class BggGameRating {
    @XmlElementWrapper(name = "ranks")
    @XmlElement(name = "rank")
    private List<BggGameRank> ranks; 

    public String getRanks() {
        return ranks.stream().map(BggGameRank::getText).collect(Collectors.joining("\n"));
    }
}
