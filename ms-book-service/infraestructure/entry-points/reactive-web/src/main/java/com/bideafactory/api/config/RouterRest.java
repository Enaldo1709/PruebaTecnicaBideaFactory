package com.bideafactory.api.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.bideafactory.api.Handler;

@Configuration
public class RouterRest {

    @Value("${server.servlet.context-path}")
    private String contextPath;
    
    @Bean
    RouterFunction<ServerResponse> getRoutes(Handler handler){
        return route(POST(contextPath), handler::bookHouse);
    }

}
