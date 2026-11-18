package strategies;

import enums.PaymentStatus;
import lombok.AllArgsConstructor;
import models.Payment;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@AllArgsConstructor
public class MasterCard implements IPaymentStrategy {

   @Override
   public PaymentStatus processPayment(@NotNull Payment payment) throws InterruptedException {
      synchronized (payment) {
         if (!(payment.getStatus() == PaymentStatus.NOT_INITIATED || payment.getStatus() == PaymentStatus.FAILED))
            return payment.getStatus();
         payment.setStatus(PaymentStatus.INITIATED);
      }
      int rng = new Random().nextInt(2000, 5000);
      Thread.sleep(rng);
      synchronized (payment) {
         if (rng > 2500) {
            payment.setStatus(PaymentStatus.FAILED);
            return PaymentStatus.FAILED;
         }
         payment.setStatus(PaymentStatus.COMPLETED);
         return PaymentStatus.COMPLETED;
      }
   }
}
