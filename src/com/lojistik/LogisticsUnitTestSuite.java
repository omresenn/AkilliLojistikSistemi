package com.lojistik;

import com.lojistik.algorithm.DijkstraRouteOptimizer;
import com.lojistik.algorithm.GreedyKnapsackDistributor;
import com.lojistik.factory.PackageFactory;
import com.lojistik.factory.VehicleFactory;
import com.lojistik.model.*;
import com.lojistik.model.Package;
import com.lojistik.observer.DeliveryTrackingManager;
import com.lojistik.observer.ITrackingObserver;
import com.lojistik.repository.JsonLogisticsRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Akıllı Lojistik Sistemi - Kapsamlı Birim Test Paketi (Unit Test Suite).
 * 
 * Şart: En az 8 birim testi bulunmalıdır.
 * Bu sınıfta 10 adet detaylı ve bağımsız birim testi bulunmaktadır.
 * Harici kütüphane gerektirmeden bağımsız olarak (test.bat) veya IDE içerisinden çalışabilir.
 */
public class LogisticsUnitTestSuite {

    private static int passedCount = 0;
    private static int failedCount = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println(" 🧪 AKILLI LOJİSTİK SİSTEMİ - BİRİM TESTLERİ (UNIT TESTS) ");
        System.out.println("===============================================================");

        runTest("Test 1: Araç Taşıma Kapasitesi ve Yük Kısıtı Testi", LogisticsUnitTestSuite::testVehicleCapacityConstraint);
        runTest("Test 2: Araç Fabrika Deseni (Factory Pattern) Üretim Testi", LogisticsUnitTestSuite::testVehicleFactoryCreation);
        runTest("Test 3: Paket Polimorfizmi ve Ücret Hesaplama Testi", LogisticsUnitTestSuite::testPackageShippingFeePolymorphism);
        runTest("Test 4: Dijkstra En Kısa Rota Algoritması Testi", LogisticsUnitTestSuite::testDijkstraShortestPath);
        runTest("Test 5: Bağlantısız Graf ve Rota Bulunamama Durumu Testi", LogisticsUnitTestSuite::testDijkstraDisconnectedGraph);
        runTest("Test 6: Greedy Knapsack Paket Dağıtım Algoritması Testi", LogisticsUnitTestSuite::testGreedyDistributionAlgorithm);
        runTest("Test 7: Observer Tasarım Kalıbı ve Canlı Bildirim Testi", LogisticsUnitTestSuite::testObserverNotification);
        runTest("Test 8: JSON Veri Saklama ve Geri Yükleme (Persistence) Testi", LogisticsUnitTestSuite::testDataPersistenceSaveAndLoad);
        runTest("Test 9: Kapsülleme ve Veri Doğrulama (Validation) Testi", LogisticsUnitTestSuite::testEncapsulationAndValidation);
        runTest("Test 10: Araç Operasyonel Maliyet ve Hız Polimorfizmi Testi", LogisticsUnitTestSuite::testVehicleOperationalCostPolymorphism);

        System.out.println("===============================================================");
        System.out.printf("📊 TEST SONUÇLARI: Toplam: %d | Başarılı: %d ✅ | Başarısız: %d ❌\n",
                (passedCount + failedCount), passedCount, failedCount);
        System.out.println("===============================================================");

