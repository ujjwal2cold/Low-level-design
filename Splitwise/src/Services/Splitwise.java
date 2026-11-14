package Services;

import Models.Expense;
import Models.User;
import enums.SPLIT_TYPE;

import java.util.List;
import java.util.Map;

public class Splitwise {
   private final GroupService grpService;
   private final ExpenseMakingService expService;

   public Splitwise(GroupService grpService, ExpenseMakingService expService) {
      this.grpService = grpService;
      this.expService = expService;
   }

   public int createGroup(String name, User admin) {
      return grpService.makeGroup(name, admin);
   }

   public void addInGroup(int groupId, User newUser) {
      grpService.addMember(groupId, newUser);
   }

   public void addExpense(int groupId, User paidBy, List<User> involvedUsers, SPLIT_TYPE type, double amt, Map<User, Double> share) {
      if (grpService.isValidGroup(groupId)) {
         Expense e = expService.createExpense(groupId, paidBy, type, involvedUsers, amt, share);
         grpService.addExpense(groupId, e);
      }
   }
   public void showAll(int grpId){
      grpService.showGroup(grpId);
   }

}
