package ru.practicum.stats.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.configuration.ConsumerClient;
import ru.practicum.stats.aggregator.configuration.ProducerClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final ConsumerClient clientConsumer;
    private final ProducerClient clientProducer;
    private final EventSimilarityService service;

    @Value("${aggregator.kafka.consumer.topic}")
    private String consumerTopic;

    @Value("${aggregator.kafka.producer.topic}")
    private String producerTopic;

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {

        try {
            clientConsumer.getConsumer().subscribe(List.of(consumerTopic));

            int count = 0;

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = clientConsumer.getConsumer().poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    List<EventSimilarityAvro> eventSimilarityAvroList = service.calculate((UserActionAvro) record.value());
                    if (eventSimilarityAvroList.isEmpty()) {
                    } else {
                        for (EventSimilarityAvro eventSimilarityAvro : eventSimilarityAvroList) {
                            ProducerRecord<String, SpecificRecordBase> calculation = new ProducerRecord<>(producerTopic, eventSimilarityAvro);
                            clientProducer.getProducer().send(calculation);
                        }
                    }
                    manageOffsets(record, count, clientConsumer.getConsumer());
                    count++;
                }

                clientConsumer.getConsumer().commitAsync();
            }

        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки сообщений UserAction", e);
        } finally {
            try {
                clientProducer.getProducer().flush();
                clientConsumer.getConsumer().commitSync(currentOffsets);
            } finally {
                clientConsumer.stop();
                clientProducer.stop();
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
