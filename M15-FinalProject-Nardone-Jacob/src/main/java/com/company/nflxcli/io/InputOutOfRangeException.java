package com.company.nflxcli.io;


/**
 * Simple exception class for number inputs from the user that are outside the range of possible values.
 */
public class InputOutOfRangeException extends RuntimeException {

    public InputOutOfRangeException(String msg){
        super(msg);
    }

}

