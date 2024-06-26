package io.github.RazzaNoonan.messagingservice;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

public class MyHandler extends TextWebSocketHandler {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // Store sessions using a thread-safe collection
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println("New WebSocket connection established");
        
        String token = getTokenFromSession(session);
        if (token != null && verifyToken(token, session)) {
            sessions.put(session.getId(), session);
        } else {
            System.out.println("Token not found or invalid. Closing session.");
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.remove(session.getId());
        System.out.println("WebSocket connection closed. Session ID: " + session.getId());
    }

    private String getTokenFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if ("token".equals(keyValue[0])) {
                    return keyValue.length > 1 ? keyValue[1] : null;
                }
            }
        }
        return null;
    }

    private boolean verifyToken(String token, WebSocketSession session) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(token).get();
            session.getAttributes().put("uid", decodedToken.getUid());
            System.out.println("Token verified successfully for UID: " + decodedToken.getUid());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	System.out.println("Received message via WebSocket: " + message.getPayload());
        publishMessageToKafka("YourKafkaTopic", message.getPayload());
    }

    private void publishMessageToKafka(String topic, String message) {
    	System.out.println("Publishing message to Kafka topic " + topic + ": " + message);
        kafkaTemplate.send(topic, message);
    }

    public static void broadcastMessage(String message) {
        TextMessage textMessage = new TextMessage(message);
        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}