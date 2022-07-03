package com.bideafactory.restconsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.bideafactory.model.dto.BookModel;
import com.bideafactory.model.exeptions.GenericException;
import com.bideafactory.restconsumer.operations.RestConsumerOperations;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DiscountValidatorAdapterTest {
    
    @Mock
    private RestConsumerOperations operations;
    
    @InjectMocks
    private DiscountValidatorAdapter adapter;

    @BeforeEach
    void setUp(){
        String url = "http://test.test/test";
        ReflectionTestUtils.setField(adapter, "discountUrl", url);
    }

    @Test
    void testValidateDiscountSuccess() {
        Flux<HashMap<String,Object>> flux = Flux.just(
            new HashMap<>(Map.of("status",true,"discountCode","test"))
        );
        when(operations.get(anyString())).thenReturn(flux);
        BookModel model = mock(BookModel.class);
        when(model.getDiscountCode()).thenReturn("test");

        StepVerifier.create(adapter.validateDiscount(model))
            .expectSubscription()
            .assertNext(actual -> assertEquals(model, actual))
            .verifyComplete();
    }
    @Test
    void testValidateDiscountFailedInvalid() {
        Flux<HashMap<String,Object>> flux = Flux.just(
            new HashMap<>(Map.of("test",true,"discountCode","test"))
        );
        when(operations.get(anyString())).thenReturn(flux);
        BookModel model = mock(BookModel.class);
        when(model.getDiscountCode()).thenReturn("test");

        StepVerifier.create(adapter.validateDiscount(model))
            .expectError(GenericException.class)
            .verify();

        Flux<HashMap<String,Object>> flux2 = Flux.just(
            new HashMap<>(Map.of("status",true,"discountCode","test1"))
        );
        when(operations.get(anyString())).thenReturn(flux2);

        StepVerifier.create(adapter.validateDiscount(model))
            .expectError(GenericException.class)
            .verify();
        Flux<HashMap<String,Object>> flux3 = Flux.just(
            new HashMap<>(Map.of("status",true,"test","test1"))
        );
        when(operations.get(anyString())).thenReturn(flux3);

        StepVerifier.create(adapter.validateDiscount(model))
            .expectError(GenericException.class)
            .verify();
        Flux<HashMap<String,Object>> flux4 = Flux.just(
            new HashMap<>(Map.of("status",false,"discountCode","test"))
        );
        when(operations.get(anyString())).thenReturn(flux4);

        StepVerifier.create(adapter.validateDiscount(model))
            .expectError(GenericException.class)
            .verify();
    }
}
