package com.lojistik.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rota optimizasyon algoritması sonucunda ortaya çıkan en uygun güzergahı,
 * toplam mesafeyi ve tahmini süreyi kapsülleyen sınıf.
 */
public class RouteResult {
    private final List<Location> path;
    private final double totalDistanceKm;
    private final double estimatedTimeMinutes;
    private final boolean found;

    public RouteResult(List<Location> path, double totalDistanceKm, double estimatedTimeMinutes) {
        this.path = path != null ? new ArrayList<>(path) : new ArrayList<>();
        this.totalDistanceKm = Math.max(0.0, totalDistanceKm);
        this.estimatedTimeMinutes = Math.max(0.0, estimatedTimeMinutes);
        this.found = !this.path.isEmpty() && totalDistanceKm < Double.MAX_VALUE / 2;
    }

    public static RouteResult notFound() {
        return new RouteResult(Collections.emptyList(), Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public List<Location> getPath() {
        return Collections.unmodifiableList(path);
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public double getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public boolean isFound() {
        return found;
    }

    @Override
    public String toString() {
        if (!found) {
            return "Uygun rota bulunamadı.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Rota [Mesafe: ").append(String.format("%.2f", totalDistanceKm)).append(" km, ");
        sb.append("Süre: ").append(String.format("%.1f", estimatedTimeMinutes)).append(" dk]: ");
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getName());
            if (i < path.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }
}
