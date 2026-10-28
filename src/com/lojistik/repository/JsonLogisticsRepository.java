package com.lojistik.repository;

import com.lojistik.factory.PackageFactory;
import com.lojistik.factory.VehicleFactory;
import com.lojistik.model.*;
import com.lojistik.model.Package;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Verilerin JSON formatında kalıcı (persistent) olarak saklanmasını sağlayan sınıf.
 * Harici kütüphane bağımlılığı olmaksızın (Standart Java kütüphaneleriyle) çalışır.
 * Böylece ödev herhangi bir bilgisayarda harici Maven/Gradle indirmesi gerektirmeden
 * doğrudan derlenip çalıştırılabilir.
 */
public class JsonLogisticsRepository implements ILogisticsRepository {
    private final String filePath;

    public JsonLogisticsRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean exists() {
        return Files.exists(Paths.get(filePath));
    }

    @Override
    public void saveData(List<Location> locations, List<Vehicle> vehicles, List<Package> packages, RouteGraph graph) throws IOException {
        Path path = Paths.get(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        // 1. Lokasyonlar
        sb.append("  \"locations\": [\n");
        for (int i = 0; i < locations.size(); i++) {
            Location loc = locations.get(i);
            sb.append(String.format("    {\"id\": \"%s\", \"name\": \"%s\", \"city\": \"%s\", \"district\": \"%s\"}%s\n",
                    escapeJson(loc.getId()),
                    escapeJson(loc.getName()),
                    escapeJson(loc.getCity()),
                    escapeJson(loc.getDistrict()),
                    (i < locations.size() - 1 ? "," : "")));
        }
        sb.append("  ],\n");

        // 2. Yollar (Graf Bağlantıları)
        sb.append("  \"roads\": [\n");
        List<String> roadLines = new ArrayList<>();
        Set<String> writtenPairs = new HashSet<>();
        for (Location loc : locations) {
            Map<String, Double> neighbors = graph.getNeighbors(loc.getId());
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                String u = loc.getId();
                String v = entry.getKey();
                String key = u.compareTo(v) < 0 ? (u + "<->" + v) : (v + "<->" + u);
                if (!writtenPairs.contains(key)) {
                    writtenPairs.add(key);
                    roadLines.add(String.format(Locale.US, "    {\"from\": \"%s\", \"to\": \"%s\", \"distanceKm\": %.2f}",
                            escapeJson(u), escapeJson(v), entry.getValue()));
                }
            }
        }
        for (int i = 0; i < roadLines.size(); i++) {
            sb.append(roadLines.get(i)).append(i < roadLines.size() - 1 ? ",\n" : "\n");
        }
        sb.append("  ],\n");

        // 3. Araçlar
        sb.append("  \"vehicles\": [\n");
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle v = vehicles.get(i);
            String vType = (v instanceof Truck) ? "TRUCK" : (v instanceof Van ? "VAN" : "MOTORCYCLE");
            String locId = v.getCurrentLocation() != null ? v.getCurrentLocation().getId() : "";
            sb.append(String.format(Locale.US, "    {\"id\": \"%s\", \"type\": \"%s\", \"plate\": \"%s\", \"model\": \"%s\", \"maxWeight\": %.1f, \"maxVolume\": %.2f, \"locationId\": \"%s\"}%s\n",
                    escapeJson(v.getId()),
                    vType,
                    escapeJson(v.getPlateNumber()),
                    escapeJson(v.getModel()),
                    v.getMaxWeightCapacityKg(),
                    v.getMaxVolumeCapacityM3(),
                    escapeJson(locId),
                    (i < vehicles.size() - 1 ? "," : "")));
        }
        sb.append("  ],\n");

