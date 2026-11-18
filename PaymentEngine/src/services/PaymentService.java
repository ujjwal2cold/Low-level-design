package services;

import enums.PaymentStatus;
import models.Payment;
import strategies.PaymentGateway;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentService {
   private final PaymentGateway gateway;
   private ExecutorService service;
   private final Map<UUID, CompletableFuture<PaymentStatus>> map = new ConcurrentHashMap<>();

   public PaymentService(PaymentGateway gateway, int workerThreads) {
      this.gateway = gateway;
      this.service = Executors.newFixedThreadPool(workerThreads);
   }

   public CompletableFuture<PaymentStatus> processMyPayment(Payment pay) {
      CompletableFuture<PaymentStatus> current = new CompletableFuture<>();
      CompletableFuture<PaymentStatus> old = map.putIfAbsent(pay.getUUID(), current);
      if (old != null)
         return old;
      CompletableFuture<PaymentStatus> sendRequest = CompletableFuture.supplyAsync(() -> {
         try {
            return gateway.tryPayment(pay);
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }, service);
      sendRequest.thenAccept(current::complete);
      return current;
   }

   public void shutdown() {
      service.shutdown();
   }
}
