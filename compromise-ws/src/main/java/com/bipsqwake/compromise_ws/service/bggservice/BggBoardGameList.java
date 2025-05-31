package com.bipsqwake.compromise_ws.service.bggservice;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

@XmlRootElement(name = "items")
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class BggBoardGameList {
    
    @XmlElement(name = "item")
    private List<BggBoardGame> games;
    

}
