package model;

public class RepairServiceDetail3 {
    private int id;
    private float price;
    private String note;
    private Service3 service;
    private Technician3 technician;

    public RepairServiceDetail3() {}

    public RepairServiceDetail3(int id, float price, String note, Service3 service, Technician3 technician) {
        this.id = id;
        this.price = price;
        this.note = note;
        this.service = service;
        this.technician = technician;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Service3 getService() { return service; }
    public void setService(Service3 service) { this.service = service; }
    public Technician3 getTechnician() { return technician; }
    public void setTechnician(Technician3 technician) { this.technician = technician; }
}
