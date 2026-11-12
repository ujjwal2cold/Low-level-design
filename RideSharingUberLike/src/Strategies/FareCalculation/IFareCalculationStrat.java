package Strategies.FareCalculation;

import Models.Location;
import enums.PRODUCT_TYPE;

public interface IFareCalculationStrat {
   int pay( Location src, Location dest, PRODUCT_TYPE product);
}
