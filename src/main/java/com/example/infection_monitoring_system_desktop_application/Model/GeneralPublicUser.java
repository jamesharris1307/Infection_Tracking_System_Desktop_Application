package com.example.infection_monitoring_system_desktop_application.Model;

import java.time.LocalDate;

public class GeneralPublicUser extends User {

    public GeneralPublicUser(String email, String password, String firstName, String lastName,
                             LocalDate dateOfBirth, String addressLine1, String addressLine2,
                             String townCity, String county, String postcode, AccountStatus accountStatus) {
        super(email, password, firstName, lastName, dateOfBirth,
                addressLine1, addressLine2, townCity, county, postcode, accountStatus);
    }

    @Override
    public Role getRole() {
        return Role.GeneralPublic;
    }
}
