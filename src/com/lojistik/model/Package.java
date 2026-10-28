package com.lojistik.model;

import java.util.Objects;

/**
 * Soyut Paket (Kargo) Temel Sınıfı.
 * 
 * OOP Prensipleri:
 * - Soyutlama (Abstraction): calculateShippingFee() ve getHandlingInstructions() gibi metotlar soyuttur.
 * - Kapsülleme (Encapsulation): Tüm alanlar private, geçerlilik kuralları (validation) mevcuttur.
 */
public abstract class Package {
    private String id;
    private String trackingCode;
    private String senderName;
    private String recipientName;
    private Location destination;
    private double weightKg;
    private double volumeM3;
    private DeliveryStatus status;
    private double baseCost;

    public Package(String id, String trackingCode, String senderName, String recipientName,
                   Location destination, double weightKg, double volumeM3, double baseCost) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Paket ID boş olamaz.");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Teslimat lokasyonu belirtilmelidir.");
        }
        if (weightKg <= 0) {
            throw new IllegalArgumentException("Paket ağırlığı sıfırdan büyük olmalıdır.");
        }
        if (volumeM3 <= 0) {
            throw new IllegalArgumentException("Paket hacmi sıfırdan büyük olmalıdır.");
        }
        if (baseCost < 0) {
            throw new IllegalArgumentException("Taban maliyet negatif olamaz.");
        }

        this.id = id.trim();
        this.trackingCode = (trackingCode != null && !trackingCode.trim().isEmpty()) 
                ? trackingCode.trim() 
                : "TRK-" + id.trim().toUpperCase();
        this.senderName = senderName != null ? senderName.trim() : "Bilinmeyen Gönderici";
        this.recipientName = recipientName != null ? recipientName.trim() : "Bilinmeyen Alıcı";
        this.destination = destination;
        this.weightKg = weightKg;
        this.volumeM3 = volumeM3;
        this.baseCost = baseCost;
        this.status = DeliveryStatus.BEKLEMEDE;
    }

    /**
     * Çok Biçimlilik (Polymorphism):
     * Her paket türü kendi gönderim maliyetini kendi özel formülüne göre hesaplar.
     */
    public abstract double calculateShippingFee();

    /**
     * Çok Biçimlilik (Polymorphism):
     * Paket türüne özel lojistik ve taşıma talimatlarını döndürür.
     */
    public abstract String getHandlingInstructions();

    /**
     * Dağıtım algoritmasında öncelik sırasını belirleyen seviye.
     * Sayı ne kadar büyükse dağıtımda o kadar önceliklidir (Greedy önceliklendirme).
     */
    public abstract int getPriorityLevel();

    public abstract String getPackageType();

    // Getter ve Setter metotları (Kapsülleme)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID boş olamaz.");
        }
        this.id = id.trim();
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        if (destination == null) {
            throw new IllegalArgumentException("Hedef lokasyon boş olamaz.");
        }
        this.destination = destination;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        if (weightKg <= 0) {
            throw new IllegalArgumentException("Ağırlık pozitif bir değer olmalıdır.");
        }
        this.weightKg = weightKg;
    }

    public double getVolumeM3() {
        return volumeM3;
    }

    public void setVolumeM3(double volumeM3) {
        if (volumeM3 <= 0) {
            throw new IllegalArgumentException("Hacim pozitif bir değer olmalıdır.");
        }
        this.volumeM3 = volumeM3;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Durum boş olamaz.");
        }
        this.status = status;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(double baseCost) {
        if (baseCost < 0) {
            throw new IllegalArgumentException("Taban ücret negatif olamaz.");
        }
        this.baseCost = baseCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Package aPackage = (Package) o;
        return Objects.equals(id, aPackage.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] #%s -> %s (%s, %.1f kg, Ücret: %.2f TL, Durum: %s)",
                getPackageType(), trackingCode, destination.getName(), recipientName, weightKg, calculateShippingFee(), status.getDescription());
    }
}
