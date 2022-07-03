package com.bideafactory.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bideafactory.model.dto.BookModel;
import com.bideafactory.model.exeptions.GenericException;
import com.bideafactory.model.gateways.BookRepository;
import com.bideafactory.model.gateways.BookValidator;
import com.bideafactory.model.gateways.DiscountValidator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ReservationsUseCaseTest {

    @Mock
    private BookRepository repository;
    @Mock
    private BookValidator bookValidator;
    @Mock
    private DiscountValidator discountValidator; 

    @InjectMocks
    private ReservationsUseCase useCase;

    @Test
    void testToBookSuccess() {
        BookModel model = mock(BookModel.class);
        when(bookValidator.validateBook(anyString())).thenReturn(Mono.just(model));
        when(repository.save(any(BookModel.class))).thenReturn(Mono.just(model));
        when(repository.getAllByHouseId(anyString())).thenReturn(Flux.just(model));
        when(discountValidator.validateDiscount(anyString())).thenReturn(Mono.just(model));

        Date endDate = Date.valueOf(LocalDate.of(2022, 5, 22));
        Date startDate = Date.valueOf(LocalDate.of(2022, 4, 24));
        
        when(model.getEndDate()).thenReturn(endDate);
        when(model.getStartDate()).thenReturn(startDate);
        when(model.getDiscountCode()).thenReturn("test");
        when(model.getHouseId()).thenReturn("test");

        StepVerifier.create(useCase.toBook("test"))
            .expectSubscription()
            .assertNext(res -> assertEquals(200, res.getStatusCode()))
            .verifyComplete();
    }
    
    @Test
    void testToBookFailed() {
        when(bookValidator.validateBook(anyString())).thenReturn(Mono.error(
            GenericException.badRequest("Missing required property 'houseId'.")
        ));
        StepVerifier.create(useCase.toBook("test"))
            .expectSubscription()
            .assertNext(res -> assertEquals(400, res.getStatusCode()))
            .verifyComplete();
    }
    
    @Test
    void testValidateHouseIsAvailableSuccess() {
        Date endDate = Date.valueOf(LocalDate.of(2022, 5, 22));
        BookModel model = mock(BookModel.class);

        when(repository.getAllByHouseId(anyString())).thenReturn(Flux.just(model));
        when(model.getEndDate()).thenReturn(endDate);
        when(model.getHouseId()).thenReturn("test");


        StepVerifier.create(useCase.validateHouseIsAvailable(model))
            .expectSubscription()
            .assertNext(m -> assertEquals(model, m))
            .verifyComplete();

        Date startDate = Date.valueOf(LocalDate.of(2022, 10, 24));
        endDate = Date.valueOf(LocalDate.of(2022, 11, 22));
        when(model.getEndDate()).thenReturn(endDate);
        when(model.getStartDate()).thenReturn(startDate);

        StepVerifier.create(useCase.validateHouseIsAvailable(model))
            .expectSubscription()
            .assertNext(m -> assertEquals(model, m))
            .verifyComplete();
            
        when(repository.getAllByHouseId(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(useCase.validateHouseIsAvailable(model))
            .expectSubscription()
            .assertNext(m -> assertEquals(model, m))
            .verifyComplete();
    }
    @Test
    void testValidateHouseIsAvailableFailed() {
        Date startDate = Date.valueOf(LocalDate.of(2022, 5, 24));
        Date endDate = Date.valueOf(LocalDate.of(2022, 10, 22));
        BookModel model = mock(BookModel.class);
        when(repository.getAllByHouseId(anyString())).thenReturn(Flux.just(model));
        when(model.getEndDate()).thenReturn(endDate);
        when(model.getStartDate()).thenReturn(startDate);
        when(model.getHouseId()).thenReturn("test");

        StepVerifier.create(useCase.validateHouseIsAvailable(model))
            .expectError(GenericException.class)
            .verify();
    }

    @Test
    void testIsAvailable() {
        Long expected = 0L;
        StepVerifier.create(useCase.isAvailable(expected))
            .expectSubscription()
            .assertNext(actual -> assertEquals(expected, actual))
            .verifyComplete();
        
        StepVerifier.create(useCase.isAvailable(1L))
            .expectError(GenericException.class)
            .verify();

        
    }   

    @Test
    void testValidateDate() {
        Date startDate = Date.valueOf(LocalDate.of(2022, 5, 24));
        Date endDate = Date.valueOf(LocalDate.of(2022, 10, 22));
        BookModel model = mock(BookModel.class);
        when(model.getEndDate()).thenReturn(endDate);
        when(model.getStartDate()).thenReturn(startDate);

        StepVerifier.create(useCase.validateDate(model))
            .expectSubscription()
            .assertNext(actual -> assertEquals(model, actual))
            .verifyComplete();

        startDate = Date.valueOf(LocalDate.of(2022, 11, 24));
        endDate = Date.valueOf(LocalDate.of(2022, 10, 22));
        when(model.getEndDate()).thenReturn(endDate);
        when(model.getStartDate()).thenReturn(startDate);

        StepVerifier.create(useCase.validateDate(model))
            .expectError(GenericException.class)
            .verify();
    }

    @Test
    void testCheckDatesNotAvailable(){
        Date startDate = Date.valueOf(LocalDate.of(2022, 05, 24));
        Date endDate = Date.valueOf(LocalDate.of(2022, 11, 22));
        BookModel model = mock(BookModel.class);
        
        when(model.getEndDate()).thenReturn(endDate);
        when(model.getStartDate()).thenReturn(startDate);
        
        assertEquals(true, useCase.checkDatesNotAvailable.test(model));
        
        endDate = Date.valueOf(LocalDate.of(2022, 05, 22));
        when(model.getEndDate()).thenReturn(endDate);
        
        assertEquals(false, useCase.checkDatesNotAvailable.test(model));
        
        endDate = Date.valueOf(LocalDate.of(2022, 11, 22));
        startDate = Date.valueOf(LocalDate.of(2022, 9, 24));
        when(model.getEndDate()).thenReturn(endDate);
        when(model.getStartDate()).thenReturn(startDate);
        assertEquals(false, useCase.checkDatesNotAvailable.test(model));
    }
}
