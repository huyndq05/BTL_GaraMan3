package model;

public class AppointmentServiceDetail3 {
    private int id;
    private Service3 service;

    public AppointmentServiceDetail3() {}

    public AppointmentServiceDetail3(int id, Service3 service) {
        this.id = id;
        this.service = service;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Service3 getService() { return service; }
    public void setService(Service3 service) { this.service = service; }
}
