package com.tiltcode.tiltcode.Exception;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class DataTypeException extends Exception {
    public DataTypeException(){}
    public DataTypeException(String message){ super("DataTypeException : "+message); }
}
