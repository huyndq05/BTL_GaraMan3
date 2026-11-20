package model;

import java.util.Date;

public class SalesStaff3 extends Staff3 {
    public SalesStaff3() { super(); }

    public SalesStaff3(int id, String username, String password, Date birthDate, String email,
                       String phoneNumber, float salary, String note, FullName3 fullName, FullAddress3 address) {
        super(id, username, password, birthDate, email, phoneNumber, salary, note, fullName, address);
    }
}
