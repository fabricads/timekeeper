package br.com.redhat.consulting.util;

public class GeneralException extends Exception {
    
    public GeneralException(String msg) {
        super(msg);
    }
    
    public GeneralException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    

}
