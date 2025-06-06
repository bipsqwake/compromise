package com.bipsqwake.compromise_ws.service.bggservice;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.ToString;

@XmlRootElement(name = "stats")
@ToString
@Getter
public class BggBoardGameStats {
    @XmlAttribute(name = "minplayers")
    private int minPlayers;
    @XmlAttribute(name = "maxplayers")
    private int maxPlayers;
    @XmlAttribute(name = "minplaytime")
    private int minPlayTime;
    @XmlAttribute(name = "maxplaytime")
    private int maxPlayTime;
}
