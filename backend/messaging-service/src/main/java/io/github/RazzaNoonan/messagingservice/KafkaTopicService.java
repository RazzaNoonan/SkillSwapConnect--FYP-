package io.github.RazzaNoonan.messagingservice;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service; 
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaTopicService {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    public void createTopic(String topicName, int partitions, short replicationFactor) {
        Map<String, Object> configs = new HashMap<>(kafkaAdmin.getConfigurationProperties());
        try (AdminClient adminClient = AdminClient.create(configs)) {
            // Check if the topic already exists
            if (!adminClient.listTopics().names().get().contains(topicName)) {
                NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
            } else {
                // Log that the topic already exists and no action was taken
            }
        } catch (Exception e) {
            // Handle exception
        }
    }

}