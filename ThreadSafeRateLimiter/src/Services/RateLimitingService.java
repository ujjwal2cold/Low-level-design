package Services;

import Factories.RateLimiterFactory;
import Models.User;
import Strategies.IRateLimitingStrategy;
import enums.ALGORITHMS;

public class RateLimitingService {
   IRateLimitingStrategy strat;

   public RateLimitingService(ALGORITHMS algo, int maxRequests, int windowInSeconds) {
      RateLimiterFactory fact = new RateLimiterFactory();
      strat = fact.createRateLimitingStrategy(algo, maxRequests, windowInSeconds);
   }

   public boolean allowRequest(User u) {
      return strat.allowRequest(u);
   }
}
