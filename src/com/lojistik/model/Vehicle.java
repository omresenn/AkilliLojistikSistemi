package com.lojistik.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Soyut Dağıtım Aracı Temel Sınıfı.
 * 
 * OOP Prensipleri:
 * - Soyutlama (Abstraction): calculateOperationalCost() ve getSpeedFactor()
 * - Kalıtım (Inheritance): Truck, Van, Motorcycle sınıflarının atasıdır.
 * - Kapsülleme (Encapsulation): Kapasite kontrolleri ve yükleme işlemleri kontrollü metotlarla yapılır.
 * - Çok Biçimlilik (Polymorphism): Farklı araç tipleri kendi maliyet ve hız hesaplamalarını yapar.
 */
public abstract class Vehicle {
    private String id;
    private String plateNumber;
    private String model;
    private double maxWeightCapacityKg;
    private double maxVolumeCapacityM3;
    private double currentLoadedWeight;
    private double currentLoadedVolume;
    private final List<Package> loadedPackages;
    private Location currentLocation;
    private boolean active;

    public Vehicle(String id, String plateNumber, String model,
                   double maxWeightCapacityKg, double maxVolumeCapacityM3, Location currentLocation) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Araç ID boş olamaz.");
        }
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Plaka boş olamaz.");
        }
        if (maxWeightCapacityKg <= 0) {
            throw new IllegalArgumentException("Ağırlık kapasitesi pozitif olmalıdır.");
        }
        if (maxVolumeCapacityM3 <= 0) {
            throw new IllegalArgumentException("Hacim kapasitesi pozitif olmalıdır.");
        }

        this.id = id.trim();
        this.plateNumber = plateNumber.trim().toUpperCase();
        this.model = model != null ? model.trim() : "Standart Araç";
        this.maxWeightCapacityKg = maxWeightCapacityKg;
        this.maxVolumeCapacityM3 = maxVolumeCapacityM3;
        this.currentLoadedWeight = 0.0;
        this.currentLoadedVolume = 0.0;
        this.loadedPackages = new ArrayList<>();
        this.currentLocation = currentLocation;
        this.active = true;
    }

    /**
     * Çok Biçimlilik (Polymorphism):
     * Kat edilen mesafeye göre aracın yakıt/aşınma operasyonel maliyetini hesaplar.
     */
    public abstract double calculateOperationalCost(double distanceKm);

    /**
     * Aracın ortalama hız faktörü (km/saat cinsinden baz hız).
     */
    public abstract double getSpeedFactor();

    public abstract String getVehicleType();

    /**
     * Aracın verilen paketi taşıyacak boş ağırlık ve hacim kapasitesi olup olmadığını denetler.
     */
    public boolean canLoad(Package pkg) {
        if (pkg == null) return false;
        boolean weightOk = (this.currentLoadedWeight + pkg.getWeightKg()) <= this.maxWeightCapacityKg;
        boolean volumeOk = (this.currentLoadedVolume + pkg.getVolumeM3()) <= this.maxVolumeCapacityM3;
        return weightOk && volumeOk;
    }

    /**
     * Araca paket yükler (Kapasite kısıtı kontrol edilir).
     */
    public boolean loadPackage(Package pkg) {
        if (!canLoad(pkg)) {
            return false;
        }
        loadedPackages.add(pkg);
        currentLoadedWeight += pkg.getWeightKg();
        currentLoadedVolume += pkg.getVolumeM3();
        pkg.setStatus(DeliveryStatus.ARACA_YUKLENDI);
        return true;
    }

    /**
     * Paketi araçtan indirir / teslim eder.
     */
    public boolean unloadPackage(Package pkg) {
        if (loadedPackages.remove(pkg)) {
            currentLoadedWeight = Math.max(0.0, currentLoadedWeight - pkg.getWeightKg());
            currentLoadedVolume = Math.max(0.0, currentLoadedVolume - pkg.getVolumeM3());
            return true;
        }
        return false;
    }

    /**
     * Araçtaki tüm yükü boşaltır.
     */
    public void clearPackages() {
        loadedPackages.clear();
        currentLoadedWeight = 0.0;
        currentLoadedVolume = 0.0;
    }

    /**
     * Ağırlık doluluk oranını yüzde olarak döndürür (0 - 100).
     */
    public double getWeightOccupancyRate() {
        return (currentLoadedWeight / maxWeightCapacityKg) * 100.0;
    }

    /**
     * Hacim doluluk oranını yüzde olarak döndürür (0 - 100).
     */
    public double getVolumeOccupancyRate() {
        return (currentLoadedVolume / maxVolumeCapacityM3) * 100.0;
    }

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

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Plaka boş olamaz.");
        }
        this.plateNumber = plateNumber.trim().toUpperCase();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getMaxWeightCapacityKg() {
        return maxWeightCapacityKg;
    }

    public void setMaxWeightCapacityKg(double maxWeightCapacityKg) {
        if (maxWeightCapacityKg <= 0) {
            throw new IllegalArgumentException("Kapasite sıfırdan büyük olmalıdır.");
        }
        this.maxWeightCapacityKg = maxWeightCapacityKg;
    }

    public double getMaxVolumeCapacityM3() {
        return maxVolumeCapacityM3;
    }

    public void setMaxVolumeCapacityM3(double maxVolumeCapacityM3) {
        if (maxVolumeCapacityM3 <= 0) {
            throw new IllegalArgumentException("Hacim kapasitesi sıfırdan büyük olmalıdır.");
        }
        this.maxVolumeCapacityM3 = maxVolumeCapacityM3;
    }

    public double getCurrentLoadedWeight() {
        return currentLoadedWeight;
    }

    public double getCurrentLoadedVolume() {
        return currentLoadedVolume;
    }

    public List<Package> getLoadedPackages() {
        return Collections.unmodifiableList(loadedPackages);
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] Plaka: %s | Model: %s | Yük: %.1f/%.1f kg (%%%.1f) | Hacim: %.2f/%.2f m³ | Paket: %d adet",
                getVehicleType(), plateNumber, model, currentLoadedWeight, maxWeightCapacityKg, getWeightOccupancyRate(),
                currentLoadedVolume, maxVolumeCapacityM3, loadedPackages.size());
    }
}
