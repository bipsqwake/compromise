package com.bipsqwake.compromise_ws.service.bggservice;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.ToString;

@XmlRootElement(name = "item")
@ToString
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class BggBoardGame {

    @XmlElement
    private String name;
    @XmlElement(name = "originalname")
    private String originalName;
    @XmlElement(name = "yearpublished")
    private String yearPublished;
    @XmlElement
    private String image;
    @XmlElement
    private BggBoardGameStats stats;

    public boolean isSuitableForPlayersNum(int playersNum) {
        return (playersNum >= stats.getMinPlayers() && playersNum <= stats.getMaxPlayers()) ||
        (stats.getMinPlayers() == 0 && stats.getMaxPlayers() == 0);
    }

    public String getRankDescription() {
        return stats.getRating().getRanks();
    }
}
