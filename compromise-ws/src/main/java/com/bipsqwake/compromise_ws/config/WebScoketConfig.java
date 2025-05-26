package com.bipsqwake.compromise_ws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebScoketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("appconfig.allowed-origins-pattern")
    String allowedOriginsPattern;

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.setUserDestinationPrefix("/user");
        registry.setApplicationDestinationPrefixes("/server");
    }
    
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/compromise").setAllowedOriginPatterns("*");
    }
}
