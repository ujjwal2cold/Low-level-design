package Strategies;

import Models.User;
import enums.USER_TIER;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindow extends IRateLimitingStrategy {
   private Map<Integer, Queue<Long>> requestMap;

   public SlidingWindow(int maxRequests, int windowInSeconds) {
      super(maxRequests, windowInSeconds);
      requestMap = new ConcurrentHashMap<>();
   }

   @Override
   public boolean allowRequest(User u) {
      int userId = u.getId();
      USER_TIER tier = u.getTier();
      Queue<Long> requestQueue = requestMap.computeIfAbsent(userId, k -> new ArrayDeque<>());
      synchronized (requestQueue) {
         long currentTime = System.currentTimeMillis();
         while (!requestQueue.isEmpty() && (currentTime - requestQueue.peek() > this.config.getWindowInSeconds() * 1000L)) {
            requestQueue.poll();
         }
         if (requestQueue.size() >= config.getMaxRequests() + tier.getMoreRequests()) {
            return false;
         }
         requestQueue.add(currentTime);
         return true;
      }
   }
}
