package com.bideafactory.usecase;

import java.time.Instant;
import java.util.Date;
import java.util.function.Predicate;

import com.bideafactory.model.Response;
import com.bideafactory.model.dto.BookModel;
import com.bideafactory.model.dto.ErrorResponse;
import com.bideafactory.model.dto.ResponseModel;
import com.bideafactory.model.exeptions.GenericException;
import com.bideafactory.model.gateways.BookRepository;
import com.bideafactory.model.gateways.BookValidator;
import com.bideafactory.model.gateways.DiscountValidator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ReservationsUseCase {
    private final BookRepository repository;
    private final BookValidator bookValidator;
    private final DiscountValidator discountValidator;

    public Mono<Response> toBook(String model){
        return bookValidator.validateBook(model)
            .flatMap(this::validateDate)
            .flatMap(book -> discountValidator.validateDiscount(book.getDiscountCode()))
            .flatMap(this::validateHouseIsAvailable)
            .flatMap(repository::save)
            .map(book -> ResponseModel.ok("Book Accepted."))
            .map(Response.class::cast)
            .onErrorResume(GenericException.class, e -> Mono.just(ErrorResponse.error(e)));
    } 

    protected Mono<BookModel> validateDate(BookModel model){
        if (model.getStartDate().after(model.getEndDate())){
            return Mono.error(GenericException.badRequest("The start date cannot be set after the end date."));
        }
        return Mono.just(model);
    }

    protected Mono<BookModel> validateHouseIsAvailable(BookModel model){
        return repository.getAllByHouseId(model.getHouseId())
            .filter(checkDatesNotAvailable)
            .count()
            .flatMap(this::isAvailable)
            .map(l -> model);
            
    }

    protected Predicate<BookModel> checkDatesNotAvailable = m -> m.getEndDate().after(Date.from(Instant.now())) 
        && m.getStartDate().before(Date.from(Instant.now()));

    protected Mono<Long> isAvailable(Long count){
        if (count > 0) {
            return Mono.error(GenericException.conflict("The house is reserved on the required date."));
        }
        return Mono.just(count);
    }
}
