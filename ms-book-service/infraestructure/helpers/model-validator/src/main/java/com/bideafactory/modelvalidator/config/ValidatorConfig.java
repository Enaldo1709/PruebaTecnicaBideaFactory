package com.bideafactory.modelvalidator.config;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Objects;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ValidatorConfig {
    @Value("${validation.regex.chars}")
    private String charRegex;
    @Value("${validation.regex.date}")
    private String dateRegex;
    @Value("${validation.schema-filename}")
    private String filename;
    
    @Bean
    ObjectMapper getObjectMapper(DateFormat format){
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(format);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        return mapper;
    }

    @Bean
    JSONObject jsonValidationSchema(ObjectMapper mapper) throws JSONException, IOException {
        return new JSONObject( 
            mapper.readTree(Objects.requireNonNull(
                    this.getClass().getClassLoader().getResourceAsStream(filename))
                ).toString()
                .replace(":regex-char", charRegex)
                .replace(":regex-date", dateRegex)
        );
        
    }

}
