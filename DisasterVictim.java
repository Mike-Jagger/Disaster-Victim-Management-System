package edu.ucalgary.oop;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet; // For using a Set

public class DisasterVictim {
    private static int counter = 0;
    private static boolean gendersLoaded = false;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private Integer approximateAge;
    private String ENTRY_DATE;
    private String comments;
    private Location location;
    private int ASSIGNED_SOCIAL_ID;
    private Family family;
    private ArrayList<MedicalRecord> medicalRecords;
    private ArrayList<Supply> personalBelongings;
    private String gender;
    public static List<String> validGenderOptions = new ArrayList<>();
    private static final String DATA_FOLDER = "data"; // Data folder path
    private static final String GENDER_OPTIONS_FILE = "GenderOptions.txt"; // Gender options file

    // Initialize the set
    private Set<DietaryRestriction> dietaryRestrictions;

    // Constructor
    public DisasterVictim(String firstName, String ENTRY_DATE) throws IllegalArgumentException {
        this.firstName = firstName;
        if (!isValidDateFormat(ENTRY_DATE)) {
            throw new IllegalArgumentException("Invalid date format for entry date. Expected format: YYYY-MM-DD");
        }
        this.ENTRY_DATE = ENTRY_DATE;
        medicalRecords = new ArrayList<>();
        personalBelongings = new ArrayList<>();
        if (!gendersLoaded) {
            loadGenderOptions();
            gendersLoaded = true;
        }
        ASSIGNED_SOCIAL_ID = generateSocialID();
        dietaryRestrictions = new HashSet<>();
    }

    // Getters and Setters
    // Firstname
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // lastName
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Mutable Attributes
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // approximateAge
    public Integer getApproximateAge() {
        return approximateAge;
    }

    public void setApproximateAge(Integer approximateAge) {
        if (approximateAge != null && (approximateAge < 0 || approximateAge > 200)) {
            throw new IllegalArgumentException("Approximate age cannot be negative nor greater than 200");
        }
        this.approximateAge = approximateAge;

        // Logic to clear dateOfBirth if age is set. Consider using a helper method.
        if (approximateAge != null) {
            clearDateOfBirth(); // Helper method
        }
    }

    // dateOfBirth
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        if (isValidDateFormat(dateOfBirth)) { // Assuming you have validation logic
            this.dateOfBirth = dateOfBirth;

            // Logic to clear approximateAge if dateOfBirth is set
            if (dateOfBirth != null) {
                clearApproximateAge();
            }
        } else {
            throw new IllegalArgumentException("Invalid date of birth format");
        }
    }

    // Helper Methods for managing age and DOB
    private void clearDateOfBirth() {
        dateOfBirth = null;
    }

    private void clearApproximateAge() {
        approximateAge = null;
    }

    // Immutable Attributes
    public String getENTRY_DATE() {
        return ENTRY_DATE;
    }

    public int getAssignedSocialID() { // Assuming social ID is assigned once
        return ASSIGNED_SOCIAL_ID;
    }
    public void setASSIGNED_SOCIAL_ID (int ID) { this.ASSIGNED_SOCIAL_ID = ID; }

    // Methods for managing relationships
    public void setFamily(Family family) throws MemberNotFoundException {
        this.family = family;
    }

    public Family getFamily() {
        return family;
    }

    // Methods for managing medical records
    public void addMedicalRecord(MedicalRecord record) {
        medicalRecords.add(record);
    }

    public void removeMedicalRecord(MedicalRecord record) {
        medicalRecords.remove(record);
    }

    public ArrayList<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    // Methods for managing belongings
    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    // Modify addPersonalBelonging to interact with Location
    public void addPersonalBelonging(Supply supply) {
        personalBelongings.add(supply);

        if (location != null) {
            location.removeSupply(supply.getType(), supply.getQuantity()); // Update the Location's supplies
        }
    }

    public void removePersonalBelonging(Supply supply) {
        personalBelongings.remove(supply);
    }

    public ArrayList<Supply> getPersonalBelongings() {
        return personalBelongings;
    }

    // Methods for gender management
    public void setGender(String gender) throws InvalidGenderException {
        if (!validGenderOptions.contains(gender)) {
            throw new InvalidGenderException("Invalid gender option");
        }
        this.gender = gender;
    }

    public String getGender() {
        return this.gender;
    }

    private void loadGenderOptions() {
        // Use Paths.get() to handle file separators correctly
        String filePath = Paths.get(DATA_FOLDER, GENDER_OPTIONS_FILE).toString();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                validGenderOptions.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading gender options file: " + e.getMessage());
        }
    }

    // Methods for dietary restrictions
    public void addDietaryRestriction(DietaryRestriction restriction) {
        dietaryRestrictions.add(restriction);
    }

    public void removeDietaryRestriction(DietaryRestriction restriction) {
        dietaryRestrictions.remove(restriction);
    }

    public Set<DietaryRestriction> getDietaryRestrictions() {
        return dietaryRestrictions;
    }


    // Helper functions
    private boolean isValidDateFormat(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Database generates IDs automatically
    private static int generateSocialID() {
        counter++;
        return counter;
    }
}
