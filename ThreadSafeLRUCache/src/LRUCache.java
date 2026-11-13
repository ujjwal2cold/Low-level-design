import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
/**
 * A fixed-capacity cache that evicts the least recently used entry
 * once the capacity is exceeded.
 *
 * <p>Both {@code get} and {@code put} run in O(1) time, using a hash map
 * combined with a doubly linked list. This class is thread-safe.
 *
 * <p>Verified against the LeetCode problem
 * <a href="https://leetcode.com/problems/lru-cache/">LRU Cache</a>.
 *
 * @param <K> the type of keys; must implement {@code equals} and {@code hashCode} consistently
 * @param <V> the type of cached values
 */

class ThreadSafeLRUCache<K, V> {
   private final Map<K, Node<K, V>> map = new HashMap<>();
   private final int capacity;
   private final Node<K, V> head;
   private final Node<K, V> tail;
   private final K sentinelKey;
   private final V sentinelValue;
   private final ReentrantLock lock = new ReentrantLock();

   public ThreadSafeLRUCache(int capacity, K sentinelKey, V sentinelValue) {
      this.capacity = capacity;
      this.sentinelKey = sentinelKey;
      this.sentinelValue = sentinelValue;
      this.head = new Node<>(sentinelKey, sentinelValue);
      this.tail = new Node<>(sentinelKey, sentinelValue);
      this.head.setNext(tail);
      this.tail.setPrev(head);
   }

   public V get(K key) {
      lock.lock();
      try {
         Node<K, V> node = map.get(key);
         if (node == null) {
            return this.sentinelValue;
         }
         node.pop();
         node.moveToFront(head);
         return node.getValue();
      } finally {
         lock.unlock();
      }
   }

   public void put(K key, V value) {
      lock.lock();
      try {
         Node<K, V> node;
         if (!map.containsKey(key)) {
            if (map.size() == this.capacity)
               map.remove(tail.evictPrev());
            node = new Node<>(key, value);
            map.put(key, node);
         } else {
            node = map.get(key);
            node.setValue(value);
            node.pop();
         }
         node.moveToFront(head);
      } finally {
         lock.unlock();
      }
   }
}
