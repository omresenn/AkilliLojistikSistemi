package com.lojistik.observer;

import com.lojistik.model.DeliveryStatus;
import com.lojistik.model.Package;

/**
 * Durum değişikliklerini gözlemcilere bildiren Subject arayüzü (Observer Pattern).
 */
public interface ITrackingSubject {
    void registerObserver(ITrackingObserver observer);
    void removeObserver(ITrackingObserver observer);
    void notifyObservers(Package pkg, DeliveryStatus oldStatus, DeliveryStatus newStatus, String details);
}
