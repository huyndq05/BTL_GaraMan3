package model;

public class RepairSparePartDetail3 {
    private int id;
    private int quantity;
    private float price;
    private SparePart3 sparePart;

    public RepairSparePartDetail3() {}

    public RepairSparePartDetail3(int id, int quantity, float price, SparePart3 sparePart) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.sparePart = sparePart;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public SparePart3 getSparePart() { return sparePart; }
    public void setSparePart(SparePart3 sparePart) { this.sparePart = sparePart; }
}
