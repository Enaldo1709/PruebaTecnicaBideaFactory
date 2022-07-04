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
import com.bideafactory.model.gateways.BookService;
import com.bideafactory.model.gateways.BookValidator;
import com.bideafactory.model.gateways.DiscountValidator;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ReservationsUseCaseTest {

    @Mock
    private BookService repository;
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
        when(repository.validateIsAvailable(any(BookModel.class))).thenReturn(Mono.just(model));
        when(discountValidator.validateDiscount(any(BookModel.class))).thenReturn(Mono.just(model));

        Date endDate = Date.valueOf(LocalDate.of(2022, 8, 25));
        Date startDate = Date.valueOf(LocalDate.of(2022, 8, 24));
        
        when(model.getEndDate()).thenReturn(endDate);
        when(model.getStartDate()).thenReturn(startDate);

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
    void testValidateDate() {
        Date startDate = Date.valueOf(LocalDate.of(2022, 8, 24));
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

        startDate = Date.valueOf(LocalDate.of(2022, 5, 24));
        endDate = Date.valueOf(LocalDate.of(2022, 10, 22));
        when(model.getEndDate()).thenReturn(endDate);
        when(model.getStartDate()).thenReturn(startDate);

        StepVerifier.create(useCase.validateDate(model))
            .expectError(GenericException.class)
            .verify();
    }

}
