package com.bideafactory.model.enums;

public enum HttpCodes {
    OK(200),
    BAD_REQUEST(400),
    TIMEOUT(408),
    CONFLICT(409),
    SERVER_ERROR(500);


    public final int value;

    private HttpCodes(int value){
        this.value = value;
    }
}
