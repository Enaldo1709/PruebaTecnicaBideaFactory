package com.bideafactory.model.gateways;

import com.bideafactory.model.dto.BookModel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookRepository {
    public Flux<BookModel> getAllByHouseId(String houseId);
    public Mono<BookModel> save(BookModel model);
    
}
