package com.lojistik.observer;

import com.lojistik.model.DeliveryStatus;
import com.lojistik.model.Package;

/**
 * Konsol üzerinden canlı durum güncellemelerini yazdıran Observer uygulaması.
 */
public class ConsoleNotificationObserver implements ITrackingObserver {

    @Override
    public void onPackageStatusChanged(Package pkg, DeliveryStatus oldStatus, DeliveryStatus newStatus, String details) {
        System.out.println("  🔔 [CANLI BİLDİRİM] Takip No: " + pkg.getTrackingCode()
                + " | Alıcı: " + pkg.getRecipientName()
                + " | Yeni Durum: " + newStatus.getDescription()
                + (details != null && !details.isEmpty() ? " (" + details + ")" : ""));
    }
}
