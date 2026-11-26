package strategies;

import models.Order;

public class SellingValidationStrat implements IValidationStrat {
   @Override
   public boolean validateOrder(Order o) {
      int toSell = o.getQuantity();
      return o.getU().getQuantityOfStock(o.getS()) >= toSell;
   }
}
