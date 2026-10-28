package com.lojistik.service;

import com.lojistik.algorithm.DijkstraRouteOptimizer;
import com.lojistik.algorithm.GreedyKnapsackDistributor;
import com.lojistik.algorithm.IPackageDistributor;
import com.lojistik.algorithm.IRouteOptimizer;
import com.lojistik.factory.PackageFactory;
import com.lojistik.factory.VehicleFactory;
import com.lojistik.model.*;
import com.lojistik.model.Package;
import com.lojistik.observer.ConsoleNotificationObserver;
import com.lojistik.observer.DeliveryTrackingManager;
import com.lojistik.repository.ILogisticsRepository;
import com.lojistik.repository.JsonLogisticsRepository;

import java.io.IOException;
import java.util.*;

/**
 * Lojistik Yönetim Servisi (Service & Facade Katmanı).
 * 
 * SOLID Prensipleri:
 * - SRP: Lojistik iş akışlarını koordine eder, alt işlemleri uzman sınıflara delege eder.
 * - DIP: Rota optimizasyonuna (IRouteOptimizer), paket dağıtımına (IPackageDistributor)
 *   ve veri depolamaya (ILogisticsRepository) arayüzler üzerinden bağlıdır.
 */
public class LogisticsService {
    private final List<Location> locations;
    private final List<Vehicle> vehicles;
    private final List<Package> packages;
    private final RouteGraph routeGraph;

    private IRouteOptimizer routeOptimizer;
    private IPackageDistributor packageDistributor;
    private final ILogisticsRepository repository;
    private final DeliveryTrackingManager trackingManager;

    public LogisticsService(ILogisticsRepository repository) {
        this.locations = new ArrayList<>();
        this.vehicles = new ArrayList<>();
        this.packages = new ArrayList<>();
        this.routeGraph = new RouteGraph();

        // Stratejiler varsayılan olarak Dijkstra ve Greedy Knapsack seçilir (Strategy Pattern)
        this.routeOptimizer = new DijkstraRouteOptimizer();
        this.packageDistributor = new GreedyKnapsackDistributor();

        this.repository = repository != null ? repository : new JsonLogisticsRepository("data/logistics_data.json");
        this.trackingManager = new DeliveryTrackingManager();

        // Canlı konsol gözlemcisini bağla (Observer Pattern)
        this.trackingManager.registerObserver(new ConsoleNotificationObserver());
    }

    /**
     * Sistemi başlatır. Varsa diskteki verileri yükler, yoksa örnek veri kümesi oluşturur.
     */
    public void initialize() {
        if (repository.exists()) {
            try {
                repository.loadData(locations, vehicles, packages, routeGraph);
                System.out.println("💾 [BİLGİ] Kayıtlı veriler dosyadan başarıyla yüklendi.");
                return;
            } catch (Exception e) {
                System.err.println("⚠️ Veri yüklenirken hata oluştu: " + e.getMessage() + ". Varsayılan veriler yükleniyor.");
            }
        }
        seedDefaultData();
        saveAllData();
    }

