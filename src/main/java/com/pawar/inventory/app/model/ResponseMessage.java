package com.pawar.inventory.app.model;

public class ResponseMessage {

    private String responseMessage;

    public ResponseMessage() {
    }

    public ResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;

    }

    @Override
    public String toString() {
        return "ResponseMessage [responseMessage=" + responseMessage + "]";
    }

}