        // 4. Paketler
        sb.append("  \"packages\": [\n");
        for (int i = 0; i < packages.size(); i++) {
            Package p = packages.get(i);
            String pType = (p instanceof ExpressPackage) ? "EXPRESS" : (p instanceof FragilePackage ? "FRAGILE" : "STANDARD");
            String destId = p.getDestination() != null ? p.getDestination().getId() : "";
            String extraParam = "0";
            if (p instanceof ExpressPackage) {
                extraParam = String.valueOf(((ExpressPackage) p).getGuaranteedDeliveryHours());
            } else if (p instanceof FragilePackage) {
                extraParam = String.format(Locale.US, "%.2f", ((FragilePackage) p).getInsuranceFee());
            }

            sb.append(String.format(Locale.US, "    {\"id\": \"%s\", \"type\": \"%s\", \"trackingCode\": \"%s\", \"sender\": \"%s\", \"recipient\": \"%s\", \"destinationId\": \"%s\", \"weight\": %.2f, \"volume\": %.4f, \"baseCost\": %.2f, \"status\": \"%s\", \"extra\": \"%s\"}%s\n",
                    escapeJson(p.getId()),
                    pType,
                    escapeJson(p.getTrackingCode()),
                    escapeJson(p.getSenderName()),
                    escapeJson(p.getRecipientName()),
                    escapeJson(destId),
                    p.getWeightKg(),
                    p.getVolumeM3(),
                    p.getBaseCost(),
                    p.getStatus().name(),
                    escapeJson(extraParam),
                    (i < packages.size() - 1 ? "," : "")));
        }
        sb.append("  ]\n");
        sb.append("}\n");

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(sb.toString());
        }
    }

    @Override
    public void loadData(List<Location> outLocations, List<Vehicle> outVehicles, List<Package> outPackages, RouteGraph outGraph) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return;
        }

        String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        Map<String, Location> locationMap = new HashMap<>();

        // Basit ve güvenilir JSON blok ayrıştırıcı
        String locSection = extractSection(content, "locations");
        if (locSection != null) {
            for (String itemStr : splitJsonObjects(locSection)) {
                String id = extractValue(itemStr, "id");
                String name = extractValue(itemStr, "name");
                String city = extractValue(itemStr, "city");
                String district = extractValue(itemStr, "district");
                if (id != null && name != null) {
                    Location loc = new Location(id, name, city, district);
                    outLocations.add(loc);
                    locationMap.put(id, loc);
                    outGraph.addLocation(loc);
                }
            }
        }

        String roadSection = extractSection(content, "roads");
        if (roadSection != null) {
            for (String itemStr : splitJsonObjects(roadSection)) {
                String from = extractValue(itemStr, "from");
                String to = extractValue(itemStr, "to");
                String distStr = extractValue(itemStr, "distanceKm");
                if (from != null && to != null && distStr != null) {
                    Location locFrom = locationMap.get(from);
                    Location locTo = locationMap.get(to);
                    if (locFrom != null && locTo != null) {
                        double dist = Double.parseDouble(distStr.replace(",", "."));
                        outGraph.addRoad(locFrom, locTo, dist);
                    }
                }
            }
        }

        String vehicleSection = extractSection(content, "vehicles");
        if (vehicleSection != null) {
            for (String itemStr : splitJsonObjects(vehicleSection)) {
                String id = extractValue(itemStr, "id");
                String type = extractValue(itemStr, "type");
                String plate = extractValue(itemStr, "plate");
                String model = extractValue(itemStr, "model");
                String maxW = extractValue(itemStr, "maxWeight");
                String maxV = extractValue(itemStr, "maxVolume");
                String locId = extractValue(itemStr, "locationId");

                if (id != null && type != null && plate != null) {
                    Location loc = locationMap.get(locId);
                    VehicleFactory.VehicleType vType = VehicleFactory.VehicleType.valueOf(type);
                    double w = maxW != null ? Double.parseDouble(maxW.replace(",", ".")) : 1000.0;
                    double v = maxV != null ? Double.parseDouble(maxV.replace(",", ".")) : 10.0;
                    Vehicle vehicle = VehicleFactory.createVehicle(vType, id, plate, model, w, v, loc);
                    outVehicles.add(vehicle);
                }
            }
        }

        String packageSection = extractSection(content, "packages");
        if (packageSection != null) {
            for (String itemStr : splitJsonObjects(packageSection)) {
                String id = extractValue(itemStr, "id");
                String type = extractValue(itemStr, "type");
                String trk = extractValue(itemStr, "trackingCode");
                String sender = extractValue(itemStr, "sender");
                String recipient = extractValue(itemStr, "recipient");
                String destId = extractValue(itemStr, "destinationId");
                String weightStr = extractValue(itemStr, "weight");
                String volumeStr = extractValue(itemStr, "volume");
                String costStr = extractValue(itemStr, "baseCost");
                String statusStr = extractValue(itemStr, "status");
                String extraStr = extractValue(itemStr, "extra");

                if (id != null && type != null && destId != null) {
                    Location dest = locationMap.get(destId);
                    if (dest != null) {
                        PackageFactory.PackageType pType = PackageFactory.PackageType.valueOf(type);
                        double weight = weightStr != null ? Double.parseDouble(weightStr.replace(",", ".")) : 5.0;
                        double volume = volumeStr != null ? Double.parseDouble(volumeStr.replace(",", ".")) : 0.05;
                        double cost = costStr != null ? Double.parseDouble(costStr.replace(",", ".")) : 20.0;

                        Package pkg;
                        if (pType == PackageFactory.PackageType.EXPRESS) {
                            int hours = extraStr != null ? Integer.parseInt(extraStr) : 24;
                            pkg = PackageFactory.createPackage(pType, id, trk, sender, recipient, dest, weight, volume, cost, hours);
                        } else if (pType == PackageFactory.PackageType.FRAGILE) {
                            double ins = extraStr != null ? Double.parseDouble(extraStr.replace(",", ".")) : 40.0;
                            pkg = PackageFactory.createPackage(pType, id, trk, sender, recipient, dest, weight, volume, cost, ins, true);
                        } else {
                            pkg = PackageFactory.createPackage(pType, id, trk, sender, recipient, dest, weight, volume, cost);
                        }

                        if (statusStr != null) {
                            try {
                                pkg.setStatus(DeliveryStatus.valueOf(statusStr));
                            } catch (Exception ignored) {}
                        }
                        outPackages.add(pkg);
                    }
                }
            }
        }
    }

    // JSON yardımcı yardımcı metotları
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String extractSection(String json, String key) {
        String marker = "\"" + key + "\"";
        int start = json.indexOf(marker);
        if (start == -1) return null;
        int arrStart = json.indexOf("[", start);
        if (arrStart == -1) return null;
        int depth = 0;
        int arrEnd = -1;
        for (int i = arrStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) {
                    arrEnd = i;
                    break;
                }
            }
        }
        if (arrEnd != -1) {
            return json.substring(arrStart + 1, arrEnd);
        }
        return null;
    }

    private List<String> splitJsonObjects(String section) {
        List<String> list = new ArrayList<>();
        int depth = 0;
        int start = -1;
        for (int i = 0; i < section.length(); i++) {
            char c = section.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    list.add(section.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return list;
    }

    private String extractValue(String objectJson, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(objectJson);
        if (m.find()) {
            int valStart = m.end();
            if (valStart >= objectJson.length()) return null;
            char firstChar = objectJson.charAt(valStart);
            if (firstChar == '\"') {
                int endQuote = objectJson.indexOf('\"', valStart + 1);
                if (endQuote != -1) {
                    return objectJson.substring(valStart + 1, endQuote);
                }
            } else {
                int end = valStart;
                while (end < objectJson.length() && objectJson.charAt(end) != ',' && objectJson.charAt(end) != '}' && objectJson.charAt(end) != '\n') {
                    end++;
                }
                return objectJson.substring(valStart, end).trim();
            }
        }
        return null;
    }
}
