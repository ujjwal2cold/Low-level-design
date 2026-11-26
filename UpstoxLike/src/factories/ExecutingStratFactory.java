package factories;

import enums.OrderType;
import models.Order;
import strategies.BuyingStrat;
import strategies.IExecutingStrategy;
import strategies.SellingStrat;

public class ExecutingStratFactory {
   public static IExecutingStrategy strategy(Order o){
      if(o.getOrder() == OrderType.BUY_ORDER)
         return new BuyingStrat();
      else if(o.getOrder() == OrderType.SELL_ORDER)
         return new SellingStrat();
      throw new IllegalArgumentException("Not a supported order");
   }
}
