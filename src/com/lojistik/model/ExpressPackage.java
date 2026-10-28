package com.lojistik.model;

/**
 * Acil teslim edilmesi gereken ekspres kargoları temsil eden somut sınıf.
 * Kalıtım (Inheritance) ve Çok Biçimlilik (Polymorphism) örneğidir.
 */
public class ExpressPackage extends Package {
    private int guaranteedDeliveryHours;

    public ExpressPackage(String id, String trackingCode, String senderName, String recipientName,
                          Location destination, double weightKg, double volumeM3, double baseCost,
                          int guaranteedDeliveryHours) {
        super(id, trackingCode, senderName, recipientName, destination, weightKg, volumeM3, baseCost);
        this.guaranteedDeliveryHours = guaranteedDeliveryHours > 0 ? guaranteedDeliveryHours : 24;
    }

    @Override
    public double calculateShippingFee() {
        // Ekspres tarife: Taban ücret + (kg * 14 TL + m3 * 35 TL) * 1.5 katsayısı
        double standardPart = (getWeightKg() * 14.0) + (getVolumeM3() * 35.0);
        return getBaseCost() + (standardPart * 1.5);
    }

    @Override
    public String getHandlingInstructions() {
        return "ACİL EKSPRES KARGO: İlk dağıtım aracına yüklenmeli (" + guaranteedDeliveryHours + " saat taahhütlü).";
    }

    @Override
    public int getPriorityLevel() {
        return 3; // En yüksek öncelik (Greedy algoritmada ilk sırada yer alır)
    }

    @Override
    public String getPackageType() {
        return "Ekspres Paket";
    }

    public int getGuaranteedDeliveryHours() {
        return guaranteedDeliveryHours;
    }

    public void setGuaranteedDeliveryHours(int guaranteedDeliveryHours) {
        this.guaranteedDeliveryHours = guaranteedDeliveryHours;
    }
}
