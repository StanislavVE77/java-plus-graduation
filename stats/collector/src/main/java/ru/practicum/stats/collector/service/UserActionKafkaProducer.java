package ru.practicum.stats.collector.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.stats.collector.configuration.UserActionClient;
import org.springframework.core.env.Environment;

@Slf4j
@Service
@AllArgsConstructor
public class UserActionKafkaProducer {
    private final UserActionClient clientProducer;

    @Autowired
    private Environment env;

    public void sendUserActionToKafka(SpecificRecordBase message) {

        String producerTopic = env.getProperty("collector.kafka.producer.topic");

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(producerTopic, message);
        log.info("--> Отправка в Kafka сообщения (UserActionAvro): {}", record);
        clientProducer.getProducer().send(record);
    }

}
