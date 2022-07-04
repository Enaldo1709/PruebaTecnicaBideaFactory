package com.bideafactory.model.gateways;

import com.bideafactory.model.dto.BookModel;

import reactor.core.publisher.Mono;

public interface BookService {
    public Mono<BookModel> validateIsAvailable(BookModel model);
    public Mono<BookModel> save(BookModel model);
    
}
