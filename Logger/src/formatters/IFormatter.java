package formatters;

import models.LogMessage;

public interface IFormatter {
   String formatMessage(LogMessage message);
}
