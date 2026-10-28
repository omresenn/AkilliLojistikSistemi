package com.lojistik.observer;

import com.lojistik.model.DeliveryStatus;
import com.lojistik.model.Package;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Paket durum değişikliklerini yöneten ve kayıtlı gözlemcilere haber veren yayıncı (Subject).
 */
public class DeliveryTrackingManager implements ITrackingSubject {
    private final List<ITrackingObserver> observers = new ArrayList<>();
    private final List<String> auditLogs = new ArrayList<>();

    @Override
    public synchronized void registerObserver(ITrackingObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public synchronized void removeObserver(ITrackingObserver observer) {
        observers.remove(observer);
    }

    @Override
    public synchronized void notifyObservers(Package pkg, DeliveryStatus oldStatus, DeliveryStatus newStatus, String details) {
        String logEntry = String.format("[%tF %<tT] Kargo #%s (%s): %s -> %s | %s",
                System.currentTimeMillis(),
                pkg.getTrackingCode(),
                pkg.getPackageType(),
                oldStatus.getDescription(),
                newStatus.getDescription(),
                details != null ? details : "");
        auditLogs.add(logEntry);

        for (ITrackingObserver observer : observers) {
            observer.onPackageStatusChanged(pkg, oldStatus, newStatus, details);
        }
    }

    /**
     * Paketin durumunu güvenli şekilde günceller ve tüm dinleyicilere bildirim fırlatır.
     */
    public void updatePackageStatus(Package pkg, DeliveryStatus newStatus, String details) {
        if (pkg == null || newStatus == null) return;
        DeliveryStatus oldStatus = pkg.getStatus();
        if (oldStatus != newStatus) {
            pkg.setStatus(newStatus);
            notifyObservers(pkg, oldStatus, newStatus, details);
        }
    }

    public List<String> getAuditLogs() {
        return Collections.unmodifiableList(auditLogs);
    }

    public int getObserverCount() {
        return observers.size();
    }
}
