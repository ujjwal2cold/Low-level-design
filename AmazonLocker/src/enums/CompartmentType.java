package enums;

public enum CompartmentType {
   SMALL(20.0, 50.0),
   MEDIUM(50.0, 100.0),
   LARGE(100.0, 200.0);
   private final double vol, wt;

   CompartmentType(double v, double wt) {
      this.vol = v;
      this.wt = wt;
   }

   public boolean canFit(double v, double wt) {
      return this.vol <= v && this.wt <= wt;
   }
}
