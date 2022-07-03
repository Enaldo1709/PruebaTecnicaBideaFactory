package com.bideafactory.restconsumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bideafactory.model.dto.BookModel;
import com.bideafactory.model.exeptions.GenericException;
import com.bideafactory.model.gateways.DiscountValidator;
import com.bideafactory.restconsumer.operations.RestConsumerOperations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscountValidatorAdapter implements DiscountValidator{

    @Value("${adapter.restconsumer.discount-url}")
    private String discountUrl;

    private final RestConsumerOperations operations;

    @Override
    public Mono<BookModel> validateDiscount(BookModel model) {
        return operations.get(discountUrl)
            .filter(m -> m.containsKey("status") && m.containsKey("discountCode"))
            .any(
                m -> String.valueOf(m.get("discountCode")).equals(model.getDiscountCode()) 
                    && Boolean.valueOf(String.valueOf(m.get("status"))).equals(Boolean.TRUE)    
            )
            .flatMap(b -> Boolean.TRUE.equals(b)
                ? Mono.just(model)
                : Mono.error(GenericException.conflict("Invalid discount"))
            ).doOnSuccess(m -> log.info("The provided discount code is valid."))
            .doOnError(GenericException.class,e -> log.warn("The provided discount code is invalid or not found."));
        
    }

}
