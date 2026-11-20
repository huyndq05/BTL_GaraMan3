package model;

import java.util.Date;

public class ImportOrder3 {
    private int id;
    private Date importDate;
    private float totalPrice;
    private WarehouseStaff3 warehouseStaff;
    private Supplier3 supplier;
    private ImportOrderDetail3[] importDetails;

    public ImportOrder3() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getImportDate() { return importDate; }
    public void setImportDate(Date importDate) { this.importDate = importDate; }
    public float getTotalPrice() { return totalPrice; }
    public void setTotalPrice(float totalPrice) { this.totalPrice = totalPrice; }
    public WarehouseStaff3 getWarehouseStaff() { return warehouseStaff; }
    public void setWarehouseStaff(WarehouseStaff3 warehouseStaff) { this.warehouseStaff = warehouseStaff; }
    public Supplier3 getSupplier() { return supplier; }
    public void setSupplier(Supplier3 supplier) { this.supplier = supplier; }
    public ImportOrderDetail3[] getImportDetails() { return importDetails; }
    public void setImportDetails(ImportOrderDetail3[] importDetails) { this.importDetails = importDetails; }
}
