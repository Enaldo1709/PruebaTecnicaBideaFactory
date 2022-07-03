package com.bideafactory.model.gateways;

import com.bideafactory.model.dto.BookModel;

import reactor.core.publisher.Mono;

public interface DiscountValidator {
    public Mono<BookModel> validateDiscount(String discountID);
}
