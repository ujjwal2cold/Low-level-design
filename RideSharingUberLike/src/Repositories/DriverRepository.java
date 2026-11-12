package Repositories;

import Models.Driver;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DriverRepository {
   private final Map<String, Driver> repo = new ConcurrentHashMap<>();

   public void addDriver(Driver driver) {
      String id = driver.getId();
      if (repo.containsKey(id)) {
         return;
      }
      repo.put(id, driver);
   }

   public Driver findById(String id) {
      return repo.getOrDefault(id, null);
   }

   public Collection<Driver> allDrivers() {
      return repo.values();
   }
}
