package ru.practicum.stats.collector.configuration;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@Getter
@AllArgsConstructor
@ConfigurationProperties("collector.kafka.producer")
public class UserActionProducerConfiguration {
    private Properties properties;


    @Bean
    UserActionClient getProducerClient() {
        return new UserActionClient() {

            private Producer<String, SpecificRecordBase> producer;

            public Producer<String, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
            }

            private void initProducer() {

                producer = new KafkaProducer<>(properties);
            }

            public void stop() {
                if (producer != null) {
                    producer.close();
                }
            }
        };
    }

}
