package com.bideafactory.model.dto;

import com.bideafactory.model.Response;
import com.bideafactory.model.enums.HttpCodes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ResponseModel implements Response{
    private int statusCode;
    private String message;

    public static ResponseModel ok(String message){
        return builder().statusCode(HttpCodes.OK.value).message(message).build();
    }
}
