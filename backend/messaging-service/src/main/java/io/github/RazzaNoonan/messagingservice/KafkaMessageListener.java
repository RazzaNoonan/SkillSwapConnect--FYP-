package io.github.RazzaNoonan.messagingservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageListener {

    @KafkaListener(topics = "YourKafkaTopic", groupId = "yourGroupId")
    public void listen(String message) {
        // Use MyHandler to broadcast the message to all connected WebSocket clients
        MyHandler.broadcastMessage(message);
    }
}