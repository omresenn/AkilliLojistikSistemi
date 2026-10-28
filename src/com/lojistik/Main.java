package com.lojistik;

import com.lojistik.repository.JsonLogisticsRepository;
import com.lojistik.service.LogisticsService;
import com.lojistik.ui.ConsoleMenu;

/**
 * Akıllı Lojistik Sistemi - Ana Giriş Noktası (Main Entry Point).
 * 
 * Nesneye Yönelik Programlama Proje Ödevi
 * Konu: Akıllı Lojistik Sistemi (Araç kapasitesine göre paket dağıtımı ve teslimat rotalarının optimizasyonu)
 */
public class Main {
    public static void main(String[] args) {
        // UTF-8 konsol desteği ve Türkçe karakter uyumu
        try {
            System.setProperty("file.encoding", "UTF-8");
        } catch (Exception ignored) {}

        // Repository ve Servis katmanı başlatılır (Dependency Injection)
        JsonLogisticsRepository repository = new JsonLogisticsRepository("data/logistics_data.json");
        LogisticsService service = new LogisticsService(repository);

        // Sistemin kalıcı verilerle yüklenmesi
        service.initialize();

        // Konsol Menü Arayüzünün Başlatılması
        ConsoleMenu menu = new ConsoleMenu(service);
        menu.start();
    }
}
