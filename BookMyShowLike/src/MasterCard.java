class MasterCard implements IPaymentStrategy{
   @Override
   public void pay(int price) {
      System.out.println("Payment of INR :" + price + " made via MasterCard XXXX-XXXX-XXXX");
   }
   @Override
   public void refund(int price) {
      System.out.println("Refund of INR :" + price + " made to MasterCard XXXX-XXXX-XXXX");
   }
}
