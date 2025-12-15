package com.example.infection_monitoring_system_desktop_application.Unit.Model;

import com.example.infection_monitoring_system_desktop_application.Model.GeneralPublicUser;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

class GeneralPublicUserTest {

    @Test void testUserCreationGetters() {
        LocalDate dob = LocalDate.of(2002, 2, 2);
        GeneralPublicUser user = new GeneralPublicUser(
                "generalpublicuser@test.com",
                "password123",
                "testFname",
                "testLname",
                dob,
                "addressTestLine1",
                "AddressTestLine2",
                "TestTownCity",
                "TestCounty",
                "TE271ST",
                User.AccountStatus.Active
        );

        assertEquals("generalpublicuser@test.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("testFname", user.getFirstName());
        assertEquals("testLname", user.getLastName());
        assertEquals(dob, user.getDateOfBirth());
        assertEquals("addressTestLine1", user.getAddressLine1());
        assertEquals("AddressTestLine2", user.getAddressLine2());
        assertEquals("TestTownCity", user.getTownCity());
        assertEquals("TestCounty", user.getCounty());
        assertEquals("TE271ST", user.getPostcode());
        assertEquals(User.AccountStatus.Active, user.getAccountStatus());
    }

    @Test void testSetter() {
        GeneralPublicUser user = new GeneralPublicUser(
                "temp@email.com", "tempPass", "TempF", "TempL",
                LocalDate.of(2001, 1, 1),
                "TempAddressL1", "TempAddressL2", "TempTownCity", "TempCounty", "TE37 3MP",
                User.AccountStatus.Disabled
        );

        user.setEmail("new@email.com");
        user.setFirstName("newFName");
        user.setLastName("newLName");
        user.setDateOfBirth(LocalDate.of(2002, 2, 2));
        user.setAddressLine1("newAddressL1");
        user.setAddressLine2("newAddressL2");
        user.setTownCity("newTownCity");
        user.setCounty("newCounty");
        user.setPostcode("NE443EW");
        user.setAccountStatus(User.AccountStatus.Active);

        assertEquals("new@email.com", user.getEmail());
        assertEquals("newFName", user.getFirstName());
        assertEquals("newLName", user.getLastName());
        assertEquals(LocalDate.of(2002, 2, 2), user.getDateOfBirth());
        assertEquals("newAddressL1", user.getAddressLine1());
        assertEquals("newAddressL2", user.getAddressLine2());
        assertEquals("newTownCity", user.getTownCity());
        assertEquals("newCounty", user.getCounty());
        assertEquals("NE443EW", user.getPostcode());
        assertEquals(User.AccountStatus.Active, user.getAccountStatus());
    }
}