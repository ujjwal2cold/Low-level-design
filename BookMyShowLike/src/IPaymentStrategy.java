interface IPaymentStrategy {
   void pay(int price);
   void refund(int price);
}
