package com.wedding.broker.model;

public class ErrorConfirmMessage {
    private String message;
    private boolean isError;

    public ErrorConfirmMessage(){
        isError = false;
        message = "The following services could not be reached due to a supplier failure for the: ";
    }
    public void addToMessage(String service){
        message = message + " "+service;
    }
    public String getMessage() { return message;}
    public boolean isError() {return isError;}
    public void setErrorTrue() {isError = true;}
}


