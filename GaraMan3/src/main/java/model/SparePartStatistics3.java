package model;

public class SparePartStatistics3 extends SparePart3 {
    private int quantityImported;
    private int quantitySold;
    private float totalRevenue;

    public SparePartStatistics3() {}

    public int getQuantityImported() { return quantityImported; }
    public void setQuantityImported(int quantityImported) { this.quantityImported = quantityImported; }
    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }
    public float getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(float totalRevenue) { this.totalRevenue = totalRevenue; }
}
