package enums;

public enum PRODUCT_TYPE {
   UBER_XL(30, 100),
   UBER_GO(20, 100),
   POOL(15, 100);
   private final int perUnitRate;
   private final int basePrice;
   PRODUCT_TYPE(int perUnitRate, int basePrice) {
      this.perUnitRate = perUnitRate;
      this.basePrice = basePrice;
   }
   public int getPerUnitRate() {
      return perUnitRate;
   }
   public int getBasePrice() {
      return basePrice;
   }

}
