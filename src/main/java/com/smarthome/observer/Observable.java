package com.smarthome.observer;


// Observable contract for subscribe, unsubscribe, and event notification.
public interface Observable {
    
    void attach(Observer observer);

    
    void detach(Observer observer);

    
    void notifyObservers(String event);
}
