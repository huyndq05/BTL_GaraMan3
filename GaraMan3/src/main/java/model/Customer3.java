package model;

public class Customer3 {
    private int id;
    private String phoneNumber;
    private String email;
    private String note;
    private FullName3 fullName;
    private FullAddress3 address;
    private Car3[] cars;

    public Customer3() {}

    public Customer3(int id, FullName3 fullName, String phoneNumber, String email, FullAddress3 address, String note) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.note = note;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public FullName3 getFullName() { return fullName; }
    public void setFullName(FullName3 fullName) { this.fullName = fullName; }
    public FullAddress3 getAddress() { return address; }
    public void setAddress(FullAddress3 address) { this.address = address; }
    public Car3[] getCars() { return cars; }
    public void setCars(Car3[] cars) { this.cars = cars; }
}
