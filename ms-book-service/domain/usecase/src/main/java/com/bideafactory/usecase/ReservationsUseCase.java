package com.bideafactory.usecase;

import java.time.Instant;
import java.util.Date;

import com.bideafactory.model.Response;
import com.bideafactory.model.dto.BookModel;
import com.bideafactory.model.dto.ErrorResponse;
import com.bideafactory.model.dto.ResponseModel;
import com.bideafactory.model.exeptions.GenericException;
import com.bideafactory.model.gateways.BookService;
import com.bideafactory.model.gateways.BookValidator;
import com.bideafactory.model.gateways.DiscountValidator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ReservationsUseCase {
    private final BookService repository;
    private final BookValidator bookValidator;
    private final DiscountValidator discountValidator;

    
    public Mono<Response> toBook(String model){
        return bookValidator.validateBook(model)
            .flatMap(this::validateDate)
            .flatMap(m -> discountValidator.validateDiscount(m).zipWith(repository.validateIsAvailable(m)))
            .flatMap(t -> repository.save(t.getT2()))
            .map(book -> ResponseModel.ok("Book Accepted."))
            .map(Response.class::cast)
            .onErrorResume(GenericException.class, e -> Mono.just(ErrorResponse.error(e)));
    } 


    protected Mono<BookModel> validateDate(BookModel model){
        if (model.getStartDate().after(model.getEndDate())){
            return Mono.error(GenericException.badRequest("The start date cannot be set after the end date."));
        } else if (model.getStartDate().before(Date.from(Instant.now()))) {
            return Mono.error(GenericException.badRequest("The start date must be after or today's date."));
        }
        return Mono.just(model);
    }    
}
