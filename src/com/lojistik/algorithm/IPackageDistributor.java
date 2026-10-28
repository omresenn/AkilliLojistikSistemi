package com.lojistik.algorithm;

import com.lojistik.model.Package;
import com.lojistik.model.Vehicle;

import java.util.List;
import java.util.Map;

/**
 * Paket dağıtım algoritmaları için Strateji Arayüzü (Strategy Pattern).
 * OCP (Open-Closed Principle): Farklı dağıtım algoritmaları (Greedy Knapsack,
 * First-Fit, Genetic Algorithm vb.) bu arayüz üzerinden sisteme bağlanabilir.
 */
public interface IPackageDistributor {
    /**
     * Beklemedeki paketleri araç filosu kapasitesine ve öncelik durumuna göre paylaştırır.
     * 
     * @param pendingPackages Dağıtılmayı bekleyen paketler
     * @param availableVehicles Müsait dağıtım araçları
     * @return Dağıtım sonuç raporu (Araç -> Atanan Paketler listesi)
     */
    Map<Vehicle, List<Package>> distributePackages(List<Package> pendingPackages, List<Vehicle> availableVehicles);
}
