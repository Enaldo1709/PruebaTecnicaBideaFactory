package com.bideafactory.model.exeptions;

import com.bideafactory.model.enums.HttpCodes;

public class GenericException extends RuntimeException{
    private final transient int code;
    private final transient String error;

    
    public GenericException(int code, String error) {
        this.code = code;
        this.error = error;
    }
    public GenericException(String message, int code, String error) {
        super(message);
        this.code = code;
        this.error = error;
    }
    public GenericException(String message, Throwable cause, int code, String error) {
        super(message, cause);
        this.code = code;
        this.error = error;
    }


    public int getCode() {
        return code;
    }
    public String getError() {
        return error;
    }

    
    public static GenericException badRequest(String message){
        return new GenericException(message, HttpCodes.BAD_REQUEST.value, HttpCodes.BAD_REQUEST.status);
    }
    
    public static GenericException error(String message, String error, Throwable cause){
        return new GenericException(message, cause, HttpCodes.SERVER_ERROR.value, error);
    }
    public static GenericException serverError(String message){
        return new GenericException(message, HttpCodes.SERVER_ERROR.value, HttpCodes.SERVER_ERROR.status);
    }
    public static GenericException serverError(String message, Throwable cause){
        return new GenericException(message +": "+cause.getMessage(), cause, 
            HttpCodes.SERVER_ERROR.value, HttpCodes.SERVER_ERROR.status);
    }
    public static GenericException serverError(Throwable cause){
        return new GenericException(cause.getMessage(), cause, HttpCodes.SERVER_ERROR.value, HttpCodes.SERVER_ERROR.status);
    }

    public static GenericException conflict(String message){
        return new GenericException(message, HttpCodes.CONFLICT.value, HttpCodes.CONFLICT.status);
    }
    public static GenericException timeout(String message){
        return new GenericException(message, HttpCodes.TIMEOUT.value, HttpCodes.TIMEOUT.status);
    }
}
