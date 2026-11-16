package model;

import java.util.HashMap;
import java.util.Map;

public class TopicHandler {
   private final Topic topic;
   private final Map<Integer, SubscriberWorker> workerThreads;

   public TopicHandler(Topic t) {
      this.topic = t;
      workerThreads = new HashMap<>();
   }

   public synchronized void publishMessage(String message) {
      this.topic.addMessage(message);
      for (ISubscriber each : topic.getSubscribers()) {
         int id = each.getId();
         if (!workerThreads.containsKey(id)) {
            SubscriberWorker worker = new SubscriberWorker(each, this.topic);
            workerThreads.put(id, worker);
            new Thread(worker).start();
         } else {
            SubscriberWorker worker = workerThreads.get(id);
            worker.tryWakingUp();
         }
      }
   }
}
