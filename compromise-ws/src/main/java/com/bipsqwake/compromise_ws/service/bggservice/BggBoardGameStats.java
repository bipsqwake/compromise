package com.bipsqwake.compromise_ws.service.bggservice;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "stats")
public class BggBoardGameStats {
    @XmlElement(name = "minplayers")
    private int minPlayers;
    @XmlElement(name = "maxplayers")
    private int maxPlayers;
    @XmlElement(name = "minplaytime")
    private int minPlayTime;
    @XmlElement(name = "maxplaytime")
    private int maxPlayTime;
}
