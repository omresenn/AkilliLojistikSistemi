package com.lojistik.model;

/**
 * Hassas, kırılabilir ve özel sigorta gerektiren kargoları temsil eden somut sınıf.
 * Kalıtım (Inheritance) ve Çok Biçimlilik (Polymorphism) örneğidir.
 */
public class FragilePackage extends Package {
    private double insuranceFee;
    private boolean bubbleWrapped;

    public FragilePackage(String id, String trackingCode, String senderName, String recipientName,
                          Location destination, double weightKg, double volumeM3, double baseCost,
                          double insuranceFee, boolean bubbleWrapped) {
        super(id, trackingCode, senderName, recipientName, destination, weightKg, volumeM3, baseCost);
        this.insuranceFee = Math.max(0.0, insuranceFee);
        this.bubbleWrapped = bubbleWrapped;
    }

    @Override
    public double calculateShippingFee() {
        // Kırılabilir ürün tarifesi: Taban ücret + (kg * 11 TL) + (m3 * 30 TL) + sigorta bedeli
        return getBaseCost() + (getWeightKg() * 11.0) + (getVolumeM3() * 30.0) + insuranceFee;
    }

    @Override
    public String getHandlingInstructions() {
        return "DİKKAT KIRILABİLİR: " + (bubbleWrapped ? "Balonlu naylon ile korumalı. " : "Acilen sarılmalı. ")
                + "En üste yerleştirilmeli, düşürülmemelidir.";
    }

    @Override
    public int getPriorityLevel() {
        return 2; // Orta-yüksek öncelik
    }

    @Override
    public String getPackageType() {
        return "Kırılabilir Paket";
    }

    public double getInsuranceFee() {
        return insuranceFee;
    }

    public void setInsuranceFee(double insuranceFee) {
        this.insuranceFee = Math.max(0.0, insuranceFee);
    }

    public boolean isBubbleWrapped() {
        return bubbleWrapped;
    }

    public void setBubbleWrapped(boolean bubbleWrapped) {
        this.bubbleWrapped = bubbleWrapped;
    }
}
