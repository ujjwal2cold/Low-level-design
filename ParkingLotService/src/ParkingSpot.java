package parkinglot;

public class ParkingSpot {
   private final int id;
   private final VehicleType type;
   private boolean booked;

   public ParkingSpot(int id, VehicleType type) {
      this.id = id;
      this.type = type;
   }

   public int getId() {
      return id;
   }

   public VehicleType getType() {
      return type;
   }

   public boolean isAvailable() {
      return !booked;
   }

   public void book() {
      booked = true;
   }

   public void unbook() {
      booked = false;
   }
}
