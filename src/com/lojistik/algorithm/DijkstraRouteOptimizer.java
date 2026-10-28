package com.lojistik.algorithm;

import com.lojistik.model.Location;
import com.lojistik.model.RouteGraph;
import com.lojistik.model.RouteResult;

import java.util.*;

/**
 * Dijkstra En Kısa Yol Algoritması Uygulaması.
 * 
 * Algoritma Mantığı:
 * - Başlangıç düğümünden diğer tüm düğümlere olan mesafeleri sonsuz, başlangıcı 0 kabul eder.
 * - En küçük mesafeli düğümü seçmek için Min-Heap (PriorityQueue) veri yapısı kullanılır.
 * - Her komşu düğüm için kenar ağırlığı (mesafe) gevşetilir (edge relaxation).
 * - Hedefe ulaşıldığında önceki düğüm (predecessor) haritası üzerinden en kısa rota geriye doğru kurulur.
 * - Zaman Karmaşıklığı: O((V + E) log V)
 */
public class DijkstraRouteOptimizer implements IRouteOptimizer {

    private static class NodeDistance implements Comparable<NodeDistance> {
        final String locationId;
        final double distance;

        NodeDistance(String locationId, double distance) {
            this.locationId = locationId;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeDistance o) {
            return Double.compare(this.distance, o.distance);
        }
    }

    @Override
    public RouteResult findShortestRoute(RouteGraph graph, Location source, Location target, double vehicleSpeedKmH) {
        if (graph == null || source == null || target == null) {
            return RouteResult.notFound();
        }

        if (source.equals(target)) {
            return new RouteResult(Collections.singletonList(source), 0.0, 0.0);
        }

        Map<String, Double> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        PriorityQueue<NodeDistance> priorityQueue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        for (Location loc : graph.getAllLocations()) {
            distances.put(loc.getId(), Double.MAX_VALUE);
        }

        distances.put(source.getId(), 0.0);
        priorityQueue.add(new NodeDistance(source.getId(), 0.0));

        while (!priorityQueue.isEmpty()) {
            NodeDistance current = priorityQueue.poll();
            String currentId = current.locationId;

            if (visited.contains(currentId)) continue;
            visited.add(currentId);

            if (currentId.equals(target.getId())) {
                break; // Hedefe en kısa yoldan ulaşıldı
            }

            Map<String, Double> neighbors = graph.getNeighbors(currentId);
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                String neighborId = entry.getKey();
                double edgeWeight = entry.getValue();

                if (!visited.contains(neighborId)) {
                    double newDist = distances.get(currentId) + edgeWeight;
                    if (newDist < distances.get(neighborId)) {
                        distances.put(neighborId, newDist);
                        predecessors.put(neighborId, currentId);
                        priorityQueue.add(new NodeDistance(neighborId, newDist));
                    }
                }
            }
        }

        double totalDist = distances.get(target.getId());
        if (totalDist >= Double.MAX_VALUE / 2) {
            return RouteResult.notFound();
        }

        // Rotayı geriye doğru yeniden inşa et (Path Reconstruction)
        LinkedList<Location> path = new LinkedList<>();
        String curr = target.getId();
        while (curr != null) {
            Location loc = graph.getLocation(curr);
            if (loc != null) {
                path.addFirst(loc);
            }
            curr = predecessors.get(curr);
        }

        // Hıza göre tahmini varış süresi (dakika cinsinden): (Mesafe / Hız) * 60
        double speed = vehicleSpeedKmH > 0 ? vehicleSpeedKmH : 60.0;
        double estimatedMinutes = (totalDist / speed) * 60.0;

        return new RouteResult(path, totalDist, estimatedMinutes);
    }

    @Override
    public RouteResult optimizeMultiStopRoute(RouteGraph graph, Location start, List<Location> destinations, double vehicleSpeedKmH) {
        if (destinations == null || destinations.isEmpty()) {
            return new RouteResult(Collections.singletonList(start), 0.0, 0.0);
        }

        List<Location> fullPath = new ArrayList<>();
        fullPath.add(start);

        List<Location> remaining = new ArrayList<>(new LinkedHashSet<>(destinations));
        remaining.remove(start);

        Location current = start;
        double totalDistance = 0.0;

        // Nearest Neighbor Greedy Heuristic with Dijkstra Shortest Path
        while (!remaining.isEmpty()) {
            Location nextClosest = null;
            double minSegmentDist = Double.MAX_VALUE;
            RouteResult bestSegment = null;

            for (Location candidate : remaining) {
                RouteResult seg = findShortestRoute(graph, current, candidate, vehicleSpeedKmH);
                if (seg.isFound() && seg.getTotalDistanceKm() < minSegmentDist) {
                    minSegmentDist = seg.getTotalDistanceKm();
                    nextClosest = candidate;
                    bestSegment = seg;
                }
            }

            if (nextClosest == null || bestSegment == null) {
                break; // Kalan lokasyonlara ulaşılamıyor
            }

            // Segment yollarını ekle (ilk eleman zaten var, sonrakileri ekle)
            List<Location> segPath = bestSegment.getPath();
            for (int i = 1; i < segPath.size(); i++) {
                fullPath.add(segPath.get(i));
            }

            totalDistance += minSegmentDist;
            remaining.remove(nextClosest);
            current = nextClosest;
        }

        double speed = vehicleSpeedKmH > 0 ? vehicleSpeedKmH : 60.0;
        double estimatedMinutes = (totalDistance / speed) * 60.0;

        return new RouteResult(fullPath, totalDistance, estimatedMinutes);
    }
}
