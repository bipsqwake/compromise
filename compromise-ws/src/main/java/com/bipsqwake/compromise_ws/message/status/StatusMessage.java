package com.bipsqwake.compromise_ws.message.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatusMessage {

    @JsonProperty
    protected final Status status;

    public StatusMessage(Status status) {
        this.status = status;
    }

    public enum Status {
        STARTED,
        FINISHED,
        FINISHED_NO_CARD
    }
}
