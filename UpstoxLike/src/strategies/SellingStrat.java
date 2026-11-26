package strategies;

import models.Stock;

import java.util.Map;

public class SellingStrat implements IExecutingStrategy {
   @Override
   public void executeOrder(Map<Stock, Integer> quantityMap, Stock s, int qty) {
      quantityMap.compute(s, (stock, have) -> {
         int curr = have - qty;
         return curr == 0 ? null : curr;
      });
   }
}
