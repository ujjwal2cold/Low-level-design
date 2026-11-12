package Models;

import enums.PRODUCT_TYPE;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Driver {
   private String id;
   private Set<PRODUCT_TYPE> supportedProducts;
   private volatile Location currentLocation;
   private AtomicBoolean isAvailable = new AtomicBoolean(true);

   public Driver(String id) {
      this.id = id;
      this.supportedProducts = ConcurrentHashMap.newKeySet();
   }

   public void addProduct(PRODUCT_TYPE product) {
      supportedProducts.add(product);
   }

   public boolean supportsProduct(PRODUCT_TYPE product) {
      return supportedProducts.contains(product);
   }

   public String getId() {
      return this.id;
   }

   public void setCurrentLocation(Location currentLocation) {
      this.currentLocation = currentLocation;
   }

   public Location getCurrentLocation() {
      return this.currentLocation;
   }

   public boolean tryBook() {
      return isAvailable.compareAndSet(true, false);
   }

   public boolean markAvailable() {
      return isAvailable.compareAndSet(false, true);
   }

   public boolean isAvailable() {
      return isAvailable.get();
   }
}
