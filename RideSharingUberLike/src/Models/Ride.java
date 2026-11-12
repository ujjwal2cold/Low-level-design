package Models;

import enums.ASSIGN_STATUS;
import enums.PRODUCT_TYPE;
import enums.RIDE_STATUS;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class Ride {
   private static AtomicInteger auto_ID = new AtomicInteger(0);
   private int id;
   private String riderId;
   private String driverId;
   private Location src, dest;
   private Instant expiresAt = Instant.now().plus(Duration.ofSeconds(10));
   private RIDE_STATUS status = RIDE_STATUS.UNBOOKED;
   private PRODUCT_TYPE type;
   private int totalFare;

   public Ride(String riderId, Location src, Location dest, PRODUCT_TYPE type, int fare) {
      this.id = auto_ID.getAndIncrement();
      this.riderId = riderId;
      this.src = src;
      this.dest = dest;
      this.type = type;
      this.totalFare = fare;
   }

   public synchronized ASSIGN_STATUS checkAndAssignDriver(Driver driver) {
      if (this.status == RIDE_STATUS.BOOKED) {
         return ASSIGN_STATUS.RIDE_BOOKED;
      } else if (Instant.now().isAfter(this.expiresAt)) {
         return ASSIGN_STATUS.RIDE_EXPIRED;
      }
      boolean res = driver.tryBook();
      if (res) {
         this.status = RIDE_STATUS.BOOKED;
         this.driverId = driver.getId();
         return ASSIGN_STATUS.RIDE_BOOKED;
      }
      return ASSIGN_STATUS.RIDE_UNBOOKED;
   }

   public Location getSrcLocation() {
      return this.src;
   }

   public PRODUCT_TYPE getType() {
      return this.type;
   }
}
