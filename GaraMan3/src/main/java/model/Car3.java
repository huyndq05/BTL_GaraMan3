package model;

public class Car3 {
    private int id;
    private String plateNumber;
    private String brand;
    private String model;
    private int year;
    private String description;
    private int customerId;

    public Car3() {}

    public Car3(int id, String plateNumber, String brand, String model, int year, String description, int customerId) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.description = description;
        this.customerId = customerId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
}
