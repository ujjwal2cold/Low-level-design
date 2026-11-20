package models;

import enums.Direction;
import lombok.Getter;

import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Elevator implements Runnable {
   @Getter
   private final int id;
   @Getter
   private AtomicReference<Direction> dir = new AtomicReference<>(Direction.IDLE);
   @Getter
   private AtomicInteger currentFloor = new AtomicInteger(0);
   private ReentrantLock lock = new ReentrantLock();
   private final Condition hasWork = lock.newCondition();
   private final TreeMap<Integer, HallRequest> up = new TreeMap<>(), down = new TreeMap<>(Collections.reverseOrder());
   private final TreeSet<Integer> stops = new TreeSet<>();

   public Elevator(int id) {
      this.id = id;
   }

   private boolean noWork() {
      assert lock.isHeldByCurrentThread();
      return up.isEmpty() && down.isEmpty() && stops.isEmpty();
   }

   @Override
   public void run() {
      do {
         lock.lock();
         try {
            while (noWork()) {
               setDir(Direction.IDLE);
               hasWork.await();
            }
            if (getDir().get() == Direction.IDLE) {
               wakeUpDirection();
            }
            System.out.print("Currently At floor : " + getCurrentFloor());
            if (tryServing())
               System.out.print(" and opening door");
            checkForDirection();
            System.out.println(" and its current sweep is : " + getDir().get());
            if (!noWork())
               adjustFloor();
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
         } finally {
            lock.unlock();
         }
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }
      while (true);

   }

   private void adjustFloor() {
      currentFloor.addAndGet(getDir().get() == Direction.UP ? 1 : -1);
   }

   private void checkForDirection() {
      int currFloor = getCurrentFloor().get();
      if (getDir().get() == Direction.UP) {
         if (stops.higher(currFloor) != null)
            return;
         else if (up.higherKey(currFloor) != null)
            return;
         else if (down.lowerKey(currFloor) != null)
            return;
         if (noWork())
            setDir(Direction.IDLE);
         else
            setDir(Direction.DOWN);
      } else {
         if (stops.floor(currFloor) != null)
            return;
         else if (down.higherKey(currFloor) != null)
            return;
         else if (up.lowerKey(currFloor) != null)
            return;
         if (noWork())
            setDir(Direction.IDLE);
         else
            setDir(Direction.UP);
      }
   }

   private boolean tryServing() {
      int currFloor = getCurrentFloor().get();
      if (getDir().get() == Direction.UP) {
         boolean served = stops.remove(currFloor);
         if (up.remove(currFloor) != null)
            served = true;
         boolean nothingAbove = stops.higher(currFloor) == null && up.higherKey(currFloor) == null && down.lowerKey(currFloor) == null;
         if (nothingAbove && down.remove(currFloor) != null) {
            served = true;
         }
         return served;
      } else if (getDir().get() == Direction.DOWN) {
         boolean served = stops.remove(currFloor);
         if (down.remove(currFloor) != null)
            served = true;
         boolean nothingBelow = stops.lower(currFloor) == null && down.higherKey(currFloor) == null && up.lowerKey(currFloor) == null;
         if (nothingBelow && up.remove(currFloor) != null) {
            served = true;
         }
         return served;
      }
      return false;
   }


   private void setDir(Direction direction) {
      dir.set(direction);
   }

   private void wakeUpDirection() {
      int curr = getCurrentFloor().get();
      if (stops.higher(curr) != null || up.higherKey(curr) != null)
         setDir(Direction.UP);
      else
         setDir(Direction.DOWN);
   }


   public void addExternalRequest(HallRequest r) {
      lock.lock();
      try {
         int floor = r.getFloor();
         Direction dir = r.getDirection();
         if (dir == Direction.UP)
            up.put(floor, r);
         else
            down.put(floor, r);
         hasWork.signal();

      } finally {
         lock.unlock();
      }
   }

   public void addInternalRequest(int destination) {
      lock.lock();
      try {
         stops.add(destination);
         hasWork.signal();
      } finally {
         lock.unlock();
      }
   }
}
