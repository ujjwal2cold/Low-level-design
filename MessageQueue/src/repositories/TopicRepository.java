package repositories;

import model.Topic;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TopicRepository {
   private final Map<Integer, Topic> map = new ConcurrentHashMap<>();

   public void addTopic(Topic t) {
      map.put(t.getId(), t);
   }

   public Optional<Topic> findById(int id) {
      return Optional.ofNullable(map.get(id));
   }
}
