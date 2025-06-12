package com.wedding.broker.model;

public class ErrorOrderMessage {
    private String message;
    private boolean isError;

    public ErrorOrderMessage(){
        isError = false;
        message = "The following services could not be booked because someone else has just reserved them: ";
    }
    public void addToMessage(String service){
        message = message + " "+service;
    }
    public String getMessage() { return message;}
    public boolean isError() {return isError;}
    public void setErrorTrue() {isError = true;}
}
