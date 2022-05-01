package com.company.nflxcli.api;


/**
 * Simple exception class for malformed API requests.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String msg){
        super(msg);
    }

}


