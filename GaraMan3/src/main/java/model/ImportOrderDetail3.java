package model;

public class ImportOrderDetail3 {
    private int id;
    private int quantity;
    private float price;
    private SparePart3 sparePart;

    public ImportOrderDetail3() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public SparePart3 getSparePart() { return sparePart; }
    public void setSparePart(SparePart3 sparePart) { this.sparePart = sparePart; }
}
