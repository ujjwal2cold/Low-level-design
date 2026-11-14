package Strategies;

import Models.Member;
import Models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PercentageSplit implements ISplittingStrategy {
   private final Map<User, Double> shares;
   private double totalAmt;
   public PercentageSplit(Map<User, Double> shares, double totalAmt) {
      this.shares = shares;
      this.totalAmt = totalAmt;
   }
   public List<Member> splitAmongUsers() {
      List<Member> res = new ArrayList<>();
      for(var key : shares.keySet()){
         double theirShare = totalAmt * (shares.get(key) / 100);
         res.add(new Member(key, theirShare));
      }
      return res;
   }
}
