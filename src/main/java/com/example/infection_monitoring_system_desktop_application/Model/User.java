package com.example.infection_monitoring_system_desktop_application.Model;

import java.time.LocalDate;

public abstract class User {

    public enum Role {
        GeneralPublic, HealthcareProfessional, Administrator
    }

    public enum AccountStatus {
        Active, Disabled
    }

    private int userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String addressLine1;
    private String addressLine2;
    private String townCity;
    private String county;
    private String postcode;
    private AccountStatus accountStatus;

    public User(String email, String password, String firstName, String lastName,
                LocalDate dateOfBirth, String addressLine1, String addressLine2,
                String townCity, String county, String postcode, AccountStatus accountStatus) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.townCity = townCity;
        this.county = county;
        this.postcode = postcode;
        this.accountStatus = accountStatus;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getTownCity() { return townCity; }
    public void setTownCity(String townCity) { this.townCity = townCity; }

    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }

    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }

    public abstract Role getRole();
}
