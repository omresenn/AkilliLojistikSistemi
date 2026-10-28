package com.lojistik.repository;

import com.lojistik.model.Location;
import com.lojistik.model.Package;
import com.lojistik.model.RouteGraph;
import com.lojistik.model.Vehicle;

import java.io.IOException;
import java.util.List;

/**
 * Lojistik verilerinin kalıcı olarak saklanması ve yüklenmesi için Repository Arayüzü.
 * 
 * SOLID Prensipleri:
 * - DIP (Dependency Inversion Principle): İş mantığı katmanı doğrudan dosya sistemine değil,
 *   bu arayüze bağımlıdır. İleride JSON yerine SQLite veya MongoDB eklenmek istendiğinde
 *   yalnızca yeni bir repository sınıfı yazılması yeterlidir.
 */
public interface ILogisticsRepository {
    void saveData(List<Location> locations, List<Vehicle> vehicles, List<Package> packages, RouteGraph graph) throws IOException;
    void loadData(List<Location> outLocations, List<Vehicle> outVehicles, List<Package> outPackages, RouteGraph outGraph) throws IOException;
    boolean exists();
}
