package com.lojistik.model;

/**
 * Paket ve teslimat süreçlerinin aşamalarını belirten durum numaralandırması.
 */
public enum DeliveryStatus {
    BEKLEMEDE("Beklemede (Depoda)"),
    ARACA_YUKLENDI("Araca Yüklendi"),
    DAGITIMDA("Dağıtımda (Yolda)"),
    TESLIM_EDILDI("Teslim Edildi"),
    IPTAL("İptal Edildi");

    private final String description;

    DeliveryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
