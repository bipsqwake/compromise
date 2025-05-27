package com.bipsqwake.compromise_ws.message;

import com.bipsqwake.compromise_ws.room.Card;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FinishMessage {
    
    @JsonProperty
    private Status status;
    @JsonProperty
    private Card selectedCard;

    public FinishMessage() {
        
    }

    public FinishMessage(Card selectedCard) {
        this.status = Status.FINISHED;
        this.selectedCard = selectedCard;
    }

    public enum Status {
        FINISHED
    }
}
