package Strategies;

import Models.Config;
import Models.User;

abstract public class IRateLimitingStrategy {
   Config config;

   IRateLimitingStrategy(int maxRequests, int windowInSeconds) {
      config = new Config(maxRequests, windowInSeconds);
   }

   public abstract boolean allowRequest(User u);
}
