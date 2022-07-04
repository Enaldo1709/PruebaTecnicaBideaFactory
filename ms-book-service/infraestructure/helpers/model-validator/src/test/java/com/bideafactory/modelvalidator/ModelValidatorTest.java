package com.bideafactory.modelvalidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.bideafactory.model.exeptions.GenericException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("deprecation")
class ModelValidatorTest {

    private JSONObject schema;
    private ObjectMapper mapper;

    private ModelValidator validator;

    @BeforeEach
    void setUp() throws JSONException, IOException{
        mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);

        String schemaString = mapper.readTree(Objects.requireNonNull(
            this.getClass().getClassLoader().getResourceAsStream("book-schema.json"))
        ).toString()
        .replace(":regex-char", "^([^$<>'\\\"/\\t\\n\\r&{}]*)$")
        .replace(":regex-date", "^(19[0-9]{2}|2[0-9]{3})-(0[1-9]|1[012])-([123]0|[012][1-9]|31)$");

        schema = new JSONObject(schemaString);
        validator = new ModelValidator(schema, mapper);
    }

    @Test
    void testGetAsJson() {
        String body = "{\"test\":123}";

        StepVerifier.create(validator.getAsJson(body))
            .expectSubscription()
            .assertNext(j -> assertEquals(123, j.getInt("test")))
            .verifyComplete();

        body = "{\"test\":123";

        StepVerifier.create(validator.getAsJson(body))
            .expectError(GenericException.class)
            .verify();
    }

    @Test
    void testHandleValidationException() throws JsonProcessingException {
        ValidationException exception = new ValidationException(
            SchemaLoader.load(schema),
            "error invalid",
            List.of(new ValidationException(SchemaLoader.load(schema), String.class, new Object()))
        );

        StepVerifier.create(validator.handleValidationException(exception))
            .expectError(GenericException.class)
            .verify();

        exception = new ValidationException(SchemaLoader.load(schema), String.class, new Object());
        StepVerifier.create(validator.handleValidationException(exception))
            .expectError(GenericException.class)
            .verify();

        ObjectMapper mockMapper = mock(ObjectMapper.class);
        when(mockMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
        ReflectionTestUtils.setField(validator, "mapper", mockMapper);
        StepVerifier.create(validator.handleValidationException(exception))
            .expectError(GenericException.class)
            .verify();

    }

    @Test
    void testMapModel() {
        String body = "{\"id\":\"test\",\"name\":\"test\",\"lastname\":\"test\",\"age\":33,"+
            "\"phoneNumber\":\"test\",\"startDate\":\"2022-03-04\",\"endDate\":\"2022-03-04\","
            +"\"houseId\":\"test\",\"discountCode\":\"test\"}";
        
        StepVerifier.create(validator.mapModel(body))
            .expectSubscription()
            .assertNext(actual -> {
                assertEquals(33, actual.getAge());
                assertEquals("test", actual.getName());
                assertEquals("test", actual.getHouseId());
                assertEquals(Date.valueOf(LocalDate.of(2022, 3, 4)), actual.getStartDate());
            }).verifyComplete();
        
        body = "{\"id\":\"test\",\"name\":\"test\",\"lastname\":\"test\",\"age\":33,"+
            "\"phoneNumber\":\"test\",\"startDate\":\"2022-03-04\",\"endDate\":\"2022-03-04\","
            +"\"houseId\":\"test\",\"discountCode\":\"test\"";

        StepVerifier.create(validator.mapModel(body))
            .expectError(GenericException.class)
            .verify();
    } 

    @Test
    void testValidateBody() {
        String body = "{\"id\":\"test\",\"name\":\"test\",\"lastname\":\"test\",\"age\":33,"+
            "\"phoneNumber\":\"test\",\"startDate\":\"2022-03-04\",\"endDate\":\"2022-03-04\","
            +"\"houseId\":\"test\",\"discountCode\":\"test\"}";

        JSONObject jsonBody = new JSONObject(body);
        StepVerifier.create(validator.validateBody(jsonBody))
            .expectSubscription()
            .assertNext(actual -> assertEquals(jsonBody, actual))
            .verifyComplete();

        body = "{\"id\":\"t'est\",\"name\":\"te$/st\",\"lastname\":\"test\",\"age\":33,"+
            "\"phoneNumber\":\"test\",\"startDate\":\"2022-03-04\",\"endDate\":\"2022-03-04\","
            +"\"houseId\":\"test\",\"discountCode\":\"test\"}";
        
        JSONObject jsonBody2 = new JSONObject(body);
        StepVerifier.create(validator.validateBody(jsonBody2))
            .expectError(GenericException.class)
            .verify();
    }

    @Test
    void testValidateBook() {
        String body = "{\"id\":\"test\",\"name\":\"test\",\"lastname\":\"test\",\"age\":33,"+
            "\"phoneNumber\":\"test\",\"startDate\":\"2022-03-04\",\"endDate\":\"2022-03-04\","
            +"\"houseId\":\"test\",\"discountCode\":\"test\"}";

        StepVerifier.create(validator.validateBook(body))
            .expectSubscription()
            .assertNext(actual -> {
                assertEquals(33, actual.getAge());
                assertEquals("test", actual.getName());
                assertEquals("test", actual.getHouseId());
                assertEquals(Date.valueOf(LocalDate.of(2022, 3, 4)), actual.getStartDate());
            }).verifyComplete();
    }
}
