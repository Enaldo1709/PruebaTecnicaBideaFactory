package com.bideafactory.api;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.bideafactory.usecase.ReservationsUseCase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final ReservationsUseCase useCase;

    public Mono<ServerResponse> bookHouse(ServerRequest request){
        return request.bodyToMono(String.class)
            .flatMap(useCase::toBook)
            .flatMap(res -> ServerResponse.status(res.getStatusCode()).bodyValue(res));
    }
    
}
