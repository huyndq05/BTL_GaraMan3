package model;

import java.util.Date;

public class Appointment3 {
    private int id;
    private Date appointmentDate;
    private String appointmentTime;
    private String status;
    private String note;
    private Customer3 customer;
    private Car3 car;
    private AppointmentServiceDetail3[] appointmentServices;

    public Appointment3() {}

    public Appointment3(int id, Date appointmentDate, String appointmentTime, String status,
                        Customer3 customer, Car3 car, AppointmentServiceDetail3[] appointmentServices) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.customer = customer;
        this.car = car;
        this.appointmentServices = appointmentServices;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Customer3 getCustomer() { return customer; }
    public void setCustomer(Customer3 customer) { this.customer = customer; }
    public Car3 getCar() { return car; }
    public void setCar(Car3 car) { this.car = car; }
    public AppointmentServiceDetail3[] getAppointmentServices() { return appointmentServices; }
    public void setAppointmentServices(AppointmentServiceDetail3[] appointmentServices) { this.appointmentServices = appointmentServices; }
}
