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

    

}
