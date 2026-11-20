package model;

import java.util.Date;

public class Staff3 {
    protected int id;
    protected String username;
    protected String password;
    protected Date birthDate;
    protected String email;
    protected String phoneNumber;
    protected float salary;
    protected String note;
    protected FullName3 fullName;
    protected FullAddress3 address;

    public Staff3() {}

    public Staff3(int id, String username, String password, Date birthDate, String email,
                  String phoneNumber, float salary, String note, FullName3 fullName, FullAddress3 address) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.birthDate = birthDate;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.salary = salary;
        this.note = note;
        this.fullName = fullName;
        this.address = address;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public float getSalary() { return salary; }
    public void setSalary(float salary) { this.salary = salary; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public FullName3 getFullName() { return fullName; }
    public void setFullName(FullName3 fullName) { this.fullName = fullName; }
    public FullAddress3 getAddress() { return address; }
    public void setAddress(FullAddress3 address) { this.address = address; }
}
