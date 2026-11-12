package Services;

import Models.Location;
import Strategies.FareCalculation.IFareCalculationStrat;
import enums.PRODUCT_TYPE;

import java.util.HashMap;
import java.util.Map;

public class FareCalculationService {
   volatile IFareCalculationStrat strat;

   public FareCalculationService(IFareCalculationStrat strat) {
      setStrat(strat);
   }

   public void setStrat(IFareCalculationStrat strat) {
      this.strat = strat;
   }

   int getFare(Location src, Location dest, PRODUCT_TYPE type) {
      return strat.pay(src, dest, type);
   }

   Map<PRODUCT_TYPE, Integer> makeAllFares(Location src, Location dest) {
      Map<PRODUCT_TYPE, Integer> allFares = new HashMap<>();
      for (PRODUCT_TYPE productType : PRODUCT_TYPE.values()) {
         allFares.put(productType, getFare(src, dest, productType));
      }
      return allFares;
   }
}
