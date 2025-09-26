package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.grpc.stats.collector.UserActionControllerGrpc;
import ru.practicum.grpc.stats.useraction.UserActionProto;

@Slf4j
@Component
public class CollectorClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorStub;

    public void sendUserActionToCollector(UserActionProto record) {
        log.info("Отправляю данные: {}", record.getAllFields());
        collectorStub.collectUserAction(record);
    }
}