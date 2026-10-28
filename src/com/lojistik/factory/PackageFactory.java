package com.lojistik.factory;

import com.lojistik.model.*;
import com.lojistik.model.Package;

/**
 * Paket Üretim Fabrikası (Factory Pattern).
 * İstemcinin doğrudan somut sınıflara bağımlı olmasını önler.
 */
public class PackageFactory {

    public enum PackageType {
        STANDARD,
        EXPRESS,
        FRAGILE
    }

    public static Package createPackage(PackageType type, String id, String trackingCode,
                                        String sender, String recipient, Location destination,
                                        double weightKg, double volumeM3, double baseCost,
                                        Object... extraParams) {
        if (type == null) {
            throw new IllegalArgumentException("Paket tipi belirtilmelidir.");
        }

        switch (type) {
            case STANDARD:
                return new StandardPackage(id, trackingCode, sender, recipient, destination, weightKg, volumeM3, baseCost);

            case EXPRESS:
                int guaranteedHours = (extraParams.length > 0 && extraParams[0] instanceof Integer)
                        ? (Integer) extraParams[0] : 24;
                return new ExpressPackage(id, trackingCode, sender, recipient, destination, weightKg, volumeM3, baseCost, guaranteedHours);

            case FRAGILE:
                double insuranceFee = (extraParams.length > 0 && extraParams[0] instanceof Double)
                        ? (Double) extraParams[0] : 45.0;
                boolean bubbleWrap = (extraParams.length > 1 && extraParams[1] instanceof Boolean)
                        ? (Boolean) extraParams[1] : true;
                return new FragilePackage(id, trackingCode, sender, recipient, destination, weightKg, volumeM3, baseCost, insuranceFee, bubbleWrap);

            default:
                throw new IllegalArgumentException("Desteklenmeyen kargo türü: " + type);
        }
    }
}
