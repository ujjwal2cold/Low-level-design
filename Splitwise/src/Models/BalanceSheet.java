package Models;

import java.util.HashMap;
import java.util.Map;

public class BalanceSheet {
   private double totalAmtPaid;
   private final Map<User, Double> oneToOneMap;

   BalanceSheet() {
      this.totalAmtPaid = 0;
      this.oneToOneMap = new HashMap<>();
   }

   public double getTotalAmountPaid() {
      return this.totalAmtPaid;
   }

   public void addTotal(double amt) {
      this.totalAmtPaid += amt;
   }

   public void creditFrom(User other, double amt) {
      oneToOneMap.merge(other, amt, Double::sum);
   }

   public void debitFrom(User other, double amt) {
      oneToOneMap.merge(other, -amt, Double::sum);
   }

   public String describeBalance() {
      StringBuilder result = new StringBuilder();
      for (var other : oneToOneMap.keySet()) {
         double amount = oneToOneMap.get(other);
         if (Math.abs(amount - 0) > 1e-6) {
            result.append(amount < 0 ? String.format("  owes %.2f to %s%n", -amount, other.getName()) : String.format("  is owed %.2f by %s%n", amount, other.getName()));
         }
      }
      if (result.isEmpty())
         result.append("is settled up with everyone");
      return result.toString();
   }

}
