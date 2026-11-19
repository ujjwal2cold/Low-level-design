package models;

import enums.ChannelType;
import enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Getter
public class User {
   private String id, name, email, mobileNumber;
   private final Map<NotificationType, Set<ChannelType>> preferredSettings = new ConcurrentHashMap<>();
   public void addPreference(NotificationType nType, ChannelType cType){
      Set<ChannelType> type = preferredSettings.computeIfAbsent(nType, k -> ConcurrentHashMap.newKeySet());
      type.add(cType);
   }
   public Set<ChannelType> preferred(NotificationType type){
      return Set.copyOf(preferredSettings.getOrDefault(type, new HashSet<>()));
   }
}
