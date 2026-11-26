package models;

import factories.ValidationStratFactory;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
   private final String name;
   @Getter
   private final String id;
   private final Portfolio portfolio;
   private final List<Order> orderHistory;
   private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
   @Getter
   private AtomicInteger wallet = new AtomicInteger(0);

   public User(String name, String id) {
      this.name = name;
      this.id = id;
      this.portfolio = new Portfolio(this.id);
      this.orderHistory = new ArrayList<>();
   }

   public void addMoney(int m) {
      rwLock.writeLock().lock();
      try {
         wallet.addAndGet(m);
      } finally {
         rwLock.writeLock().unlock();
      }
   }

   public int getQuantityOfStock(Stock s) {
      rwLock.readLock().lock();
      try {
         return this.portfolio.getQuantity(s);
      } finally {
         rwLock.readLock().unlock();
      }
   }

   private void addOrder(Order o) {
      orderHistory.add(o);
   }

   public List<Order> getOrderHistory() {
      rwLock.readLock().lock();
      try {
         return List.copyOf(orderHistory);
      } finally {
         rwLock.readLock().unlock();
      }
   }

   public boolean executeOrder(Order o) {
      rwLock.writeLock().lock();
      try {
         if (!ValidationStratFactory.createValidationStrat(o).validateOrder(o))
            return false;
         this.portfolio.executeOrder(o);
         addOrder(o);
         return true;
      } finally {
         rwLock.writeLock().unlock();
      }
   }

   public long getMarketValue() {
      rwLock.readLock().lock();
      try {
         return this.portfolio.getMarketValue();
      } finally {
         rwLock.readLock().unlock();
      }
   }

   public void printPortFolio() {
      rwLock.readLock().lock();
      try {
         this.portfolio.printPortFolio();
      } finally {
         rwLock.readLock().unlock();
      }
   }
}
