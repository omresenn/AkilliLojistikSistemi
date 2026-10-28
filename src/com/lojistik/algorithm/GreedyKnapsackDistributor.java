package com.lojistik.algorithm;

import com.lojistik.model.Motorcycle;
import com.lojistik.model.Package;
import com.lojistik.model.Truck;
import com.lojistik.model.Vehicle;

import java.util.*;

/**
 * Açgözlü Sırt Çantası (Greedy Knapsack) Tabanlı Paket Dağıtım Algoritması.
 * 
 * Algoritma Mantığı:
 * 1. Önceliklendirme: Paketler öncelik seviyesi (Ekspres: 3, Kırılabilir: 2, Standart: 1)
 *    ve birim ağırlık/hacim başına getiri oranına göre büyükten küçüğe sıralanır.
 * 2. Akıllı Eşleştirme (Vehicle Suitability):
 *    - Küçük ve acil paketler öncelikle Motosiklet ve Panelvanlara,
 *    - Ağır ve büyük hacimli paketler Kamyonlara yönlendirilir.
 * 3. Kapasite Kontrolü: Araçların azami ağırlık (kg) ve azami hacim (m³) sınırları
 *    aşılmadan paketler sırayla araçlara yüklenir.
 * 4. Kapasitesi dolan araçlar listeden çıkarılır; sığmayan paketler depoda beklemeye alınır.
 */
public class GreedyKnapsackDistributor implements IPackageDistributor {

    @Override
    public Map<Vehicle, List<Package>> distributePackages(List<Package> pendingPackages, List<Vehicle> availableVehicles) {
        Map<Vehicle, List<Package>> distributionPlan = new LinkedHashMap<>();

        if (pendingPackages == null || pendingPackages.isEmpty() || availableVehicles == null || availableVehicles.isEmpty()) {
            return distributionPlan;
        }

        for (Vehicle v : availableVehicles) {
            distributionPlan.put(v, new ArrayList<>());
        }

        // Paketleri öncelik puanına ve taşıma bedeli / hacim yoğunluğuna göre sırala
        List<Package> sortedPackages = new ArrayList<>(pendingPackages);
        sortedPackages.sort((p1, p2) -> {
            // İlk kriter: Öncelik seviyesi (3 > 2 > 1)
            int priorityCompare = Integer.compare(p2.getPriorityLevel(), p1.getPriorityLevel());
            if (priorityCompare != 0) return priorityCompare;

            // İkinci kriter: Yoğunluk oranı (ücret / (ağırlık + hacim katsayısı))
            double ratio1 = p1.calculateShippingFee() / (p1.getWeightKg() + p1.getVolumeM3() * 20.0);
            double ratio2 = p2.calculateShippingFee() / (p2.getWeightKg() + p2.getVolumeM3() * 20.0);
            return Double.compare(ratio2, ratio1);
        });

        // Her paket için en uygun aracı bul ve yükle
        for (Package pkg : sortedPackages) {
            Vehicle bestVehicle = findBestFittingVehicle(pkg, availableVehicles);
            if (bestVehicle != null) {
                boolean loaded = bestVehicle.loadPackage(pkg);
                if (loaded) {
                    distributionPlan.get(bestVehicle).add(pkg);
                }
            }
        }

        return distributionPlan;
    }

    /**
     * Verilen pakete hem kapasite hem de araç türü açısından en uygun aracı seçer.
     */
    private Vehicle findBestFittingVehicle(Package pkg, List<Vehicle> vehicles) {
        Vehicle candidate = null;
        double minRemainingCapacityAfterLoad = Double.MAX_VALUE;

        for (Vehicle vehicle : vehicles) {
            if (!vehicle.isActive()) continue;
            if (!vehicle.canLoad(pkg)) continue;

            // Tip uygunluğu bonusu
            boolean typeFavored = false;
            if (pkg.getPriorityLevel() == 3 && pkg.getWeightKg() <= 35.0 && vehicle instanceof Motorcycle) {
                typeFavored = true;
            } else if (pkg.getWeightKg() > 250.0 && vehicle instanceof Truck) {
                typeFavored = true;
            }

            // Kalan kapasiteyi en verimli kullanacak aracı seç (Best-Fit heuristiği)
            double remainingWeightCapacity = (vehicle.getMaxWeightCapacityKg() - vehicle.getCurrentLoadedWeight()) - pkg.getWeightKg();

            if (typeFavored || remainingWeightCapacity < minRemainingCapacityAfterLoad) {
                minRemainingCapacityAfterLoad = remainingWeightCapacity;
                candidate = vehicle;
                if (typeFavored) {
                    break; // Tip doğrudan eşleştiyse öncelikli olarak seç
                }
            }
        }

        return candidate;
    }
}
