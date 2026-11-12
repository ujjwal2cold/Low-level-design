package Strategies.FareCalculation;

import Models.Location;
import enums.PRODUCT_TYPE;

public class DistanceBaseFare implements IFareCalculationStrat{

   @Override
   public int pay(Location src, Location dest, PRODUCT_TYPE product) {
      int units = src.getDistanceFrom(dest);
      return product.getBasePrice() + units * product.getPerUnitRate();
   }
}
