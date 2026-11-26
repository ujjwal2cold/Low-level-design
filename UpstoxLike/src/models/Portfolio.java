package models;

import factories.ExecutingStratFactory;

import java.util.HashMap;
import java.util.Map;

public class Portfolio {
   private final String userId;
   private final Map<Stock, Integer> quantity = new HashMap<>();

   public Portfolio(String userId) {
      this.userId = userId;
   }

   public int getQuantity(Stock s) {
      return quantity.getOrDefault(s, 0);
   }

   public void executeOrder(Order o) {
      ExecutingStratFactory.strategy(o).executeOrder(quantity, o.getS(), o.getQuantity());
   }

   public void printPortFolio() {
      for (var key : quantity.keySet()) {
         System.out.println("You own " + quantity.get(key) + " shares of " + key);
      }
   }


   public long getMarketValue() {
      long totalValue = 0;
      for (var key : quantity.keySet()) {
         totalValue += (long) key.getCurrentPrice() * quantity.get(key);
      }
      return totalValue;
   }
}
