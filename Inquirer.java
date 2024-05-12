package edu.ucalgary.oop;

public class Inquirer {

    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String info;

    public Inquirer(String firstName, String lastName, String phoneNumber, String info) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.info = info;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getInfo() {
        return info;
    }
}
