package ru.practicum.stats.analyzer.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.grpc.stats.analyzer.RecommendationsControllerGrpc;
import ru.practicum.grpc.stats.recommendations.InteractionsCountRequestProto;
import ru.practicum.grpc.stats.recommendations.RecommendedEventProto;
import ru.practicum.grpc.stats.recommendations.SimilarEventsRequestProto;
import ru.practicum.grpc.stats.recommendations.UserPredictionsRequestProto;
import ru.practicum.stats.analyzer.service.handler.EventSimilarityHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@GrpcService
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final EventSimilarityHandler eventSimilarityHandler;

    public RecommendationsController(EventSimilarityHandler eventSimilarityHandler) {
        this.eventSimilarityHandler = eventSimilarityHandler;
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("GRPC message InteractionsCountRequestProto received");
            Set<Long> eventIds = new HashSet<>(request.getEventIdList());
            List<RecommendedEventProto> results = eventSimilarityHandler.getInteractionsCount(eventIds);

            for (int i = 0; i < request.getEventIdCount(); i++) {
                responseObserver.onNext(results.get(i));
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("GRPC message UserPredictionsRequestProto received");
            List<RecommendedEventProto> results = eventSimilarityHandler.getRecommendationsForUser(request.getUserId(), request.getMaxResults());

            for (int i = 0; i < results.size(); i++) {
                responseObserver.onNext(results.get(i));
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }

    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("GRPC message SimilarEventsRequestProto received");
            List<RecommendedEventProto> results = eventSimilarityHandler.getSimilarEvents(request.getUserId(), request.getEventId(), request.getMaxResults());

            for (int i = 0; i < results.size(); i++) {
                responseObserver.onNext(results.get(i));
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }

    }
}