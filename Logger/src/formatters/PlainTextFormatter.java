package formatters;

import enums.LogLevel;
import models.LogMessage;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PlainTextFormatter implements IFormatter {
   private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

   @Override
   public String formatMessage(LogMessage message) {
      String date = formatter.format(Instant.ofEpochMilli(message.getTimestamp()));
      LogLevel level = message.getLevel();
      String actualMessage = message.getActualMessage();
      return date + " " + level + ": " + actualMessage;
   }
}
