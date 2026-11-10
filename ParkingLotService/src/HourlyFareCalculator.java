package parkinglot;

import java.util.Date;

public class HourlyFareCalculator implements FareCalculator {
   private final long ratePerHour;

   public HourlyFareCalculator(long ratePerHour) {
      this.ratePerHour = ratePerHour;
   }

   @Override
   public long calculateFare(Date entryTime) {
      long millis = System.currentTimeMillis() - entryTime.getTime();
      long hours = Math.max(1, (long) Math.ceil(millis / 3_600_000.0));
      return hours * ratePerHour;
   }
}
