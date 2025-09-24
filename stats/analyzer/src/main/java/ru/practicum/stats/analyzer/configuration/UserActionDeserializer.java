package ru.practicum.stats.analyzer.configuration;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public class UserActionDeserializer extends BaseAvroDeserializer<UserActionAvro>  {
    public UserActionDeserializer() {

        super(UserActionAvro.getClassSchema());
    }

}
