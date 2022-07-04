package com.bideafactory.restconsumer.operations;

import java.net.URI;
import java.util.HashMap;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import com.bideafactory.model.exeptions.GenericException;

import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestConsumerOperations {
    
    private final WebClient client;

    public Flux<HashMap<String,Object>> get(String url){
        return client.get()
            .uri(URI.create(url))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(new ParameterizedTypeReference<HashMap<String,Object>>() {})
            .doOnError(
                TimeoutException.class, 
                e -> log.error("Error: timeout while fetching discounts: "+e.getMessage())
            ).doOnError(
                WebClientException.class, 
                e -> log.error("Error while fetching discouts: ",e)
            )
            .onErrorMap(
                TimeoutException.class, 
                e -> GenericException.timeout("Error validating discount: Request Timeout")
            ).onErrorMap(WebClientException.class,GenericException::serverError);
    }

}
