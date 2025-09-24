package ru.practicum.stats.aggregator.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Getter
@AllArgsConstructor
@ConfigurationProperties("aggregator.kafka.producer")
public class ProducerConfiguration {
    private Properties properties;


    @Bean
    ProducerClient getProducerClient() {
        return new ProducerClient() {

            private final AtomicInteger counter = new AtomicInteger(0);

            private Producer<String, SpecificRecordBase> producer;


            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
            }

            private void initProducer() {

                producer = new KafkaProducer<>(properties);
            }

            @Override
            public void stop() {
                if (producer != null) {
                    producer.close();
                }
            }
        };
    }

}
