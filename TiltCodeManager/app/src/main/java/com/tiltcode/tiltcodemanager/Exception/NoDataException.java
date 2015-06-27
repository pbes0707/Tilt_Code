package com.tiltcode.tiltcodemanager.Exception;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class NoDataException extends Exception {
    public NoDataException(){}
    public NoDataException(String message){ super("NoDataException : "+message); }
}
