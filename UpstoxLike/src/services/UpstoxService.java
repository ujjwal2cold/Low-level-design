package services;

import factories.FallbackStratFactory;
import lombok.AllArgsConstructor;
import models.Order;
import models.Stock;
import models.User;

import java.util.List;

@AllArgsConstructor
public class UpstoxService {
   private final StockService stockService;
   private final UserService userService;

   public void addStock(Stock s) {
      this.stockService.addStock(s);
   }

   public void setCurrentPrice(String stockId, int currPrice) {
      this.stockService.setCurrentPrice(stockId, currPrice);
   }

   public boolean tryBuying(String stockId, int qty) {
      return this.stockService.tryBuying(stockId, qty);
   }

   public void sell(String stockId, int qty) {
      this.stockService.sell(stockId, qty);
   }

   public void createUser(String id, String name) {
      this.userService.createUser(id, name);
   }


   public long getHoldingsWorth(String userId) {
      return this.userService.getHoldingsWorth(userId);
   }

   public void printPortFolio(String userId) {
      this.userService.printPortFolio(userId);
   }

   public List<Order> getOrderHistory(String userId) {
      return this.userService.getOrderHistory(userId);
   }

   public boolean tryExecutingOrder(Order o) {
      String userId = o.getU().getId();
      String stockId = o.getS().getId();
      if (!this.userService.exists(userId) || !this.stockService.exists(stockId))
         return false;
      return FallbackStratFactory.createFallback(o).execute(stockService, o);
   }
}
