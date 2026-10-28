package com.lojistik.model;

/**
 * Standart teslimat kargolarını temsil eden somut sınıf.
 * Kalıtım (Inheritance) ve Çok Biçimlilik (Polymorphism) örneğidir.
 */
public class StandardPackage extends Package {

    public StandardPackage(String id, String trackingCode, String senderName, String recipientName,
                           Location destination, double weightKg, double volumeM3, double baseCost) {
        super(id, trackingCode, senderName, recipientName, destination, weightKg, volumeM3, baseCost);
    }

    @Override
    public double calculateShippingFee() {
        // Standart tarife: Taban ücret + kg başı 8.5 TL + m3 başı 25 TL
        return getBaseCost() + (getWeightKg() * 8.5) + (getVolumeM3() * 25.0);
    }

    @Override
    public String getHandlingInstructions() {
        return "Standart paket elleçleme prosedürü uygulanır.";
    }

    @Override
    public int getPriorityLevel() {
        return 1; // Standart öncelik
    }

    @Override
    public String getPackageType() {
        return "Standart Paket";
    }
}
