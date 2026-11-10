import java.util.List;

public class Movie {
   private String name;
   private String genre;
   private List<String> cast;
   private String director;
   private int durationInMinutes;
   public Movie(String name, String genre, List<String> cast, String director, int durationInMinutes) {
      this.name = name;
      this.genre = genre;
      this.cast = List.copyOf(cast);
      this.director = director;
      this.durationInMinutes = durationInMinutes;
   }
}
