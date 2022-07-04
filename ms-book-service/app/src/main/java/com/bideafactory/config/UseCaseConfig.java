package com.bideafactory.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bideafactory.model.gateways.BookService;
import com.bideafactory.model.gateways.BookValidator;
import com.bideafactory.model.gateways.DiscountValidator;
import com.bideafactory.usecase.ReservationsUseCase;

@Configuration
public class UseCaseConfig {
    @Bean
    ReservationsUseCase reservationsUseCase(
            @Qualifier("bookServiceAdapter") BookService repository,
            BookValidator bookValidator, 
            DiscountValidator discountValidator){
        return new ReservationsUseCase(repository, bookValidator, discountValidator);
    }

    @Bean
    DateFormat getDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd");
    }

}

