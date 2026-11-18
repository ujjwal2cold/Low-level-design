package strategies;

import enums.PaymentStatus;
import models.Payment;

public interface IPaymentStrategy {
   PaymentStatus processPayment(Payment payment) throws InterruptedException;
}
