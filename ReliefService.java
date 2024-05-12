package edu.ucalgary.oop;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReliefService {
    private Inquirer inquirer;
    private DisasterVictim missingPerson;
    private LocalDate dateOfInquiry; // Change to LocalDate
    private String infoProvided;
    private Location lastKnownLocation;

    public ReliefService(Inquirer inquirer, DisasterVictim missingPerson, String dateOfInquiry, String infoProvided, Location lastKnownLocation) {
        this.inquirer = inquirer;
        this.missingPerson = missingPerson;
        setDateOfInquiry(dateOfInquiry);
        this.infoProvided = infoProvided;
        this.lastKnownLocation = lastKnownLocation;
    }

    // Getter and setter for inquirer
    public Inquirer getInquirer() {
        return inquirer;
    }

    public void setInquirer(Inquirer inquirer) {
        this.inquirer = inquirer;
    }

    // Getter and setter for missingPerson
    public DisasterVictim getMissingPerson() {
        return missingPerson;
    }

    public void setMissingPerson(DisasterVictim missingPerson) {
        this.missingPerson = missingPerson;
    }

    // Getter and setter for infoProvided
    public String getInfoProvided() {
        return infoProvided;
    }

    public void setInfoProvided(String infoProvided) {
        this.infoProvided = infoProvided;
    }

    // Getter and setter for lastKnownLocation
    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    // Getter and setter for dateOfInquiry
    public String getDateOfInquiry() {
        if (dateOfInquiry == null) {
            throw new IllegalArgumentException("Date of inquiry has not been set.");
        }
        return dateOfInquiry.format(DateTimeFormatter.ISO_DATE);
    }

    public void setDateOfInquiry(String dateOfInquiry) throws IllegalArgumentException {
        if (!isValidDateFormat(dateOfInquiry)) {
            throw new IllegalArgumentException("Invalid date format. Expected format: YYYY-MM-DD");
        }
        this.dateOfInquiry = LocalDate.parse(dateOfInquiry, DateTimeFormatter.ISO_DATE);
    }

    // Helper method to check if a string matches the YYYY-MM-DD date format
    private boolean isValidDateFormat(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // New method getLogDetails
    public String getLogDetails() {
        return "Inquirer: " + inquirer.getFirstName() +
                ", Missing Person: " + missingPerson.getFirstName() +
                ", Date of Inquiry: " + getDateOfInquiry() +  // Using the getter here
                ", Info Provided: " + infoProvided +
                ", Last Known Location: " + lastKnownLocation.getName();
    }
}
