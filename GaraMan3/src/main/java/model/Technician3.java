package model;

public class Technician3 {
    private int id;
    private String phoneNumber;
    private String specialization;
    private float salary;
    private String status;
    private FullName3 fullName;
    private FullAddress3 address;

    public Technician3() {}

    public Technician3(int id, FullName3 fullName, String phoneNumber, String specialization, String status, float salary, FullAddress3 address) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.specialization = specialization;
        this.status = status;
        this.salary = salary;
        this.address = address;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public float getSalary() { return salary; }
    public void setSalary(float salary) { this.salary = salary; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public FullName3 getFullName() { return fullName; }
    public void setFullName(FullName3 fullName) { this.fullName = fullName; }
    public FullAddress3 getAddress() { return address; }
    public void setAddress(FullAddress3 address) { this.address = address; }
}
