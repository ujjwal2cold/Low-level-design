import enums.PaymentStatus;
import models.Payment;
import services.PaymentService;
import strategies.PaymentGateway;
import strategies.UPI;

import java.util.concurrent.CompletableFuture;

public class Main {
   public static void main(String[] args) throws InterruptedException {
      PaymentGateway gate = new PaymentGateway(new UPI());
      PaymentService service = new PaymentService(gate, 3);
      Payment pay = new Payment.Builder().setUpiId("XXXX").build();
      Thread t1 = new Thread(() -> {
         for (int i = 0; i < 10; ++i) {
            CompletableFuture<PaymentStatus> status = service.processMyPayment(pay);
            status.thenAccept(System.out::println);
         }
      });
      Thread t2 = new Thread(() -> {
         for (int i = 0; i < 10; ++i) {
            CompletableFuture<PaymentStatus> status = service.processMyPayment(pay);
            status.thenAccept(System.out::println);
         }
      });
      t1.start();
      t2.start();
      t1.join();
      t2.join();
      Thread.sleep(10000);
      System.out.println(pay.getChargesMade().get());
      service.shutdown();
   }
}
