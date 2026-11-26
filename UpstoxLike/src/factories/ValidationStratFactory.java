package factories;

import enums.OrderType;
import models.Order;
import strategies.BuyingValidationStrat;
import strategies.IValidationStrat;
import strategies.SellingValidationStrat;

public class ValidationStratFactory {
   public static IValidationStrat createValidationStrat(Order o){
      if(o.getOrder() == OrderType.BUY_ORDER)
         return new BuyingValidationStrat();
      else if(o.getOrder() == OrderType.SELL_ORDER)
         return new SellingValidationStrat();
      throw new IllegalArgumentException("Not a valid order type");
   }
}
