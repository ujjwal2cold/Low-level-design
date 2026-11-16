package model;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SubscriberWorker implements Runnable {
   private final ISubscriber subscriber;
   private final Topic topic;
   private final AtomicInteger offset = new AtomicInteger(0);

   public SubscriberWorker(ISubscriber subscriber, Topic t) {
      this.subscriber = subscriber;
      this.topic = t;
   }

   @Override
   public void run() {
      do {
         synchronized (subscriber) {
            while (offset.get() > topic.getSize()) {
               try {
                  subscriber.wait();
               } catch (InterruptedException e) {
                  throw new RuntimeException(e);
               }
            }
            List<String> messages = topic.fetchMessages(offset.get());
            int sz = messages.size();
            for (String message : messages) {
               subscriber.doWork(message);
            }
            offset.addAndGet(sz);
         }
      }
      while (true);
   }

   public synchronized void tryWakingUp() {
      synchronized (subscriber) {
         subscriber.notifyAll();
      }
   }
}
