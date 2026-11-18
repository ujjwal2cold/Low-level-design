package models;

import enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Payment {
   private final UUID UUID;
   @Setter
   private PaymentStatus status = PaymentStatus.NOT_INITIATED;
   private final String upiID;
   private final String cardNumber;
   private AtomicInteger chargesMade = new AtomicInteger(0);

   private Payment(Builder builder) {
      this.UUID = builder.UUID;
      this.upiID = builder.upiID;
      this.cardNumber = builder.cardNumber;
   }

   public void increaseCharge() {
      chargesMade.incrementAndGet();
   }

   public static class Builder {
      private final UUID UUID;
      private String upiID;
      private String cardNumber;

      public Builder() {
         this.UUID = java.util.UUID.randomUUID();
      }

      public Builder setUpiId(String upiID) {
         this.upiID = upiID;
         return this;
      }

      public Builder setCardNumber(String cardNumber) {
         this.cardNumber = cardNumber;
         return this;
      }

      public Payment build() {
         if (upiID == null && cardNumber == null) {
            throw new IllegalArgumentException("Either upiID or cardNumber must be provided");
         }
         return new Payment(this);
      }
   }
}
