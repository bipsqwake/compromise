package com.bipsqwake.compromise_ws.room;

import java.util.Map;

public record CardDecisionHolder(String cardId, Map<String, Decision> decisions) {
    
}
