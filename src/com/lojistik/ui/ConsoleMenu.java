package com.lojistik.ui;

import com.lojistik.factory.PackageFactory;
import com.lojistik.factory.VehicleFactory;
import com.lojistik.model.*;
import com.lojistik.model.Package;
import com.lojistik.service.LogisticsService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Kullanıcı Konsol Arayüzü (CLI).
 * Kullanıcı dostu, menülü ve interaktif bir konsol deneyimi sunar.
 */
public class ConsoleMenu {
    private final LogisticsService service;
    private final Scanner scanner;

    public ConsoleMenu(LogisticsService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            printHeader();
            printMainMenu();
            System.out.print("👉 Seçiminiz (0-8): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handlePackageMenu();
                    break;
                case "2":
                    handleVehicleMenu();
                    break;
                case "3":
                    handleLocationAndRoadMenu();
                    break;
                case "4":
                    handleRouteCalculation();
                    break;
                case "5":
                    handleDistributionOptimization();
                    break;
                case "6":
                    handleDeliverySimulation();
                    break;
                case "7":
                    showSystemStatistics();
                    break;
                case "8":
                    service.saveAllData();
                    System.out.println("💾 [BAŞARILI] Tüm veriler data/logistics_data.json dosyasına kaydedildi.");
                    pause();
                    break;
                case "0":
                    service.saveAllData();
                    System.out.println("\n👋 Akıllı Lojistik Sistemi kapatılıyor. Veriler kaydedildi. İyi günler!\n");
                    exit = true;
                    break;
                default:
                    System.out.println("⚠️ Geçersiz seçim! Lütfen 0 ile 8 arasında bir değer giriniz.");
                    pause();
                    break;
            }
        }
    }

    private void printHeader() {
        System.out.println("\n==========================================================================");
        System.out.println(" 🚚 AKILLI LOJİSTİK SİSTEMİ - KAPASİTE VE ROTA OPTİMİZASYON YÖNETİMİ 📦 ");
        System.out.println("==========================================================================");
    }

    private void printMainMenu() {
        System.out.println(" [1] 📦 Paket Yönetimi (Listele, Yeni Ekle, Detay Gör)");
        System.out.println(" [2] 🚛 Araç Filo Yönetimi (Listele, Yeni Araç Ekle)");
        System.out.println(" [3] 🗺️  Lokasyon ve Yol Haritası Görüntüle / Yol Ekle");
        System.out.println(" [4] 🧭 En Uygun Rota Hesapla (Dijkstra En Kısa Yol Algoritması)");
        System.out.println(" [5] ⚡ Akıllı Paket Dağıtımı Çalıştır (Greedy Knapsack Kapasite Algoritması)");
        System.out.println(" [6] 🚀 Dağıtım Simülasyonu Başlat (Canlı Takip ve Observer Bildirimi)");
        System.out.println(" [7] 📊 Genel Sistem Raporu ve Finansal/Operasyonel İstatistikler");
        System.out.println(" [8] 💾 Verileri Şimdi Kaydet (Kalıcı JSON Depolama)");
        System.out.println(" [0] 🚪 Çıkış");
        System.out.println("--------------------------------------------------------------------------");
    }

    private void handlePackageMenu() {
        System.out.println("\n--- 📦 PAKET YÖNETİMİ ---");
        System.out.println("1. Tüm Paketleri Listele");
        System.out.println("2. Yeni Paket Ekle");
        System.out.println("3. Takip Kodu / ID ile Kargo Sorgula");
        System.out.print("Seçiminiz: ");
        String sub = scanner.nextLine().trim();

        if (sub.equals("1")) {
            listAllPackages();
        } else if (sub.equals("2")) {
            addNewPackageInteractive();
        } else if (sub.equals("3")) {
            searchPackage();
        }
        pause();
    }

    private void listAllPackages() {
        List<Package> packages = service.getPackages();
        if (packages.isEmpty()) {
            System.out.println("Kayıtlı paket bulunmamaktadır.");
            return;
        }
        System.out.println("\n--- TÜM KARGOLAR (" + packages.size() + " Adet) ---");
        for (Package p : packages) {
            System.out.printf("• [%s] #%s | %s -> %s (%s) | %.1f kg, %.3f m³ | Ücret: %.2f TL | Durum: %s\n",
                    p.getPackageType(), p.getTrackingCode(), p.getSenderName(),
                    p.getRecipientName(), p.getDestination().getName(),
                    p.getWeightKg(), p.getVolumeM3(), p.calculateShippingFee(), p.getStatus().getDescription());
            System.out.println("   Talimat: " + p.getHandlingInstructions());
        }
    }

    private void addNewPackageInteractive() {
        try {
            System.out.println("\n--- YENİ PAKET EKLE ---");
            System.out.println("Tür Seçiniz: [1] Standart, [2] Ekspres (Acil), [3] Kırılabilir (Hassas)");
            System.out.print("Tür (1-3): ");
            String t = scanner.nextLine().trim();
            PackageFactory.PackageType pType = PackageFactory.PackageType.STANDARD;
            if (t.equals("2")) pType = PackageFactory.PackageType.EXPRESS;
            else if (t.equals("3")) pType = PackageFactory.PackageType.FRAGILE;

            System.out.print("Kargo ID (Örn: PKG_10): ");
            String id = scanner.nextLine().trim();
            System.out.print("Takip Kodu (Boş bırakırsanız otomatik üretilir): ");
            String code = scanner.nextLine().trim();
            System.out.print("Gönderici Adı: ");
            String sender = scanner.nextLine().trim();
            System.out.print("Alıcı Adı: ");
            String recipient = scanner.nextLine().trim();

            System.out.println("\nMevcut Lokasyonlar:");
            for (Location loc : service.getLocations()) {
                System.out.println(" - " + loc.getId() + " : " + loc.getName());
            }
            System.out.print("Hedef Lokasyon ID: ");
            String locId = scanner.nextLine().trim();
            Location dest = service.findLocationById(locId);
            if (dest == null) {
                System.out.println("❌ Geçersiz lokasyon ID!");
                return;
            }

            System.out.print("Ağırlık (kg, örn: 8.5): ");
            double weight = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            System.out.print("Hacim (m³, örn: 0.08): ");
            double volume = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            System.out.print("Taban Ücret (TL, örn: 25.0): ");
            double baseCost = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));

            Package pkg;
            if (pType == PackageFactory.PackageType.EXPRESS) {
                System.out.print("Taahhüt edilen teslim süresi (saat, örn: 12): ");
                int hours = Integer.parseInt(scanner.nextLine().trim());
                pkg = PackageFactory.createPackage(pType, id, code, sender, recipient, dest, weight, volume, baseCost, hours);
            } else if (pType == PackageFactory.PackageType.FRAGILE) {
                System.out.print("Sigorta bedeli (TL, örn: 50): ");
                double ins = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
                pkg = PackageFactory.createPackage(pType, id, code, sender, recipient, dest, weight, volume, baseCost, ins, true);
            } else {
                pkg = PackageFactory.createPackage(pType, id, code, sender, recipient, dest, weight, volume, baseCost);
            }

            service.addPackage(pkg);
            System.out.println("✅ Paket başarıyla kaydedildi! Hesaplanan Gönderim Bedeli: " + String.format("%.2f TL", pkg.calculateShippingFee()));
        } catch (Exception e) {
            System.out.println("❌ Paket eklenirken hata: " + e.getMessage());
        }
    }

    private void searchPackage() {
        System.out.print("\nSorgulanacak Paket ID veya Takip Kodunu Giriniz: ");
        String q = scanner.nextLine().trim();
        Package p = service.findPackageByIdOrTracking(q);
        if (p == null) {
            System.out.println("❌ Bu ID veya takip koduna ait paket bulunamadı.");
        } else {
            System.out.println("\n📦 Paket Bilgileri:");
            System.out.println(" - ID: " + p.getId());
            System.out.println(" - Takip Kodu: " + p.getTrackingCode());
            System.out.println(" - Tür: " + p.getPackageType());
            System.out.println(" - Gönderici: " + p.getSenderName());
            System.out.println(" - Alıcı: " + p.getRecipientName());
            System.out.println(" - Hedef: " + p.getDestination());
            System.out.println(" - Ağırlık / Hacim: " + p.getWeightKg() + " kg / " + p.getVolumeM3() + " m³");
            System.out.println(" - Gönderi Ücreti: " + String.format("%.2f TL", p.calculateShippingFee()));
            System.out.println(" - Durum: " + p.getStatus().getDescription());
            System.out.println(" - Taşıma Notu: " + p.getHandlingInstructions());
        }
    }

    private void handleVehicleMenu() {
        System.out.println("\n--- 🚛 ARAÇ FİLO YÖNETİMİ ---");
        System.out.println("1. Araç Filosunu ve Doluluk Oranlarını Listele");
        System.out.println("2. Yeni Araç Ekle");
        System.out.print("Seçiminiz: ");
        String sub = scanner.nextLine().trim();

        if (sub.equals("1")) {
            listAllVehicles();
        } else if (sub.equals("2")) {
            addNewVehicleInteractive();
        }
        pause();
    }

    private void listAllVehicles() {
        List<Vehicle> vehicles = service.getVehicles();
        if (vehicles.isEmpty()) {
            System.out.println("Kayıtlı araç bulunmamaktadır.");
            return;
        }
        System.out.println("\n--- FİLO DURUMU (" + vehicles.size() + " Araç) ---");
        for (Vehicle v : vehicles) {
            System.out.printf("• [%s] Plaka: %s (%s)\n", v.getVehicleType(), v.getPlateNumber(), v.getModel());
            System.out.printf("   Konum: %s | Durum: %s\n",
                    (v.getCurrentLocation() != null ? v.getCurrentLocation().getName() : "Bilinmiyor"),
                    (v.isActive() ? "Aktif / Göreve Hazır" : "Servis Dışı"));
            System.out.printf("   Yük Doluluğu: %.1f / %.1f kg (%%%.1f) | Hacim: %.2f / %.2f m³ (%%%.1f)\n",
                    v.getCurrentLoadedWeight(), v.getMaxWeightCapacityKg(), v.getWeightOccupancyRate(),
                    v.getCurrentLoadedVolume(), v.getMaxVolumeCapacityM3(), v.getVolumeOccupancyRate());
            System.out.printf("   Yüklü Kargo Sayısı: %d adet\n", v.getLoadedPackages().size());
            if (!v.getLoadedPackages().isEmpty()) {
                System.out.print("   Yüklü Paket Kodları: ");
                for (Package p : v.getLoadedPackages()) {
                    System.out.print(p.getTrackingCode() + " (" + p.getDestination().getName() + ") ");
                }
                System.out.println();
            }
        }
    }

    private void addNewVehicleInteractive() {
        try {
            System.out.println("\n--- YENİ ARAÇ EKLE ---");
            System.out.println("Araç Tipi: [1] Ağır Yük Kamyonu, [2] Dağıtım Panelvanı, [3] Kurye Motosikleti");
            System.out.print("Tip (1-3): ");
            String t = scanner.nextLine().trim();
            VehicleFactory.VehicleType vType = VehicleFactory.VehicleType.VAN;
            if (t.equals("1")) vType = VehicleFactory.VehicleType.TRUCK;
            else if (t.equals("3")) vType = VehicleFactory.VehicleType.MOTORCYCLE;

            System.out.print("Araç ID (Örn: VEH_10): ");
            String id = scanner.nextLine().trim();
            System.out.print("Plaka (Örn: 34-ABC-123): ");
            String plate = scanner.nextLine().trim();
            System.out.print("Model/Marka (Örn: Ford Transit 2024): ");
            String model = scanner.nextLine().trim();

            System.out.println("\nBaşlangıç Konumu:");
            for (Location loc : service.getLocations()) {
                System.out.println(" - " + loc.getId() + " : " + loc.getName());
            }
            System.out.print("Lokasyon ID: ");
            String locId = scanner.nextLine().trim();
            Location startLoc = service.findLocationById(locId);

            System.out.print("Azami Ağırlık Kapasitesi (kg): ");
            double maxW = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            System.out.print("Azami Hacim Kapasitesi (m³): ");
            double maxV = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));

            Vehicle vehicle = VehicleFactory.createVehicle(vType, id, plate, model, maxW, maxV, startLoc);
            service.addVehicle(vehicle);
            System.out.println("✅ Araç filoya başarıyla eklendi: " + vehicle.getPlateNumber());
        } catch (Exception e) {
            System.out.println("❌ Araç ekleme hatası: " + e.getMessage());
        }
    }

    private void handleLocationAndRoadMenu() {
        System.out.println("\n--- 🗺️ LOKASYONLAR VE YOL HARİTASI ---");
        System.out.println("Mevcut Dağıtım Merkezleri & Şubeler:");
        for (Location loc : service.getLocations()) {
            System.out.println(" • " + loc.getId() + " : " + loc.getName() + " [" + loc.getDistrict() + " / " + loc.getCity() + "]");
        }

        System.out.println("\nAktif Karayolu Bağlantıları ve Mesafeler (Graf Ağı):");
        RouteGraph graph = service.getRouteGraph();
        for (Location loc : service.getLocations()) {
            Map<String, Double> neighbors = graph.getNeighbors(loc.getId());
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                Location toLoc = service.findLocationById(entry.getKey());
                if (toLoc != null && loc.getId().compareTo(toLoc.getId()) < 0) {
                    System.out.printf("  🛣️  %s <==== (%.1f km) ====> %s\n", loc.getName(), entry.getValue(), toLoc.getName());
                }
            }
        }
        pause();
    }

    private void handleRouteCalculation() {
        System.out.println("\n--- 🧭 EN KISA ROTA HESAPLAMA (Dijkstra Algoritması) ---");
        List<Location> locs = service.getLocations();
        for (int i = 0; i < locs.size(); i++) {
            System.out.println(" [" + (i + 1) + "] " + locs.get(i).getName());
        }

        try {
            System.out.print("Başlangıç Noktası Numarası: ");
            int sIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            System.out.print("Hedef Noktası Numarası: ");
            int dIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (sIdx >= 0 && sIdx < locs.size() && dIdx >= 0 && dIdx < locs.size()) {
                Location start = locs.get(sIdx);
                Location dest = locs.get(dIdx);

                System.out.println("\n🔍 Dijkstra Algoritması çalıştırılıyor...");
                RouteResult result = service.calculateRoute(start, dest, 70.0);

                if (result.isFound()) {
                    System.out.println("🎯 OPTİMUM ROTA BULUNDU:");
                    System.out.printf("   Toplam Mesafe: %.2f km\n", result.getTotalDistanceKm());
                    System.out.printf("   Tahmini Seyahat Süresi (70 km/s ile): %.1f dakika\n", result.getEstimatedTimeMinutes());
                    System.out.print("   Güzergah: ");
                    for (int i = 0; i < result.getPath().size(); i++) {
                        System.out.print(result.getPath().get(i).getName());
                        if (i < result.getPath().size() - 1) {
                            System.out.print(" ➔ ");
                        }
                    }
                    System.out.println();
                } else {
                    System.out.println("❌ Bu iki lokasyon arasında bağlantılı yol bulunamadı.");
                }
            } else {
                System.out.println("⚠️ Geçersiz numara seçimi.");
            }
        } catch (Exception e) {
            System.out.println("❌ Rota hesaplama hatası: " + e.getMessage());
        }
        pause();
    }

    private void handleDistributionOptimization() {
        System.out.println("\n--- ⚡ AKILLI PAKET DAĞITIMI (Greedy Knapsack Kapasite Algoritması) ---");
        System.out.println("Beklemedeki paketler önceliklerine (Ekspres > Kırılabilir > Standart)");
        System.out.println("ve araçların azami ağırlık/hacim kapasitelerine göre paylaştırılıyor...");

        Map<Vehicle, List<Package>> plan = service.runDistributionOptimization();

        int totalAssigned = 0;
        System.out.println("\n📋 DAĞITIM VE YÜKLEME PLANI SONUCU:");
        for (Map.Entry<Vehicle, List<Package>> entry : plan.entrySet()) {
            Vehicle v = entry.getKey();
            List<Package> pkgs = entry.getValue();
            totalAssigned += pkgs.size();

            System.out.println("──────────────────────────────────────────────────────────────────");
            System.out.printf("🚗 ARAÇ: %s (%s) - Plaka: %s\n", v.getVehicleType(), v.getModel(), v.getPlateNumber());
            System.out.printf("   Yüklenen Paket Sayısı: %d adet\n", pkgs.size());
            System.out.printf("   Mevcut Doluluk: %.1f / %.1f kg (%%%.1f) | %.2f / %.2f m³ (%%%.1f)\n",
                    v.getCurrentLoadedWeight(), v.getMaxWeightCapacityKg(), v.getWeightOccupancyRate(),
                    v.getCurrentLoadedVolume(), v.getMaxVolumeCapacityM3(), v.getVolumeOccupancyRate());

            for (Package p : pkgs) {
                System.out.printf("     📦 #%s | %s | Hedef: %s (%.1f kg)\n",
                        p.getTrackingCode(), p.getPackageType(), p.getDestination().getName(), p.getWeightKg());
            }
        }

        System.out.println("──────────────────────────────────────────────────────────────────");
        System.out.println("✅ Toplam " + totalAssigned + " kargo araçlara optimum şekilde yüklendi.");
        pause();
    }

    private void handleDeliverySimulation() {
        System.out.println("\n--- 🚀 CANLI DAĞITIM SİMÜLASYONU (Observer Bildirimleri) ---");
        List<Vehicle> vehicles = service.getVehicles();

        System.out.println("Yüklü paketi olan araçlar:");
        int count = 0;
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle v = vehicles.get(i);
            if (!v.getLoadedPackages().isEmpty()) {
                System.out.println(" [" + (i + 1) + "] " + v.getPlateNumber() + " - " + v.getVehicleType()
                        + " (" + v.getLoadedPackages().size() + " kargo yüklü)");
                count++;
            }
        }

        if (count == 0) {
            System.out.println("⚠️ Şu an hiçbir araçta yüklü paket yok.");
            System.out.println("Lütfen önce [5] numaralı menüden Paket Dağıtımını çalıştırınız.");
            pause();
            return;
        }

        System.out.print("Görevi başlatılacak araç numarası: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx >= 0 && idx < vehicles.size()) {
                Vehicle selected = vehicles.get(idx);
                service.simulateDeliveryForVehicle(selected);
            } else {
                System.out.println("⚠️ Geçersiz araç numarası.");
            }
        } catch (Exception e) {
            System.out.println("❌ Simülasyon hatası: " + e.getMessage());
        }
        pause();
    }

    private void showSystemStatistics() {
        System.out.println("\n================== 📊 GENEL SİSTEM RAPORU ==================");
        List<Package> packages = service.getPackages();
        List<Vehicle> vehicles = service.getVehicles();

        double totalRevenue = 0.0;
        int deliveredCount = 0;
        int inTransitCount = 0;
        int pendingCount = 0;
        double totalDeliveredWeight = 0.0;

        for (Package p : packages) {
            totalRevenue += p.calculateShippingFee();
            if (p.getStatus() == DeliveryStatus.TESLIM_EDILDI) {
                deliveredCount++;
                totalDeliveredWeight += p.getWeightKg();
            } else if (p.getStatus() == DeliveryStatus.DAGITIMDA || p.getStatus() == DeliveryStatus.ARACA_YUKLENDI) {
                inTransitCount++;
            } else if (p.getStatus() == DeliveryStatus.BEKLEMEDE) {
                pendingCount++;
            }
        }

        System.out.printf("• Toplam Kayıtlı Kargo: %d adet\n", packages.size());
        System.out.printf("   - Teslim Edilenler: %d adet (%.1f kg)\n", deliveredCount, totalDeliveredWeight);
        System.out.printf("   - Dağıtımda / Yüklü: %d adet\n", inTransitCount);
        System.out.printf("   - Depoda Bekleyen: %d adet\n", pendingCount);
        System.out.printf("• Toplam Hesaplanan Ciro / Gelir: %.2f TL\n", totalRevenue);
        System.out.printf("• Filodaki Aktif Araç Sayısı: %d adet\n", vehicles.size());
        System.out.printf("• Dağıtım Merkezi Sayısı: %d lokasyon\n", service.getLocations().size());
        System.out.println("============================================================");
        pause();
    }

    private void pause() {
        System.out.print("\nDevam etmek için [Enter] tuşuna basınız...");
        scanner.nextLine();
    }
}
