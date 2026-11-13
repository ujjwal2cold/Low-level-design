package Models;

public class Bucket {
   private int maxCapacity;
   private double currTokens;
   private double refillRate;
   private long lastRequestTime = System.nanoTime();

   public Bucket(int maxCapacity, int windowInSeconds) {
      this.maxCapacity = maxCapacity;
      this.currTokens = maxCapacity;
      this.refillRate = ((double) (maxCapacity)) / windowInSeconds;
   }

   public synchronized boolean tryMakingRequest() {
      long now = System.nanoTime();
      double seconds = (double) (now - lastRequestTime) / 1_000_000_000;
      currTokens = Math.min(maxCapacity, seconds * refillRate + currTokens);
      lastRequestTime = now;
      if (currTokens >= 1) {
         currTokens -= 1;
         return true;
      }
      return false;
   }
}
