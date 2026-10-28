package com.lojistik.model;

/**
 * Hızlı ve acil küçük paket kurye motosikleti.
 * Kalıtım (Inheritance) ve Çok Biçimlilik (Polymorphism) örneğidir.
 */
public class Motorcycle extends Vehicle {

    public Motorcycle(String id, String plateNumber, String model,
                      double maxWeightCapacityKg, double maxVolumeCapacityM3, Location currentLocation) {
        super(id, plateNumber, model, maxWeightCapacityKg, maxVolumeCapacityM3, currentLocation);
    }

    @Override
    public double calculateOperationalCost(double distanceKm) {
        // Motosiklet operasyon maliyeti: Sabit başlangıç 20 TL + km başı 3.5 TL
        return 20.0 + (distanceKm * 3.5);
    }

    @Override
    public double getSpeedFactor() {
        return 85.0; // Trafikte hızlı kurye ortalama hız 85 km/s
    }

    @Override
    public String getVehicleType() {
        return "Hızlı Kurye Motosikleti";
    }
}
