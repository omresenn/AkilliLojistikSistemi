package com.lojistik.model;

/**
 * Orta ölçekli dağıtım yapan Panelvan aracı.
 * Kalıtım (Inheritance) ve Çok Biçimlilik (Polymorphism) örneğidir.
 */
public class Van extends Vehicle {

    public Van(String id, String plateNumber, String model,
               double maxWeightCapacityKg, double maxVolumeCapacityM3, Location currentLocation) {
        super(id, plateNumber, model, maxWeightCapacityKg, maxVolumeCapacityM3, currentLocation);
    }

    @Override
    public double calculateOperationalCost(double distanceKm) {
        // Panelvan operasyon maliyeti: Sabit başlangıç 50 TL + km başı 9.0 TL
        return 50.0 + (distanceKm * 9.0);
    }

    @Override
    public double getSpeedFactor() {
        return 75.0; // Şehir içi/arası ortalama hız 75 km/s
    }

    @Override
    public String getVehicleType() {
        return "Dağıtım Panelvanı";
    }
}
