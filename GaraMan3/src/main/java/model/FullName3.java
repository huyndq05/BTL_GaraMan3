package model;

public class FullName3 {
    private int id;
    private String lastName;
    private String middleName;
    private String firstName;

    public FullName3() {}

    public FullName3(int id, String lastName, String middleName, String firstName) {
        this.id = id;
        this.lastName = lastName;
        this.middleName = middleName;
        this.firstName = firstName;
    }

    public static FullName3 fromString(String full) {
        if (full == null || full.trim().isEmpty()) return new FullName3(0, "", "", "");
        String[] parts = full.trim().split("\\s+");
        if (parts.length == 1) return new FullName3(0, "", "", parts[0]);
        if (parts.length == 2) return new FullName3(0, parts[0], "", parts[1]);
        StringBuilder mid = new StringBuilder();
        for (int i = 1; i < parts.length - 1; i++) {
            if (mid.length() > 0) mid.append(" ");
            mid.append(parts[i]);
        }
        return new FullName3(0, parts[0], mid.toString(), parts[parts.length - 1]);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lastName != null && !lastName.trim().isEmpty()) sb.append(lastName.trim());
        if (middleName != null && !middleName.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(middleName.trim());
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(firstName.trim());
        }
        return sb.toString();
    }
}
