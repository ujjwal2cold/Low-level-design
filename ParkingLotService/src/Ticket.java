package parkinglot;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Ticket {
   private static final AtomicInteger COUNTER = new AtomicInteger(1);

   private final int id;
   private final ParkingSpot spot;
   private final Vehicle vehicle;
   private final Date now;
   private volatile STATUS status;

   public Ticket(ParkingSpot spot, Vehicle vehicle, Date now) {
      this.id = COUNTER.getAndIncrement();
      this.spot = spot;
      this.vehicle = vehicle;
      this.now = now;
      this.status = STATUS.VALID;
   }

   public int getId() {
      return id;
   }

   public ParkingSpot getSpot() {
      return spot;
   }

   public Vehicle getVehicle() {
      return vehicle;
   }

   public Date getNow() {
      return now;
   }

   public STATUS getStatus() {
      return status;
   }

   public void setStatus(STATUS status) {
      this.status = status;
   }
}
