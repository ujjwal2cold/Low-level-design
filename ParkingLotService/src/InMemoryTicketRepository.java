package parkinglot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTicketRepository implements TicketRepository {
   private final Map<Integer, Ticket> tickets = new ConcurrentHashMap<>();

   @Override
   public void addTicket(Ticket ticket) {
      tickets.put(ticket.getId(), ticket);
   }

   @Override
   public Ticket getTicketById(int id) {
      Ticket t = tickets.get(id);
      if (t == null) {
         throw new IllegalArgumentException("Ticket not found: " + id);
      }
      return t;
   }
}
