package com.drawwdev.raffle.depend;

public interface Depend<T> {

    DependType dependType();

    Boolean dependent();
    
    boolean setup();

    T get();

}
