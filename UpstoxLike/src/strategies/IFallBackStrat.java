package strategies;

import models.Order;
import services.StockService;

public interface IFallBackStrat {
   boolean execute(StockService svc, Order o);
}
