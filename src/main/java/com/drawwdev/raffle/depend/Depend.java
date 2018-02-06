package com.drawwdev.raffle.depend;

public interface Depend<T> {

    Boolean dependent();
    
    boolean setup();

    T get();

}
