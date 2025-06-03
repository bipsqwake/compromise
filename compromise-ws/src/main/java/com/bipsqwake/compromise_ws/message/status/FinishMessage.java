package com.bipsqwake.compromise_ws.message.status;

import com.bipsqwake.compromise_ws.room.Card;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FinishMessage extends StatusMessage{
    
    @JsonProperty
    private Card selectedCard;

    public FinishMessage() {
        super(Status.FINISHED_NO_CARD);
    }

    public FinishMessage(Card selectedCard) {
        super(Status.FINISHED);
        this.selectedCard = selectedCard;
    }
}
