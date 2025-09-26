package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.configuration.EventSimilarityDeserializer;
import ru.practicum.stats.analyzer.dal.service.EventSimilarityService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityProcessor implements Runnable {

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(100);

    private final EventSimilarityService eventSimilarityService;

    @Autowired
    private Environment env;

    @Override
    public void run() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "EventSimilarityConsumer");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("analyzer.kafka.consumer2.properties.group.id"));
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("analyzer.kafka.bootstrap-servers"));
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class.getCanonicalName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventSimilarityDeserializer.class.getCanonicalName());


        KafkaConsumer<String, SpecificRecordBase> consumer = new KafkaConsumer<>(properties);
        String topic = env.getProperty("analyzer.kafka.consumer2.topic");

        try {
            consumer.subscribe(List.of(topic));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    eventSimilarityService.handleRecord((EventSimilarityAvro) record.value());
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки записи", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private static void manageOffsets(ConsumerRecord<String, SpecificRecordBase> record, int count, Consumer<String, SpecificRecordBase> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}
