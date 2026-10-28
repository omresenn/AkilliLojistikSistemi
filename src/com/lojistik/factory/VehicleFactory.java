package com.lojistik.factory;

import com.lojistik.model.*;

/**
 * Araç Fabrika Sınıfı (Factory Method Pattern).
 * 
 * Amaç: Nesne üretim karmaşıklığını ve mantığını istemci (client) koddan soyutlamak,
 * yeni bir araç türü eklendiğinde tek bir noktadan yönetilmesini sağlamak (OCP & SRP).
 */
public class VehicleFactory {

    public enum VehicleType {
        TRUCK,
        VAN,
        MOTORCYCLE
    }

    /**
     * Verilen parametrelere göre uygun araç nesnesini üretir.
     */
    public static Vehicle createVehicle(VehicleType type, String id, String plateNumber,
                                        String model, double maxWeightCapacityKg,
                                        double maxVolumeCapacityM3, Location startLocation) {
        if (type == null) {
            throw new IllegalArgumentException("Araç tipi belirtilmelidir.");
        }

        switch (type) {
            case TRUCK:
                return new Truck(id, plateNumber, model, maxWeightCapacityKg, maxVolumeCapacityM3, startLocation);
            case VAN:
                return new Van(id, plateNumber, model, maxWeightCapacityKg, maxVolumeCapacityM3, startLocation);
            case MOTORCYCLE:
                return new Motorcycle(id, plateNumber, model, maxWeightCapacityKg, maxVolumeCapacityM3, startLocation);
            default:
                throw new IllegalArgumentException("Desteklenmeyen araç tipi: " + type);
        }
    }

    /**
     * Standart varsayılan kapasitelerle hızlı araç oluşturur.
     */
    public static Vehicle createDefaultVehicle(VehicleType type, String id, String plateNumber, Location startLocation) {
        switch (type) {
            case TRUCK:
                return new Truck(id, plateNumber, "Mercedes Actros", 3500.0, 25.0, startLocation);
            case VAN:
                return new Van(id, plateNumber, "Ford Transit", 1200.0, 9.0, startLocation);
            case MOTORCYCLE:
                return new Motorcycle(id, plateNumber, "Honda PCX Kurye", 40.0, 0.35, startLocation);
            default:
                throw new IllegalArgumentException("Desteklenmeyen araç tipi: " + type);
        }
    }
}
