package strategies;

import models.Stock;

import java.util.Map;

public class BuyingStrat implements IExecutingStrategy {
   @Override
   public void executeOrder(Map<Stock, Integer> quantityMap, Stock s, int qty) {
      quantityMap.compute(s, (stock, have) -> {
         return have == null ? qty : have + qty;
      });
   }
}
