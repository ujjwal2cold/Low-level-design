import Models.User;
import Repositories.ExpenseRepo;
import Repositories.GroupRepo;
import Services.ExpenseMakingService;
import Services.GroupService;
import Services.Splitwise;
import enums.SPLIT_TYPE;

import java.util.List;

class Main {
   public static void main(String[] args) throws InterruptedException {
      User u1 = new User("WASD", "JOHN");
      User u2 = new User("IJK", "JANE");
      User u3 = new User("XYZ", "JACK");
      GroupRepo grpRepo = new GroupRepo();
      ExpenseRepo expRepo = new ExpenseRepo();
      Splitwise splitwise = new Splitwise(new GroupService(grpRepo), new ExpenseMakingService(expRepo));
      int grpId = splitwise.createGroup("Singapore trip", u1);
      splitwise.addInGroup(grpId, u2);
      splitwise.addInGroup(grpId, u3);
      Thread t1 = new Thread(() -> {
         splitwise.addExpense(grpId, u1, List.of(u1, u2, u3), SPLIT_TYPE.EQUAL_SPLIT, 200, null);
         splitwise.showAll(grpId);
      });
      Thread t2 = new Thread(() -> {
         splitwise.addExpense(grpId, u2, List.of(u1, u2, u3), SPLIT_TYPE.EQUAL_SPLIT, 500, null);
         splitwise.showAll(grpId);
      });
      t1.start();
      t2.start();
      t1.join();
      t2.join();
      splitwise.showAll(grpId);
   }
}