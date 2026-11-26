package strategies;

import models.Order;
import models.User;
import services.StockService;

public class BuyingFallbackStrat implements IFallBackStrat {
   @Override
   public boolean execute(StockService svc, Order o) {
      User u = o.getU();
      int qty = o.getQuantity();
      String stockId = o.getS().getId();
      if (svc.tryBuying(stockId, qty)) {
         if (u.executeOrder(o))
            return true;
         svc.sell(stockId, qty);
      }
      return false;
   }
}
