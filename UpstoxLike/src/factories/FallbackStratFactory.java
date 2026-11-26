package factories;

import enums.OrderType;
import models.Order;
import strategies.BuyingFallbackStrat;
import strategies.IFallBackStrat;
import strategies.SellingFallbackStrat;

public class FallbackStratFactory {
   public static IFallBackStrat createFallback(Order o){
      if(o.getOrder() == OrderType.BUY_ORDER)
         return new BuyingFallbackStrat();
      else if(o.getOrder() == OrderType.SELL_ORDER)
         return new SellingFallbackStrat();
      throw new IllegalArgumentException("Not a valid order");
   }
}
