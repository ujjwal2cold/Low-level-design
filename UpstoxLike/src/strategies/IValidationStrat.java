package strategies;

import models.Order;

public interface IValidationStrat {
   boolean validateOrder(Order o);
}
