package com.bideafactory.r2dbcrepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.r2dbc.core.FetchSpec;
import org.springframework.test.util.ReflectionTestUtils;

import com.bideafactory.model.dto.BookModel;
import com.bideafactory.model.exeptions.GenericException;

import io.r2dbc.spi.R2dbcBadGrammarException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BookServiceAdapterTest {
    @Mock
    private DatabaseClient client;
    @Mock
    private GenericExecuteSpec executeSpec;
    @Mock
    private FetchSpec<Map<String, Object>> fetchSpec;

    private DateFormat formatter;
    private BookModel model;

    @InjectMocks
    private BookServiceAdapter adapter;

    @BeforeEach
    void setUp() throws ParseException{
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        ReflectionTestUtils.setField(adapter, "formatter", formatter);
        when(client.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.bind(anyString(), any())).thenReturn(executeSpec);
        when(executeSpec.fetch()).thenReturn(fetchSpec);

        model = BookModel.builder()
            .id("test")
            .name("test")
            .lastname("test")
            .age(22)
            .phoneNumber("4798793873")
            .startDate(formatter.parse("2022-07-03"))
            .endDate(formatter.parse("2022-07-03"))
            .houseId("test")
            .discountCode("test")
            .build();
    }


    @Test
    void testSave() {
        when(fetchSpec.rowsUpdated()).thenReturn(Mono.just(1));
        StepVerifier.create(adapter.save(model))
            .expectSubscription()
            .assertNext(actual -> assertEquals(model, actual))
            .verifyComplete();

        when(fetchSpec.rowsUpdated()).thenReturn(Mono.error(new R2dbcBadGrammarException("Error")));
        StepVerifier.create(adapter.save(model))
            .expectError(GenericException.class)
            .verify();
    }

    @Test
    void testSaveHouse() {
        when(fetchSpec.rowsUpdated()).thenReturn(Mono.just(1));
        StepVerifier.create(adapter.saveUser(model))
            .expectSubscription()
            .assertNext(actual -> assertEquals(model, actual))
            .verifyComplete();
    }

    @Test
    void testSaveUser() {
        when(fetchSpec.rowsUpdated()).thenReturn(Mono.just(1));
        StepVerifier.create(adapter.saveHouse(model))
            .expectSubscription()
            .assertNext(actual -> assertEquals(model, actual))
            .verifyComplete();
    }

    @Test
    void testValidateIsAvailable() {
        when(fetchSpec.all()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.validateIsAvailable(model))
            .expectSubscription()
            .assertNext(actual -> assertEquals(model, actual))
            .verifyComplete();

        when(fetchSpec.all()).thenReturn(Flux.just(Map.of("test","test")));
    
        StepVerifier.create(adapter.validateIsAvailable(model))
            .expectError(GenericException.class)
            .verify();
        
    }
}
