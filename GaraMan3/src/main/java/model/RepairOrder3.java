package model;

import java.util.Date;

public class RepairOrder3 {
    private int id;
    private Date createdDate;
    private Date completedDate;
    private String status;
    private String note;
    private SalesStaff3 salesStaff;
    private Car3 car;
    private Customer3 customer;
    private RepairSparePartDetail3[] repairSpareParts;
    private RepairServiceDetail3[] repairServices;

    public RepairOrder3() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Date getCompletedDate() { return completedDate; }
    public void setCompletedDate(Date completedDate) { this.completedDate = completedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public SalesStaff3 getSalesStaff() { return salesStaff; }
    public void setSalesStaff(SalesStaff3 salesStaff) { this.salesStaff = salesStaff; }
    public Car3 getCar() { return car; }
    public void setCar(Car3 car) { this.car = car; }
    public Customer3 getCustomer() { return customer; }
    public void setCustomer(Customer3 customer) { this.customer = customer; }
    public RepairSparePartDetail3[] getRepairSpareParts() { return repairSpareParts; }
    public void setRepairSpareParts(RepairSparePartDetail3[] repairSpareParts) { this.repairSpareParts = repairSpareParts; }
    public RepairServiceDetail3[] getRepairServices() { return repairServices; }
    public void setRepairServices(RepairServiceDetail3[] repairServices) { this.repairServices = repairServices; }

    public float getTotalAmount() {
        float total = 0;
        if (repairServices != null) {
            for (RepairServiceDetail3 s : repairServices) {
                if (s != null) total += s.getPrice();
            }
        }
        if (repairSpareParts != null) {
            for (RepairSparePartDetail3 p : repairSpareParts) {
                if (p != null) total += p.getPrice() * p.getQuantity();
            }
        }
        return total;
    }
}
