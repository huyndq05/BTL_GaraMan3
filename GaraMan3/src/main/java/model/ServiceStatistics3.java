package model;

public class ServiceStatistics3 extends Service3 {
    private int usageCount;
    private float totalRevenue;

    public ServiceStatistics3() {}

    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }
    public float getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(float totalRevenue) { this.totalRevenue = totalRevenue; }
}
