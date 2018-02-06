package com.drawwdev.raffle;

public enum RaffleType {

    MONEY("Numeral"), ITEM("Item"), PERMISSION("Text"), LEVEL("Numeral"), EXP("Numeral");

    private String data;

    RaffleType(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