    /**
     * Varsayılan gerçekçi lokasyonlar, yol ağı, araç filosu ve kargoları oluşturur.
     */
    public void seedDefaultData() {
        locations.clear();
        vehicles.clear();
        packages.clear();
        routeGraph.clear();

        // 1. Lokasyonlar
        Location depMerkez = new Location("LOC_01", "Gebze Ana Lojistik Üssü", "Kocaeli", "Gebze");
        Location locKadikoy = new Location("LOC_02", "Kadıköy Dağıtım Merkezi", "İstanbul", "Kadıköy");
        Location locUmraniye = new Location("LOC_03", "Ümraniye Şubesi", "İstanbul", "Ümraniye");
        Location locBesiktas = new Location("LOC_04", "Beşiktaş Transfer Noktası", "İstanbul", "Beşiktaş");
        Location locSariyer = new Location("LOC_05", "Sarıyer Dağıtım Noktası", "İstanbul", "Sarıyer");
        Location locKartal = new Location("LOC_06", "Kartal Şubesi", "İstanbul", "Kartal");

        addLocation(depMerkez);
        addLocation(locKadikoy);
        addLocation(locUmraniye);
        addLocation(locBesiktas);
        addLocation(locSariyer);
        addLocation(locKartal);

        // 2. Yol Haritası ve Mesafeler (Ağırlıklı Graf)
        routeGraph.addRoad(depMerkez, locKartal, 22.0);
        routeGraph.addRoad(locKartal, locKadikoy, 18.0);
        routeGraph.addRoad(locKartal, locUmraniye, 20.0);
        routeGraph.addRoad(locKadikoy, locUmraniye, 11.0);
        routeGraph.addRoad(locKadikoy, locBesiktas, 14.5);
        routeGraph.addRoad(locUmraniye, locBesiktas, 16.0);
        routeGraph.addRoad(locBesiktas, locSariyer, 17.5);
        routeGraph.addRoad(locUmraniye, locSariyer, 23.0);

        // 3. Araç Filosu (Factory Method ile üretim)
        Vehicle v1 = VehicleFactory.createDefaultVehicle(VehicleFactory.VehicleType.TRUCK, "VEH_01", "34-LOG-101", depMerkez);
        Vehicle v2 = VehicleFactory.createDefaultVehicle(VehicleFactory.VehicleType.VAN, "VEH_02", "34-LOG-202", locKadikoy);
        Vehicle v3 = VehicleFactory.createDefaultVehicle(VehicleFactory.VehicleType.MOTORCYCLE, "VEH_03", "34-LOG-303", locBesiktas);
        vehicles.add(v1);
        vehicles.add(v2);
        vehicles.add(v3);

        // 4. Örnek Paketler (Factory Method ile üretim)
        Package p1 = PackageFactory.createPackage(PackageFactory.PackageType.EXPRESS, "PKG_01", "TRK-EXP101",
                "Teknoloji A.Ş.", "Ahmet Yılmaz", locBesiktas, 4.5, 0.03, 35.0, 12);

        Package p2 = PackageFactory.createPackage(PackageFactory.PackageType.FRAGILE, "PKG_02", "TRK-FRG202",
                "Kristal Züccaciye", "Ayşe Kaya", locSariyer, 12.0, 0.12, 45.0, 75.0, true);

        Package p3 = PackageFactory.createPackage(PackageFactory.PackageType.STANDARD, "PKG_03", "TRK-STD303",
                "Moda Tekstil", "Mehmet Demir", locUmraniye, 28.0, 0.25, 25.0);

        Package p4 = PackageFactory.createPackage(PackageFactory.PackageType.STANDARD, "PKG_04", "TRK-STD404",
                "Sanayi Yedek Parça", "Can Yıldız", locKartal, 450.0, 3.2, 50.0);

        Package p5 = PackageFactory.createPackage(PackageFactory.PackageType.EXPRESS, "PKG_05", "TRK-EXP505",
                "Eczane Deposu", "Fatma Çelik", locKadikoy, 2.0, 0.01, 30.0, 6);

        packages.add(p1);
        packages.add(p2);
        packages.add(p3);
        packages.add(p4);
        packages.add(p5);
    }

    public void saveAllData() {
        try {
            repository.saveData(locations, vehicles, packages, routeGraph);
        } catch (IOException e) {
            System.err.println("⚠️ Veriler kaydedilemedi: " + e.getMessage());
        }
    }

    // Lokasyon İşlemleri
    public void addLocation(Location loc) {
        if (loc != null && !locations.contains(loc)) {
            locations.add(loc);
            routeGraph.addLocation(loc);
        }
    }

    public Location findLocationById(String id) {
        for (Location loc : locations) {
            if (loc.getId().equalsIgnoreCase(id)) return loc;
        }
        return null;
    }

    // Yol Ekleme
    public void addRoad(Location from, Location to, double distanceKm) {
        routeGraph.addRoad(from, to, distanceKm);
    }

    // Araç İşlemleri
    public void addVehicle(Vehicle vehicle) {
        if (vehicle != null && !vehicles.contains(vehicle)) {
            vehicles.add(vehicle);
            saveAllData();
        }
    }

    public Vehicle findVehicleById(String id) {
        for (Vehicle v : vehicles) {
            if (v.getId().equalsIgnoreCase(id)) return v;
        }
        return null;
    }

    // Paket İşlemleri
    public void addPackage(Package pkg) {
        if (pkg != null && !packages.contains(pkg)) {
            packages.add(pkg);
            trackingManager.updatePackageStatus(pkg, DeliveryStatus.BEKLEMEDE, "Yeni kargo sisteme kaydedildi");
            saveAllData();
        }
    }

