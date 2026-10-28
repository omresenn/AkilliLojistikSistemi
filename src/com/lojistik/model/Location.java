package com.lojistik.model;

import java.util.Objects;

/**
 * Lojistik ağındaki transfer merkezlerini ve teslimat adreslerini temsil eden sınıf.
 * Kapsülleme (Encapsulation) prensibine uygun olarak tasarlanmıştır.
 */
public class Location {
    private String id;
    private String name;
    private String city;
    private String district;

    public Location(String id, String name, String city, String district) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Lokasyon ID boş olamaz.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Lokasyon adı boş olamaz.");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.city = city != null ? city.trim() : "Genel";
        this.district = district != null ? district.trim() : "Merkez";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Lokasyon ID boş olamaz.");
        }
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Lokasyon adı boş olamaz.");
        }
        this.name = name.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + district + " / " + city + ")";
    }
}
