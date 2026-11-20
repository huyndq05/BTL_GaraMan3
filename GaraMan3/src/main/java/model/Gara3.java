package model;

public class Gara3 {
    private int id;
    private String name;
    private String phoneNumber;
    private String openTime;
    private String closeTime;
    private String note;
    private FullAddress3 address;
    private StorageZone3[] storageZones;
    private Staff3[] staffs;
    private Technician3[] technicians;

    public Gara3() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }
    public String getCloseTime() { return closeTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public FullAddress3 getAddress() { return address; }
    public void setAddress(FullAddress3 address) { this.address = address; }
    public StorageZone3[] getStorageZones() { return storageZones; }
    public void setStorageZones(StorageZone3[] storageZones) { this.storageZones = storageZones; }
    public Staff3[] getStaffs() { return staffs; }
    public void setStaffs(Staff3[] staffs) { this.staffs = staffs; }
    public Technician3[] getTechnicians() { return technicians; }
    public void setTechnicians(Technician3[] technicians) { this.technicians = technicians; }
}
