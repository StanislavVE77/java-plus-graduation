package ru.practicum.stats.aggregator.configuration;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

public interface ProducerClient {

    Producer<String, SpecificRecordBase> getProducer();

    void stop();

}
