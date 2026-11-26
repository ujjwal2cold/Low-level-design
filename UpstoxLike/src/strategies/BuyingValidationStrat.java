package strategies;

import models.Order;

public class BuyingValidationStrat implements IValidationStrat {
   @Override
   public boolean validateOrder(Order o) {
      int money = o.getU().getWallet().get();
      int need = o.getQuantity() * o.getS().getCurrentPrice();
      return money >= need;
   }
}
