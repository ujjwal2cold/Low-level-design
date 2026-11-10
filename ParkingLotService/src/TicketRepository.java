package parkinglot;

public interface TicketRepository {
   void addTicket(Ticket ticket);

   Ticket getTicketById(int id);
}
