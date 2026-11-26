package strategies;

import models.Order;
import models.User;
import services.StockService;

public class SellingFallbackStrat implements IFallBackStrat {
   @Override
   public boolean execute(StockService svc, Order o) {
      User u = o.getU();
      int qty = o.getQuantity();
      String stockId = o.getS().getId();
      if (u.executeOrder(o)) {
         svc.sell(stockId, qty);
         return true;
      }
      return false;
   }
}
