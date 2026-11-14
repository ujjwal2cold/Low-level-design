package Repositories;

import Models.Expense;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExpenseRepo {
   private final Map<Integer, Expense> expenses;

   public ExpenseRepo() {
      this.expenses = new HashMap<>();
   }

   public void addExpense(Expense e) {
      this.expenses.put(e.getId(), e);
   }

   public Optional<Expense> findById(int expenseId) {
      return Optional.ofNullable(this.expenses.get(expenseId));
   }
}
