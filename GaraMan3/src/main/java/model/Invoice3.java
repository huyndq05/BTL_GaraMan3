package model;

import java.util.Date;

public class Invoice3 {
    private int id;
    private float totalPrice;
    private Date createdDate;
    private String paymentMethod;
    private String paymentStatus;
    private String note;
    private SalesStaff3 salesStaff;
    private Customer3 customer;
    private RepairOrder3 repairOrder;

    public Invoice3() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public float getTotalPrice() { return totalPrice; }
    public void setTotalPrice(float totalPrice) { this.totalPrice = totalPrice; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public SalesStaff3 getSalesStaff() { return salesStaff; }
    public void setSalesStaff(SalesStaff3 salesStaff) { this.salesStaff = salesStaff; }
    public Customer3 getCustomer() { return customer; }
    public void setCustomer(Customer3 customer) { this.customer = customer; }
    public RepairOrder3 getRepairOrder() { return repairOrder; }
    public void setRepairOrder(RepairOrder3 repairOrder) { this.repairOrder = repairOrder; }
}
