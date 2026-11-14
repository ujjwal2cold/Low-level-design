package Models;

public class Member {
   private User user;
   private double amt;
   public Member(User user, double amt) {
      this.user = user;
      this.amt = amt;
   }
   public User getUser(){
      return this.user;
   }
   public double getAmt(){
      return this.amt;
   }
}
