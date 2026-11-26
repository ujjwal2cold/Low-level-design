package models;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

public class Stock {
   @Getter
   private final String name;
   @Getter
   private final String id;
   @Getter
   @Setter
   private volatile int currentPrice;
   private final AtomicInteger quantity;

   public Stock(String name, String id, int currentPrice, int quantity) {
      this.name = name;
      this.id = id;
      this.currentPrice = currentPrice;
      this.quantity = new AtomicInteger(quantity);
   }

   public boolean tryBuying(int requestCount) {
      int before = quantity.getAndUpdate(curr -> curr >= requestCount ? curr - requestCount : curr);
      return before >= requestCount;
   }

   public void sell(int requestCount) {
      quantity.updateAndGet(curr -> curr + requestCount);
   }

   @Override
   public String toString() {
      return "Stock{" +
              "name='" + name + '\'' +
              ", id='" + id + '\'' +
              '}';
   }
}
