package com.lojistik.observer;

import com.lojistik.model.DeliveryStatus;
import com.lojistik.model.Package;

/**
 * Paket takip bildirimlerini dinleyen gözlemciler için arayüz (Observer Pattern).
 */
public interface ITrackingObserver {
    void onPackageStatusChanged(Package pkg, DeliveryStatus oldStatus, DeliveryStatus newStatus, String details);
}
