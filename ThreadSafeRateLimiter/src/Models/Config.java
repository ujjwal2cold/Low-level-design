package Models;

public class Config {
   private final int maxRequests;
   private final int windowInSeconds;

   public Config(int maxRequests, int windowInSeconds) {
      this.maxRequests = maxRequests;
      this.windowInSeconds = windowInSeconds;
   }

   public int getMaxRequests() {
      return maxRequests;
   }

   public int getWindowInSeconds() {
      return windowInSeconds;
   }
}
