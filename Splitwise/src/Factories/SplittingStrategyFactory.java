package Factories;

import Models.User;
import Strategies.EqualSplit;
import Strategies.ISplittingStrategy;
import Strategies.PercentageSplit;
import enums.SPLIT_TYPE;

import java.util.List;
import java.util.Map;

public class SplittingStrategyFactory {
   public ISplittingStrategy makeSplittingStrategy(SPLIT_TYPE type, List<User> users, double amt, Map<User, Double> percentages) {
      switch (type) {
         case EQUAL_SPLIT -> {
            return new EqualSplit(users, amt);
         }
         case PERCENTAGE_SPLIT -> {
            return new PercentageSplit(percentages, amt);
         }

      }
      throw new IllegalArgumentException("Invalid split type");
   }
}
