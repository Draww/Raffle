package com.drawwdev.raffle;

public class RaffleException extends Exception {

    private String message;

    public RaffleException(String message) {
        super(message);
        this.message = message;
    }

}
