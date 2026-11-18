package strategies;

import enums.PaymentStatus;
import models.Payment;

public class PaymentGateway{
   IPaymentStrategy strategy;

   public PaymentGateway(IPaymentStrategy strategy) {
      this.strategy = strategy;
   }

   public PaymentStatus tryPayment(Payment pay) throws InterruptedException {
      return strategy.processPayment(pay);
   }
}
