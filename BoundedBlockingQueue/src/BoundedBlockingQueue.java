import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue<E> {
   private final Queue<E> queue;
   private final ReentrantLock lock = new ReentrantLock();
   private final Condition notFull = lock.newCondition();
   private final Condition notEmpty = lock.newCondition();
   private final AtomicBoolean shut = new AtomicBoolean(false);
   private final int capacity;

   public BoundedBlockingQueue(int capacity) {
      this.queue = new ArrayDeque<>();
      this.capacity = capacity;
   }

   public void put(E ele) {
      lock.lock();
      try {
         if (shut.get())
            throw new IllegalStateException("Queue is shut down");
         while (this.size() == capacity) {
            notFull.await();
         }
         queue.add(ele);
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      } finally {
         notEmpty.signalAll();
         lock.unlock();
      }
   }

   public Optional<E> pop() {
      lock.lock();
      try {
         while (this.size() == 0 && !shut.get()) {
            notEmpty.await();
         }
         if (this.size() > 0)
            return Optional.of(queue.poll());
         return Optional.empty();
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      } finally {
         notFull.signalAll();
         lock.unlock();
      }
   }

   public void shutdown() {
      lock.lock();
      try {
         if (shut.compareAndSet(false, true))
            notEmpty.signalAll();
      } finally {
         lock.unlock();
      }
   }

   public int size() {
      return queue.size();
   }
}
