package model;

public class CustomerStatistics3 extends Customer3 {
    private float totalSpent;
    private int visitCount;

    public CustomerStatistics3() {}

    public float getTotalSpent() { return totalSpent; }
    public void setTotalSpent(float totalSpent) { this.totalSpent = totalSpent; }
    public int getVisitCount() { return visitCount; }
    public void setVisitCount(int visitCount) { this.visitCount = visitCount; }
}
