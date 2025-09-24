package ru.practicum.stats.collector.configuration;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

public interface UserActionClient {

    Producer<String, SpecificRecordBase> getProducer();

    void stop();
}