        if (failedCount > 0) {
            System.exit(1);
        }
    }

    private static void runTest(String testName, Runnable testMethod) {
        try {
            testMethod.run();
            System.out.printf("  ✅ [GEÇTİ] %s\n", testName);
            passedCount++;
        } catch (Throwable t) {
            System.out.printf("  ❌ [BAŞARISIZ] %s: %s\n", testName, t.getMessage());
            failedCount++;
        }
    }

    // Basit Assert Yardımcıları
    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError("AssertTrue başarısız: " + message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("AssertEquals başarısız: " + message + " (Beklenen: " + expected + ", Gelen: " + actual + ")");
        }
    }

    private static void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("AssertEquals başarısız: " + message + " (Beklenen: " + expected + ", Gelen: " + actual + ")");
        }
    }

    // ==========================================
    // 10 ADET BİRİM TEST METODU
    // ==========================================

    /**
     * Test 1: Araç kapasite kontrolü. Araç kapasitesini aşan paket kabul edilmemeli.
     */
    public static void testVehicleCapacityConstraint() {
        Location loc = new Location("L1", "Depo", "İstanbul", "Merkez");
        Vehicle van = new Van("V1", "34-ABC-01", "Transit", 100.0, 1.0, loc);

        Package smallPkg = new StandardPackage("P1", "TRK-01", "A", "B", loc, 60.0, 0.4, 20.0);
        Package heavyPkg = new StandardPackage("P2", "TRK-02", "A", "B", loc, 50.0, 0.4, 20.0);

        assertTrue(van.canLoad(smallPkg), "İlk paket 60 kg kapasiteye sığmalı.");
        boolean loaded1 = van.loadPackage(smallPkg);
        assertTrue(loaded1, "İlk paket yüklenmeli.");
        assertEquals(60.0, van.getCurrentLoadedWeight(), 0.001, "Yüklü ağırlık 60 olmalı.");

        // İkinci paket 50 kg -> 60 + 50 = 110 > 100 (kapasite aşımı)
        assertTrue(!van.canLoad(heavyPkg), "İkinci paket 100 kg sınırını aştığı için yüklenememeli.");
        boolean loaded2 = van.loadPackage(heavyPkg);
        assertTrue(!loaded2, "Kapasiteyi aşan paket yüklenmemelidir.");
    }

    /**
     * Test 2: Factory method ile doğru araç tiplerinin üretimi.
     */
    public static void testVehicleFactoryCreation() {
        Location loc = new Location("L1", "Merkez", "Ankara", "Çankaya");
        Vehicle truck = VehicleFactory.createDefaultVehicle(VehicleFactory.VehicleType.TRUCK, "T1", "06-TRK-01", loc);
        Vehicle moto = VehicleFactory.createDefaultVehicle(VehicleFactory.VehicleType.MOTORCYCLE, "M1", "06-MTO-01", loc);

        assertTrue(truck instanceof Truck, "Üretilen araç Truck sınıfından olmalı.");
        assertTrue(moto instanceof Motorcycle, "Üretilen araç Motorcycle sınıfından olmalı.");
        assertTrue(truck.getMaxWeightCapacityKg() > moto.getMaxWeightCapacityKg(), "Kamyon kapasitesi motordan büyük olmalı.");
    }

    /**
     * Test 3: Standart, Ekspres ve Kırılabilir paketlerin polimorfik ücret hesaplaması.
     */
    public static void testPackageShippingFeePolymorphism() {
        Location dest = new Location("L2", "Hedef", "İzmir", "Konak");
        Package standard = new StandardPackage("P1", "TRK-S1", "X", "Y", dest, 10.0, 0.1, 20.0);
        Package express = new ExpressPackage("P2", "TRK-E1", "X", "Y", dest, 10.0, 0.1, 20.0, 12);
        Package fragile = new FragilePackage("P3", "TRK-F1", "X", "Y", dest, 10.0, 0.1, 20.0, 50.0, true);

        // Hesaplama doğrulaması
        double standardFee = standard.calculateShippingFee();
        double expressFee = express.calculateShippingFee();
        double fragileFee = fragile.calculateShippingFee();

        assertTrue(expressFee > standardFee, "Ekspres kargo standart kargodan daha pahalı olmalı.");
        assertTrue(fragileFee > standardFee, "Sigortalı kırılabilir kargo standart kargodan daha pahalı olmalı.");
        assertEquals("Ekspres Paket", express.getPackageType(), "Tip adı doğru dönmeli.");
    }

    /**
     * Test 4: Dijkstra algoritması ile en kısa yolun doğru hesaplanması.
     */
    public static void testDijkstraShortestPath() {
        RouteGraph graph = new RouteGraph();
        Location a = new Location("A", "Nokta A", "İst", "A");
        Location b = new Location("B", "Nokta B", "İst", "B");
        Location c = new Location("C", "Nokta C", "İst", "C");
        Location d = new Location("D", "Nokta D", "İst", "D");

        // A - B (10), B - D (15) => Toplam 25
        // A - C (5), C - D (8)   => Toplam 13 (En kısa yol A -> C -> D olmalı)
        graph.addRoad(a, b, 10.0);
        graph.addRoad(b, d, 15.0);
        graph.addRoad(a, c, 5.0);
        graph.addRoad(c, d, 8.0);

        DijkstraRouteOptimizer optimizer = new DijkstraRouteOptimizer();
        RouteResult result = optimizer.findShortestRoute(graph, a, d, 60.0);

        assertTrue(result.isFound(), "Rota bulunmalıdır.");
        assertEquals(13.0, result.getTotalDistanceKm(), 0.001, "En kısa mesafe 13 km olmalıdır.");
        assertEquals(3, result.getPath().size(), "Rota 3 düğümden oluşmalıdır (A -> C -> D).");
        assertEquals("A", result.getPath().get(0).getId(), "Başlangıç A olmalıdır.");
        assertEquals("C", result.getPath().get(1).getId(), "Ara durak C olmalıdır.");
        assertEquals("D", result.getPath().get(2).getId(), "Bitiş D olmalıdır.");
    }

    /**
     * Test 5: Bağlantısı olmayan iki lokasyon için rota bulunamama doğrulaması.
     */
    public static void testDijkstraDisconnectedGraph() {
        RouteGraph graph = new RouteGraph();
        Location a = new Location("A", "Ada Şehri", "İst", "A");
        Location b = new Location("B", "Anakara", "İst", "B");
        graph.addLocation(a);
        graph.addLocation(b);
        // Aralarında yol eklenmedi!

        DijkstraRouteOptimizer optimizer = new DijkstraRouteOptimizer();
        RouteResult result = optimizer.findShortestRoute(graph, a, b, 60.0);

        assertTrue(!result.isFound(), "Bağlantısız iki nokta için rota bulunamamalıdır.");
        assertEquals(0, result.getPath().size(), "Yol boş dönmelidir.");
    }

    /**
     * Test 6: Greedy Knapsack paket dağıtımı ve öncelik sıralaması.
     */
    public static void testGreedyDistributionAlgorithm() {
        Location loc = new Location("L", "Depo", "İst", "Merkez");
        Vehicle moto = new Motorcycle("M1", "34-MTO", "Motor", 30.0, 0.2, loc);
        Vehicle truck = new Truck("T1", "34-TRK", "Kamyon", 2000.0, 15.0, loc);

        List<Vehicle> vehicles = Arrays.asList(moto, truck);

        Package urgentSmall = new ExpressPackage("P1", "TRK-EXP", "X", "Y", loc, 5.0, 0.02, 30.0, 6);
        Package heavyStandard = new StandardPackage("P2", "TRK-STD", "X", "Y", loc, 500.0, 4.0, 40.0);

        List<Package> packages = Arrays.asList(heavyStandard, urgentSmall);

        GreedyKnapsackDistributor distributor = new GreedyKnapsackDistributor();
        Map<Vehicle, List<Package>> plan = distributor.distributePackages(packages, vehicles);

        assertTrue(plan.get(moto).contains(urgentSmall), "Acil küçük paket motosiklete atanmalıdır.");
        assertTrue(plan.get(truck).contains(heavyStandard), "Ağır paket kamyona atanmalıdır.");
    }

    /**
     * Test 7: Observer deseninde durum değişikliği bildirimi.
     */
    public static void testObserverNotification() {
        DeliveryTrackingManager manager = new DeliveryTrackingManager();
        Location loc = new Location("L", "Depo", "İst", "Merkez");
        Package pkg = new StandardPackage("P1", "TRK-01", "X", "Y", loc, 10.0, 0.1, 20.0);

        final boolean[] notified = {false};
        final DeliveryStatus[] capturedStatus = {null};

        ITrackingObserver testObserver = (p, oldStatus, newStatus, details) -> {
            notified[0] = true;
            capturedStatus[0] = newStatus;
        };

        manager.registerObserver(testObserver);
        manager.updatePackageStatus(pkg, DeliveryStatus.DAGITIMDA, "Yola çıktı");

        assertTrue(notified[0], "Observer dinleyicisine bildirim gitmelidir.");
        assertEquals(DeliveryStatus.DAGITIMDA, capturedStatus[0], "Yakalanan durum DAGITIMDA olmalıdır.");
        assertEquals(DeliveryStatus.DAGITIMDA, pkg.getStatus(), "Paketin durumu güncellenmiş olmalıdır.");
    }

    /**
     * Test 8: JSON veri depolama ve geri yükleme (Persistence).
     */
    public static void testDataPersistenceSaveAndLoad() {
        String testFile = "data/test_logistics_data.json";
        JsonLogisticsRepository repo = new JsonLogisticsRepository(testFile);

        Location loc1 = new Location("T_L1", "Test Deposu", "Testİl", "Testİlçe");
        Location loc2 = new Location("T_L2", "Test Şube", "Testİl", "Testİlçe");
        RouteGraph graph = new RouteGraph();
        graph.addRoad(loc1, loc2, 15.5);

        Vehicle van = new Van("T_V1", "34-TEST-01", "TestVan", 800.0, 5.0, loc1);
        Package pkg = new ExpressPackage("T_P1", "TRK-TEST", "Gond", "Alici", loc2, 12.0, 0.08, 30.0, 12);

        try {
            repo.saveData(Arrays.asList(loc1, loc2), Collections.singletonList(van), Collections.singletonList(pkg), graph);
            assertTrue(repo.exists(), "Test verisi dosyaya yazılmış olmalıdır.");

            List<Location> loadedLocs = new ArrayList<>();
            List<Vehicle> loadedVehicles = new ArrayList<>();
            List<Package> loadedPackages = new ArrayList<>();
            RouteGraph loadedGraph = new RouteGraph();

            repo.loadData(loadedLocs, loadedVehicles, loadedPackages, loadedGraph);

            assertEquals(2, loadedLocs.size(), "2 lokasyon yüklenmeli.");
            assertEquals(1, loadedVehicles.size(), "1 araç yüklenmeli.");
            assertEquals(1, loadedPackages.size(), "1 paket yüklenmeli.");
            assertEquals("34-TEST-01", loadedVehicles.get(0).getPlateNumber(), "Plaka doğru yüklenmeli.");
            assertEquals("TRK-TEST", loadedPackages.get(0).getTrackingCode(), "Takip kodu doğru yüklenmeli.");
        } catch (IOException e) {
            throw new AssertionError("Kalıcı veri testi IO hatası: " + e.getMessage());
        } finally {
            // Test sonrası geçici dosyayı temizle
            new File(testFile).delete();
        }
    }

    /**
     * Test 9: Negatif ağırlık/hacim verildiğinde kapsüllemenin hata fırlatması.
     */
    public static void testEncapsulationAndValidation() {
        Location loc = new Location("L", "Depo", "İst", "Merkez");
        boolean exceptionThrown = false;
        try {
            // Negatif ağırlıklı paket oluşturulamaz
            new StandardPackage("P_ERR", "TRK-ERR", "A", "B", loc, -5.0, 0.1, 20.0);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown, "Negatif ağırlıkta IllegalArgumentException fırlatılmalıdır.");
    }

    /**
     * Test 10: Farklı araçların polimorfik yakıt maliyetleri ve hızları.
     */
    public static void testVehicleOperationalCostPolymorphism() {
        Location loc = new Location("L", "Depo", "İst", "Merkez");
        Vehicle truck = new Truck("T", "34-TRK", "Kamyon", 4000.0, 30.0, loc);
        Vehicle moto = new Motorcycle("M", "34-MTO", "Motor", 35.0, 0.3, loc);

        double dist = 100.0;
        double truckCost = truck.calculateOperationalCost(dist);
        double motoCost = moto.calculateOperationalCost(dist);

        assertTrue(truckCost > motoCost, "Kamyon 100 km maliyeti motosikletten yüksek olmalıdır.");
        assertTrue(moto.getSpeedFactor() > truck.getSpeedFactor(), "Motosiklet hızı kamyondan yüksek olmalıdır.");
    }
}
