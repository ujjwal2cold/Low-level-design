package Factories;

import Strategies.IRateLimitingStrategy;
import Strategies.SlidingWindow;
import Strategies.TokenBucket;
import enums.ALGORITHMS;

public class RateLimiterFactory {
   public IRateLimitingStrategy createRateLimitingStrategy(ALGORITHMS algorithm, int maxRequests, int windowInSeconds) {
      switch(algorithm) {
         case SLIDING_WINDOW -> {return new SlidingWindow(maxRequests, windowInSeconds);}
         case  TOKEN_BUCKET -> {return new TokenBucket(maxRequests, windowInSeconds);}
      }
      throw new IllegalArgumentException("Not a valid algorithm");
   }
}
