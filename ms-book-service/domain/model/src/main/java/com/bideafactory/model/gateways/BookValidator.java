package com.bideafactory.model.gateways;

import com.bideafactory.model.dto.BookModel;

import reactor.core.publisher.Mono;

public interface BookValidator {
    public Mono<BookModel> validateBook(String book);
}
