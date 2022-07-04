package com.bideafactory.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.bideafactory.model.Response;
import com.bideafactory.usecase.ReservationsUseCase;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private ReservationsUseCase useCase;

    @Mock
    private Response response;

    @Mock 
    private ServerRequest request;

    @InjectMocks
    private Handler handler;

    @Test
    @SuppressWarnings("unchecked")
    void testBookHouse() {
        when(request.bodyToMono(any(Class.class))).thenReturn(Mono.just("test"));
        when(useCase.toBook(anyString())).thenReturn(Mono.just(response));
        when(response.getStatusCode()).thenReturn(200);

        StepVerifier.create(handler.bookHouse(request))
            .expectSubscription()
            .assertNext(actual -> assertEquals(200, actual.rawStatusCode()))
            .verifyComplete();
    }
}
