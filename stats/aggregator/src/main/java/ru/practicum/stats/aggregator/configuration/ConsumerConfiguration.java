package ru.practicum.stats.aggregator.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@Getter
@AllArgsConstructor
@ConfigurationProperties("aggregator.kafka.consumer")
public class ConsumerConfiguration {
    private Properties properties;

    @Bean
    ConsumerClient getConsumerClient() {
        return new ConsumerClient() {

            private Consumer<String, SpecificRecordBase> сonsumer;

            @Override
            public Consumer<String, SpecificRecordBase> getConsumer() {
                if (сonsumer == null) {
                    initConsumer();
                }
                return сonsumer;
            }

            private void initConsumer() {
                сonsumer = new KafkaConsumer<>(properties);
            }

            @Override
            public void stop() {
                if (сonsumer != null) {
                    сonsumer.close();
                }
            }
        };
    }
}
