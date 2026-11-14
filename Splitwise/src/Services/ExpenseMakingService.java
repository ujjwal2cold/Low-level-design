package Services;

import Factories.SplittingStrategyFactory;
import Models.Expense;
import Models.Member;
import Models.User;
import Repositories.ExpenseRepo;
import Strategies.ISplittingStrategy;
import enums.SPLIT_TYPE;

import java.util.List;
import java.util.Map;

public class ExpenseMakingService {
   ExpenseRepo repo;

   public ExpenseMakingService(ExpenseRepo repo) {
      this.repo = repo;
   }

   Expense createExpense(int groupId, User paidBy, SPLIT_TYPE type, List<User> members, double amount, Map<User, Double> percentages) {
      SplittingStrategyFactory fact = new SplittingStrategyFactory();
      ISplittingStrategy strat = fact.makeSplittingStrategy(type, members, amount, percentages);
      List<Member> res = strat.splitAmongUsers();
      Expense e = new Expense(groupId, amount, paidBy, res);
      repo.addExpense(e);
      return e;
   }
}
