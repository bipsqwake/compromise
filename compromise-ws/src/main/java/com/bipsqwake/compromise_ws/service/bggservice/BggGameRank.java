package com.bipsqwake.compromise_ws.service.bggservice;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rank")
public class BggGameRank {
    @XmlAttribute(name = "friendlyname")
    private String friendlyName;
    @XmlAttribute
    private String value;

    public String getText() {
        return friendlyName + ": " + value;
    }
}
