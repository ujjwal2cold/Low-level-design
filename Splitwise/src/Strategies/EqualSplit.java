package Strategies;

import Models.Member;
import Models.User;

import java.util.ArrayList;
import java.util.List;

public class EqualSplit implements ISplittingStrategy {
   private final List<User> usersInvolved;
   private double amt;

   public EqualSplit(List<User> usersInvolved, double amt) {
      this.usersInvolved = usersInvolved;
      this.amt = amt;
   }

   @Override
   public List<Member> splitAmongUsers() {
      double individual = amt / usersInvolved.size();
      List<Member> res = new ArrayList<>();
      for (User user : usersInvolved) {
         res.add(new Member(user, individual));
      }
      return res;
   }
}
