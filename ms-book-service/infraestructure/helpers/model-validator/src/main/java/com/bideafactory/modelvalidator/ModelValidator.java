package com.bideafactory.modelvalidator;

import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Component;

import com.bideafactory.model.dto.BookModel;
import com.bideafactory.model.exeptions.GenericException;
import com.bideafactory.model.gateways.BookValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModelValidator implements BookValidator{
    private final JSONObject schema;
    private final ObjectMapper mapper;

    private static final String LOG_ERROR_MESSAGE = "Error validating object: ";


    @Override
    public Mono<BookModel> validateBook(String book) {
        return Mono.just(book)
            .flatMap(this::getAsJson)
            .flatMap(this::validateBody)
            .map(JSONObject::toString)
            .flatMap(this::mapModel)
            .doOnSuccess(b -> log.info("Request body is valid..."));
    }

    protected Mono<JSONObject> getAsJson(String s){
        try {
            return Mono.just(new JSONObject(new JSONTokener(s)));
        } catch (JSONException e) {
            log.warn(LOG_ERROR_MESSAGE+e.getMessage());
            return Mono.error(GenericException.badRequest("Json body is invalid"));
        }
    }

    protected Mono<JSONObject> validateBody(JSONObject body){
        try {
            SchemaLoader.load(schema).validate(body);
            return Mono.just(body);
        } catch (ValidationException e) {
            log.warn(LOG_ERROR_MESSAGE+e.getMessage());
            return this.handleValidationException(e);
        }
    }

    protected Mono<JSONObject> handleValidationException(ValidationException e){
        try {
            return Mono.error(GenericException.badRequest(mapper.writeValueAsString(e.getCausingExceptions())));
        } catch (JsonProcessingException e1) {
            log.error(LOG_ERROR_MESSAGE, e1);
            return Mono.error(GenericException.serverError(e));
        }
    }


    protected Mono<BookModel> mapModel(String s){
        try {
            return Mono.just(mapper.readValue(s, BookModel.class));
        } catch (JsonProcessingException e) {
            log.warn(LOG_ERROR_MESSAGE+e.getMessage());
            return Mono.error(GenericException.badRequest("Json body is invalid"));
        }
    }
}
