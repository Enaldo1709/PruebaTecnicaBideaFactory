package com.bideafactory.restconsumer.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientException;

import com.bideafactory.model.exeptions.GenericException;

import io.netty.handler.timeout.TimeoutException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes","unchecked"})
class RestConsumerOperationsTest {
    
    @Mock
    private WebClient client;

    @Mock
    private RequestHeadersUriSpec uriSpec;
    @Mock
    private RequestHeadersSpec headersSpec;
    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    private RestConsumerOperations operations;
    
    @Test
    void testGetSuccess() {
        String url = "http://test.test/test";
        Flux<HashMap<String,Object>> flux = Flux.just(
            new HashMap<>(Map.of("test","test")),
            new HashMap<>(Map.of("test","test")),
            new HashMap<>(Map.of("test","test")),
            new HashMap<>(Map.of("test","test"))
        );

        when(client.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.accept(any(MediaType.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(any(ParameterizedTypeReference.class)))
            .thenReturn(flux);

        StepVerifier.create(operations.get(url))
            .expectSubscription()
            .assertNext(actual -> assertEquals("test", actual.get("test")))
            .expectNextCount(3L)
            .verifyComplete();

    }
    @Test
    void testGetFailedTimeout() {
        String url = "http://test.test/test";
        TimeoutException ex = mock(TimeoutException.class);

        when(client.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.accept(any(MediaType.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(any(ParameterizedTypeReference.class)))
            .thenReturn(Flux.error(ex));
        when(ex.getMessage()).thenReturn("Request timeout");

        StepVerifier.create(operations.get(url))
            .expectError(GenericException.class)
            .verify();

    }
    @Test
    void testGetFailedException() {
        String url = "http://test.test/test";
        WebClientException ex = mock(WebClientException.class);

        when(client.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.accept(any(MediaType.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(any(ParameterizedTypeReference.class)))
            .thenReturn(Flux.error(() -> ex));

        StepVerifier.create(operations.get(url))
            .expectError(GenericException.class)
            .verify();

    }
}
