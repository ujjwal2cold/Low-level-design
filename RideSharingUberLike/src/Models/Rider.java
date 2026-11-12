package Models;

public class Rider {
   private String id;
   private Location currentLocation;

   public Rider(String id) {
      this.id = id;
   }

   public void setCurrentLocation(Location currentLocation) {
      this.currentLocation = currentLocation;
   }

   public String getId() {
      return this.id;
   }
}
