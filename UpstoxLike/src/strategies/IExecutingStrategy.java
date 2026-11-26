package strategies;

import models.Stock;

import java.util.Map;

public interface IExecutingStrategy {
   void executeOrder(Map<Stock, Integer> quantityMap, Stock s, int qty);
}
