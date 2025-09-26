package ru.practicum.stats.aggregator.configuration;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;

public interface ConsumerClient {

    Consumer<String, SpecificRecordBase> getConsumer();

    void stop();

}