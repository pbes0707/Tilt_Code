package com.tiltcode.tiltcode.Exception;

/**
 * Created by JSpiner on 2015. 6. 18..
 */

public class DisMatchException extends Exception {
    public DisMatchException(){}
    public DisMatchException(String message){ super("DisMatchException : "+message); }
}
