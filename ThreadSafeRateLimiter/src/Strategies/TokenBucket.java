package Strategies;

import Models.Bucket;
import Models.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucket extends IRateLimitingStrategy {
   Map<Integer, Bucket> buckets = new ConcurrentHashMap<>();

   public TokenBucket(int maxRequests, int windowInSeconds) {
      super(maxRequests, windowInSeconds);
   }

   @Override
   public boolean allowRequest(User u) {
      int userId = u.getId();
      Bucket b = buckets.computeIfAbsent(userId,
              k -> new Bucket(config.getMaxRequests() + u.getTier().getMoreRequests(), config.getWindowInSeconds()));
      return b.tryMakingRequest();
   }
}
