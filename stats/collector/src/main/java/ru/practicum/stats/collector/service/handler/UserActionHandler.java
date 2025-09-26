package ru.practicum.stats.collector.service.handler;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.useraction.UserActionProto;
import ru.practicum.stats.collector.service.UserActionKafkaProducer;

import java.time.Instant;

@Component
public class UserActionHandler {
    protected final UserActionKafkaProducer producer;

    public UserActionHandler(UserActionKafkaProducer producer) {
        this.producer = producer;
    }

    protected UserActionAvro mapToAvro(UserActionProto userActionProto) {
        return UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(ActionTypeAvro.valueOf(userActionProto.getActionType().name()))
                .setTimestamp(Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(), userActionProto.getTimestamp().getNanos()))
                .build();
    }

    public void handle(UserActionProto userActionProto) {
        UserActionAvro record = mapToAvro(userActionProto);
        producer.sendUserActionToKafka(record);
    }

}
