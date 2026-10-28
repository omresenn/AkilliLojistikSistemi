package com.lojistik.model;

import java.util.*;

/**
 * Şehirler/ilçeler arası yol ağını (Weighted Graph) modelleyen sınıf.
 * Dijkstra ve diğer rota algoritmaları bu grafik yapısı üzerinde çalışır.
 */
public class RouteGraph {
    private final Map<String, Location> locations;
    private final Map<String, Map<String, Double>> adjacencyList;

    public RouteGraph() {
        this.locations = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Ağa yeni bir lokasyon düğümü ekler.
     */
    public void addLocation(Location location) {
        if (location == null) return;
        locations.putIfAbsent(location.getId(), location);
        adjacencyList.putIfAbsent(location.getId(), new HashMap<>());
    }

    /**
     * İki lokasyon arasına çift yönlü (iki taraflı) yol ve mesafe bilgisi ekler.
     */
    public void addRoad(Location from, Location to, double distanceKm) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Lokasyonlar boş olamaz.");
        }
        if (distanceKm <= 0) {
            throw new IllegalArgumentException("Yol mesafesi sıfırdan büyük olmalıdır.");
        }
        addLocation(from);
        addLocation(to);

        adjacencyList.get(from.getId()).put(to.getId(), distanceKm);
        adjacencyList.get(to.getId()).put(from.getId(), distanceKm);
    }

    public Location getLocation(String id) {
        return locations.get(id);
    }

    public Collection<Location> getAllLocations() {
        return Collections.unmodifiableCollection(locations.values());
    }

    public Map<String, Double> getNeighbors(String locationId) {
        return adjacencyList.getOrDefault(locationId, Collections.emptyMap());
    }

    public boolean containsLocation(String id) {
        return locations.containsKey(id);
    }

    public void clear() {
        locations.clear();
        adjacencyList.clear();
    }
}
