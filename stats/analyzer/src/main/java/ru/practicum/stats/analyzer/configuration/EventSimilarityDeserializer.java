package ru.practicum.stats.analyzer.configuration;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public class EventSimilarityDeserializer extends BaseAvroDeserializer<EventSimilarityAvro>  {
    public EventSimilarityDeserializer() {

        super(EventSimilarityAvro.getClassSchema());
    }

}