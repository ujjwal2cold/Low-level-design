package services;

import models.Stock;
import repositories.StockRepo;

import java.util.Optional;

public class StockService {
   private final StockRepo sRepo;

   public StockService(StockRepo repo) {
      this.sRepo = repo;
   }

   public void addStock(Stock s) {
      this.sRepo.addStock(s);
   }

   public void setCurrentPrice(String stockId, int currPrice) {
      Optional<Stock> s = this.sRepo.findById(stockId);
      s.ifPresent(stock -> stock.setCurrentPrice(currPrice));
   }

   public boolean exists(String stockId) {
      return this.sRepo.findById(stockId).isPresent();
   }

   public boolean tryBuying(String stockId, int qty) {
      Optional<Stock> s = this.sRepo.findById(stockId);
      return s.map(stock -> stock.tryBuying(qty)).orElse(false);
   }

   public void sell(String stockId, int qty) {
      Optional<Stock> stk = this.sRepo.findById(stockId);
      stk.ifPresent(s -> s.sell(qty));
   }
}
