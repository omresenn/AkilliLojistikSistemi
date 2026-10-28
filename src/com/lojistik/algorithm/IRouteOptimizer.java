package com.lojistik.algorithm;

import com.lojistik.model.Location;
import com.lojistik.model.RouteGraph;
import com.lojistik.model.RouteResult;

import java.util.List;

/**
 * Rota optimizasyon algoritmaları için Strateji Arayüzü (Strategy Pattern).
 * OCP (Open-Closed Principle): Farklı rota algoritmaları (Dijkstra, A*, Nearest Neighbor vb.)
 * bu arayüzü uygulayarak sisteme entegre edilebilir.
 */
public interface IRouteOptimizer {
    /**
     * İki lokasyon arasındaki en uygun rotayı hesaplar.
     */
    RouteResult findShortestRoute(RouteGraph graph, Location source, Location target, double vehicleSpeedKmH);

    /**
     * Bir başlangıç noktasından başlayarak birden çok teslimat noktasına uğrayan
     * optimize edilmiş çoklu durak (Multi-stop TSP/Dijkstra) rotasını hesaplar.
     */
    RouteResult optimizeMultiStopRoute(RouteGraph graph, Location start, List<Location> destinations, double vehicleSpeedKmH);
}
