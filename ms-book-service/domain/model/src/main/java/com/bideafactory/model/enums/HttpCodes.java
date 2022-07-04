package com.bideafactory.model.enums;

public enum HttpCodes {
    OK(200,"OK"),
    BAD_REQUEST(400,"Bad Request"),
    TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    SERVER_ERROR(500,"Internal Server Error");


    public final int value;
    public final String status;

    private HttpCodes(int value, String status){
        this.value = value;
        this.status = status;
    }
}
