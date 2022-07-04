package com.bideafactory.model.dto;

import com.bideafactory.model.Response;
import com.bideafactory.model.exeptions.GenericException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ErrorResponse implements Response{
    private int statusCode;
    private String error;
    private String message;


    public static ErrorResponse error(GenericException e){
        return builder().statusCode(e.getCode()).error(e.getError()).message(e.getMessage()).build();
    }
}
