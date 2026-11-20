package model;

public class SparePart3 {
    private int id;
    private String name;
    private int quantity;
    private String unit;
    private float sellingPrice;
    private String description;

    public SparePart3() {}

    public SparePart3(int id, String name, String unit, float sellingPrice, int quantity, String description) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public float getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(float sellingPrice) { this.sellingPrice = sellingPrice; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