    public Package findPackageByIdOrTracking(String query) {
        for (Package p : packages) {
            if (p.getId().equalsIgnoreCase(query) || p.getTrackingCode().equalsIgnoreCase(query)) {
                return p;
            }
        }
        return null;
    }

    // Dağıtım Algoritması Çalıştırma (Greedy Knapsack)
    public Map<Vehicle, List<Package>> runDistributionOptimization() {
        List<Package> pending = new ArrayList<>();
        for (Package p : packages) {
            if (p.getStatus() == DeliveryStatus.BEKLEMEDE) {
                pending.add(p);
            }
        }

        Map<Vehicle, List<Package>> plan = packageDistributor.distributePackages(pending, vehicles);

        for (Map.Entry<Vehicle, List<Package>> entry : plan.entrySet()) {
            Vehicle v = entry.getKey();
            for (Package p : entry.getValue()) {
                trackingManager.updatePackageStatus(p, DeliveryStatus.ARACA_YUKLENDI,
                        v.getPlateNumber() + " plakalı " + v.getVehicleType() + " aracına yüklendi");
            }
        }

        saveAllData();
        return plan;
    }

    // Rota Hesaplama (Dijkstra)
    public RouteResult calculateRoute(Location start, Location destination, double speedKmH) {
        return routeOptimizer.findShortestRoute(routeGraph, start, destination, speedKmH);
    }

    // Araç için çoklu durak rotasını optimize et
    public RouteResult calculateMultiStopRouteForVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getLoadedPackages().isEmpty()) {
            return RouteResult.notFound();
        }

        Location start = vehicle.getCurrentLocation() != null ? vehicle.getCurrentLocation() : locations.get(0);
        List<Location> deliveryPoints = new ArrayList<>();
        for (Package p : vehicle.getLoadedPackages()) {
            if (!deliveryPoints.contains(p.getDestination())) {
                deliveryPoints.add(p.getDestination());
            }
        }

        return routeOptimizer.optimizeMultiStopRoute(routeGraph, start, deliveryPoints, vehicle.getSpeedFactor());
    }

    // Dağıtım Simülasyonu
    public void simulateDeliveryForVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getLoadedPackages().isEmpty()) {
            System.out.println("❌ Bu araçta yüklü paket bulunmamaktadır.");
            return;
        }

        System.out.println("\n🚀 Dağıtım Görevi Başlatılıyor: " + vehicle.getPlateNumber() + " (" + vehicle.getVehicleType() + ")");
        RouteResult route = calculateMultiStopRouteForVehicle(vehicle);

        System.out.println("📍 Optimize Edilmiş Güzergah: " + route);
        System.out.printf("⛽ Tahmini Operasyon Maliyeti: %.2f TL\n", vehicle.calculateOperationalCost(route.getTotalDistanceKm()));

        List<Package> toDeliver = new ArrayList<>(vehicle.getLoadedPackages());
        for (Package p : toDeliver) {
            trackingManager.updatePackageStatus(p, DeliveryStatus.DAGITIMDA, "Kurye yola çıktı");
        }

        // Teslimat tamamlama simülasyonu
        for (Package p : toDeliver) {
            trackingManager.updatePackageStatus(p, DeliveryStatus.TESLIM_EDILDI,
                    p.getRecipientName() + " kişisine teslim edildi (" + p.getDestination().getName() + ")");
            vehicle.unloadPackage(p);
        }

        // Aracın son lokasyonunu güncelle
        if (!route.getPath().isEmpty()) {
            vehicle.setCurrentLocation(route.getPath().get(route.getPath().size() - 1));
        }

        saveAllData();
        System.out.println("✅ Dağıtım başarıyla tamamlandı!\n");
    }

    // Dependency Injection / Strateji Değiştirme
    public void setRouteOptimizer(IRouteOptimizer routeOptimizer) {
        this.routeOptimizer = routeOptimizer;
    }

    public void setPackageDistributor(IPackageDistributor packageDistributor) {
        this.packageDistributor = packageDistributor;
    }

    public List<Location> getLocations() {
        return Collections.unmodifiableList(locations);
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public List<Package> getPackages() {
        return Collections.unmodifiableList(packages);
    }

    public RouteGraph getRouteGraph() {
        return routeGraph;
    }

    public DeliveryTrackingManager getTrackingManager() {
        return trackingManager;
    }
}
