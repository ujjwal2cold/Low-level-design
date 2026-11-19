package factories;

import enums.ChannelType;
import strategies.EmailSending;
import strategies.ISendingStrategy;
import strategies.SMSSending;

public class NotificationChannelFactory {
   public static ISendingStrategy makeChannel(ChannelType type) {
      switch (type) {
         case ChannelType.EMAIL -> {
            return new EmailSending();
         }
         case ChannelType.SMS -> {
            return new SMSSending();
         }
      }
      throw new IllegalArgumentException("Such a channel is not supported");
   }
}
