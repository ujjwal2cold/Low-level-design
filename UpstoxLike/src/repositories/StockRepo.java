package repositories;

import models.Stock;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class StockRepo {
   private final Map<String, Stock> map = new ConcurrentHashMap<>();
   public void addStock(Stock s){
      map.put(s.getId(), s);
   }
   public Optional<Stock> findById(String id){
      return Optional.ofNullable(map.get(id));
   }
}
