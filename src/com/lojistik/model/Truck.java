package com.lojistik.model;

/**
 * Ağır yük ve yüksek hacimli kargo taşıyan Kamyon sınıfı.
 * Kalıtım (Inheritance) ve Çok Biçimlilik (Polymorphism) örneğidir.
 */
public class Truck extends Vehicle {

    public Truck(String id, String plateNumber, String model,
                 double maxWeightCapacityKg, double maxVolumeCapacityM3, Location currentLocation) {
        super(id, plateNumber, model, maxWeightCapacityKg, maxVolumeCapacityM3, currentLocation);
    }

    @Override
    public double calculateOperationalCost(double distanceKm) {
        // Kamyon operasyon maliyeti: Sabit başlangıç 120 TL + km başı 18.5 TL
        return 120.0 + (distanceKm * 18.5);
    }

    @Override
    public double getSpeedFactor() {
        return 60.0; // Ağır vasıta ortalama hızı 60 km/s
    }

    @Override
    public String getVehicleType() {
        return "Ağır Yük Kamyonu";
    }
}
