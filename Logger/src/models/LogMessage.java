package models;

import enums.LogLevel;

public class LogMessage {
   private final LogLevel level;
   private final String actualMessage;
   private final long timestamp;

   public LogMessage(LogLevel level, String actualMessage) {
      this.level = level;
      this.actualMessage = actualMessage;
      this.timestamp = System.currentTimeMillis();
   }

   public long getTimestamp() {
      return timestamp;
   }

   public LogLevel getLevel() {
      return level;
   }

   public String getActualMessage() {
      return actualMessage;
   }
}
