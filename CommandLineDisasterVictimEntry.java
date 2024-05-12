package edu.ucalgary.oop;

import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CommandLineDisasterVictimEntry implements EnterDisasterVictim {
    private final List<DisasterVictim> victims = new ArrayList<>();
    private final List<Location> locations = new ArrayList<>();
    private final List<Family> families = new ArrayList<>();

    private static final String URL = "jdbc:postgresql://localhost/ensf380project";
    private static final String USER = "oop";
    private static final String PASS = "ucalgary";

    public CommandLineDisasterVictimEntry() {
        populateVictims();
        populateFamilies(victims);
        populateLocations(victims, locations);
        populateSupplies(victims);
    }

    public void populateVictims() {
        String query = "SELECT * FROM DISASTER_VICTIM";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                DisasterVictim victim = new DisasterVictim(
                        rs.getString("first_name"), rs.getString("entry_date"));
                victim.setLastName(rs.getString("last_name"));
                Date dob = rs.getDate("date_of_birth");
                int age = rs.getInt("approximate_age");
                if (dob == null) {
                    victim.setApproximateAge(age);
                }
                else {
                    victim.setDateOfBirth(dob.toString());
                }
                victim.setComments(rs.getString("comments"));
                victim.setGender(rs.getString("gender"));
                victim.setASSIGNED_SOCIAL_ID(rs.getInt("victim_id"));
                this.victims.add(victim);
            }
        } catch (SQLException | InvalidGenderException e) {
            e.printStackTrace();
        }
    }

    public void populateFamilies(List<DisasterVictim> victims) {
        Map<Integer, Family> familyMap = new HashMap<>();
        Map<Integer, DisasterVictim> victimMap = new HashMap<>();

        // Create a victim lookup map
        for (DisasterVictim victim : victims) {
            victimMap.put(victim.getAssignedSocialID(), victim);
        }

        // Load Families
        String familyQuery = "SELECT * FROM FAMILY";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(familyQuery)) {
            while (rs.next()) {
                int familyId = rs.getInt("family_id");
                Family family = new Family(familyId);
                family.setFamilyName(rs.getString("family_name"));
                families.add(family);
                familyMap.put(familyId, family);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Load Family Members
        String memberQuery = "SELECT * FROM FAMILY_MEMBER";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(memberQuery)) {
            while (rs.next()) {
                int familyId = rs.getInt("family_id");
                int victimId = rs.getInt("victim_id");
                Family family = familyMap.get(familyId);
                DisasterVictim victim = victimMap.get(victimId);
                if (family != null && victim != null) {
                    family.addMember(victim);
                    victim.setFamily(family);
                }
            }
        } catch (SQLException | MemberNotFoundException e) {
            e.printStackTrace();
        }

        // Load Family Connections
        String connectionQuery = "SELECT fc.connection_id, fc.family_id, fc.victim_one_id, fc.victim_two_id, fc.relationship_type " +
                "FROM FAMILY_CONNECTION fc";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(connectionQuery)) {
            while (rs.next()) {
                int familyId = rs.getInt("family_id");
                Family family = familyMap.get(familyId);
                if (family != null) {
                    DisasterVictim personOne = victimMap.get(rs.getInt("victim_one_id"));
                    DisasterVictim personTwo = victimMap.get(rs.getInt("victim_two_id"));
                    if (personOne != null && personTwo != null) {
                        String connectionId = rs.getString("connection_id");
                        String relationshipType = rs.getString("relationship_type");
                        FamilyRelation relation = new FamilyRelation(personOne, relationshipType, personTwo);
                        family.addFamilyConnection(connectionId, relation);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void populateLocations(List<DisasterVictim> victims, List<Location> locations) {
        Map<Integer, Location> locationMap = new HashMap<>();
        Map<Integer, DisasterVictim> victimMap = new HashMap<>(victims.size());

        // Populate victimMap for quick lookup
        for (DisasterVictim victim : victims) {
            victimMap.put(victim.getAssignedSocialID(), victim);
        }

        String query = "SELECT l.location_id, l.name, l.address, s.supply_id, s.type, ls.quantity, dv.victim_id " +
                "FROM LOCATION l " +
                "LEFT JOIN LOCATION_SUPPLY ls ON l.location_id = ls.location_id " +
                "LEFT JOIN SUPPLY s ON ls.supply_id = s.supply_id " +
                "LEFT JOIN DISASTER_VICTIM dv ON l.location_id = dv.location_id " +
                "ORDER BY l.location_id, s.supply_id, dv.victim_id";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            Location currentLocation = null;

            while (rs.next()) {
                int locationId = rs.getInt("location_id");
                if (!locationMap.containsKey(locationId)) {
                    currentLocation = new Location(rs.getString("name"), rs.getString("address"));
                    locationMap.put(locationId, currentLocation);
                    locations.add(currentLocation);
                } else {
                    currentLocation = locationMap.get(locationId);
                }

                if (rs.getInt("supply_id") != 0) {
                    Supply supply = new Supply(rs.getString("type"), rs.getInt("quantity"));
                    currentLocation.addSupply(supply);
                }

                int victimId = rs.getInt("victim_id");
                if (victimId != 0 && victimMap.containsKey(victimId)) {
                    currentLocation.addOccupant(victimMap.get(victimId));
                    victimMap.get(victimId).setLocation(currentLocation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void populateSupplies(List<DisasterVictim> victims) {
        Map<Integer, DisasterVictim> victimMap = new HashMap<>();

        // Prepare a map of victims by their assigned social ID
        for (DisasterVictim victim : victims) {
            victimMap.put(victim.getAssignedSocialID(), victim);
        }

        String query = "SELECT vs.victim_id, vs.supply_id, vs.quantity, s.type " +
                "FROM VICTIM_SUPPLY vs " +
                "JOIN SUPPLY s ON vs.supply_id = s.supply_id";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int victimId = rs.getInt("victim_id");
                if (victimMap.containsKey(victimId)) {
                    DisasterVictim victim = victimMap.get(victimId);
                    int quantity = rs.getInt("quantity");
                    String type = rs.getString("type");

                    Supply supply = new Supply(type, quantity);
                    victim.addPersonalBelonging(supply); // Assume addPersonalBelonging accepts a Supply object
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void enterDisasterVictim(DisasterVictim disasterVictim) {
        victims.add(disasterVictim);
    }

    public static void runEntrySystem(Scanner scanner) {
        CommandLineDisasterVictimEntry entrySystem = new CommandLineDisasterVictimEntry();
        entrySystem.entryLoop(scanner);
    }

    private void entryLoop(Scanner scanner) {
        CommandLineDisasterVictimEntry entrySystem = new CommandLineDisasterVictimEntry();

        // Main Input Loop
        while (true) {
            System.out.println("Welcome to the Disaster Victim Entry System");
            System.out.println("1. Enter Disaster Victim");
            System.out.println("2. Quit");

            System.out.println();

            int choice = readNumber(scanner);

            System.out.println();

            if (choice == 1) {
                boolean continueEntry = true;
                while (continueEntry) {
                    DisasterVictim victim = entrySystem.collectVictimDetails(scanner);
                    entrySystem.enterDisasterVictim(victim);

                    System.out.println();

                    menuFunction(victim, scanner, entrySystem.families);

                    System.out.println();

                    addMedicalRecordToVictim(victim, entrySystem.locations, scanner);

                    System.out.println();

                    System.out.println("Enter another victim? (yes/no)");
                    continueEntry = scanner.nextLine().equalsIgnoreCase("yes");
                }
            }
            else if (choice == 2) {break;}
        }

        System.out.println("Exiting the system. Thank you for using the Disaster Victim Entry System.");
    }

    private DisasterVictim collectVictimDetails(Scanner scanner) {
        String entryDate;

        System.out.println("Enter First Name:");
        String firstname = scanner.nextLine();

        System.out.println();

        System.out.println("Enter Last Name (optional):");
        String lastName = scanner.nextLine();

        System.out.println();

        while (true) {
            System.out.println("Enter Entry Date (YYYY-MM-DD):");
            entryDate = scanner.nextLine();
            if (isValidDateFormat(entryDate)) {
                break;
            }
            else {
                System.out.println("Invalid date format");
            }
        }

        // Create the object setting first name and entry date
        DisasterVictim victim = new DisasterVictim(firstname, entryDate);

        // Set last name
        victim.setLastName(lastName);

        System.out.println();

        String ageChoice;
        while (true) {
            // Set age or DOB
            System.out.println("Do you want to enter date of birth or approximate age? (dob/age)");
            ageChoice = scanner.nextLine().toLowerCase();

            System.out.println();
            if (ageChoice.equals("dob")) {
                while (true) {
                    System.out.println("Enter Date of Birth (YYYY-MM-DD):");
                    try {
                        victim.setDateOfBirth(scanner.nextLine());
                        break;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            } else if (ageChoice.equals("age")) {
                System.out.println("Provide Approximate Age");

                int approximateAge = boundaryChecker(scanner, 0, 200);

                victim.setApproximateAge(approximateAge);
                break;
            } else {
                System.out.println("Invalid Choice");
            }
        }

        System.out.println();

        // Set Gender
        System.out.println("Select Gender:");
        for (int i = 0; i < DisasterVictim.validGenderOptions.size(); i++) {
            System.out.println((i+1) + ". " + DisasterVictim.validGenderOptions.get(i));
        }

        System.out.println();

        while (true) {
            int genderChoice = boundaryChecker(scanner, 0, DisasterVictim.validGenderOptions.size());

            String gender = DisasterVictim.validGenderOptions.get(genderChoice - 1);

            // Exceptional circumstance where gender is still not part of options
            try {
                victim.setGender(gender);
                break;
            } catch (InvalidGenderException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println();

        // Set dietary restrictions
        System.out.println("Please select your dietary restrictions from the list below (type the number). " +
                "\nType 'done' when finished:");

        // Display all dietary restrictions
        int index = 1;
        for (DietaryRestriction restriction : DietaryRestriction.values()) {
            System.out.println(index + ". " + restriction + " - " + restriction.getDescription());
            index++;
        }

        System.out.println();

        while (true) {
            System.out.print("Add restriction (enter 'done' to quit): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) {
                break;
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice > 0 && choice <= DietaryRestriction.values().length) {
                    DietaryRestriction selectedRestriction = DietaryRestriction.values()[choice - 1];
                    // Check if the restriction is already added
                    if (!victim.getDietaryRestrictions().contains(selectedRestriction)) {
                        victim.addDietaryRestriction(selectedRestriction);
                        System.out.println(selectedRestriction + " added.");
                    } else {
                        System.out.println(selectedRestriction + " is already added.");
                    }
                } else {
                    System.out.println("Invalid choice, please select a number from the list.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a number or 'done' to finish.");
            }
        }

        System.out.println();
        System.out.println("Your dietary restrictions have been recorded: ");
        for (DietaryRestriction restriction : victim.getDietaryRestrictions()) {
            System.out.println(restriction + " - " + restriction.getDescription());
        }

        System.out.println();

        // Set comments
        System.out.println("Enter Comments (optional):");
        String comments = scanner.nextLine();
        victim.setComments(comments);

        System.out.println();

        // Set location
        Location location = getLocationFromUser(scanner);
        victim.setLocation(location);

        return victim;
    }

    // Helper to get location from user
    private Location getLocationFromUser(Scanner scanner) {
        System.out.println("Select a Location:");
        for (int i = 0; i < locations.size(); i++) {
            System.out.println((i + 1) + ". " + locations.get(i).getName());
        }

        System.out.println();

        int locationChoice = boundaryChecker(scanner, 0, locations.size());
        return locations.get(locationChoice - 1);
    }


    // Helper for integer input with additional validation
    public static int readNumber(Scanner reader) {
        while (true) {
            System.out.print("Enter your choice number: ");

            try {
                return Integer.parseInt(reader.nextLine());
            } catch (Exception e) {
                System.out.println("User input was not a number.");
            }
        }
    }

    // Helper for input range validation
    public static int boundaryChecker(Scanner scanner, int min, int max) {
        while (true) {
            int value = readNumber(scanner);
            if (value > min && value <= max) {
                return value;
            }
            else {
                System.out.println("Value must be between " + (min+1) + " and " + max);
            }
        }

    }

    private static boolean isValidDateFormat(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void menuFunction(DisasterVictim victim, Scanner scanner, List<Family> families) {
        System.out.println("Select an option for " + victim.getFirstName() + ":");
        System.out.println("1. Add to an existing family");
        System.out.println("2. Create a new family");
        System.out.println();
        System.out.print("Enter choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline left-over
        System.out.println();
        switch (choice) {
            case 1:
                addToExistingFamily(victim, scanner, families);
                break;
            case 2:
                createNewFamily(victim, scanner, families);
                break;
            default:
                System.out.println("Invalid choice. Please select 1 or 2.");
                menuFunction(victim, scanner, families);  // Recursive call to retry
                break;
        }
    }

    private static void addToExistingFamily(DisasterVictim victim, Scanner scanner, List<Family> families) {
        System.out.println("---- Available Families ----");
        for (int i = 0; i < families.size(); i++) {
            System.out.println((i + 1) + ". " + families.get(i).getFamilyName());
        }
        System.out.println();

        System.out.println("Select the family number to add the victim to");
        int familyIndex = boundaryChecker(scanner, 0, families.size()) - 1;

        System.out.println();

        if (familyIndex >= 0 && familyIndex < families.size()) {
            Family selectedFamily = families.get(familyIndex);
            try {
                selectedFamily.addMember(victim);
                for (DisasterVictim member : selectedFamily.getMembers()) {
                    if (!member.equals(victim)) {
                        System.out.println("What is your relationship to: ");
                        System.out.print(member.getFirstName() + " " + member.getLastName() + ": ");
                        String relationship = scanner.nextLine();
                        String connectionKey = member.getFirstName() + "-" + victim.getFirstName();
                        selectedFamily.addFamilyConnection(connectionKey, new FamilyRelation(member, relationship, victim));
                        String reverseConnectionKey = victim.getFirstName() + "-" + member.getFirstName();
                        selectedFamily.addFamilyConnection(reverseConnectionKey, new FamilyRelation(victim, relationship, member));
                    }
                }
            } catch (MemberNotFoundException e) {
                System.err.println("Error adding member: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid family selection.");
        }
    }

    private static void createNewFamily(DisasterVictim victim, Scanner scanner, List<Family> families) {
        System.out.print("Enter a name for the new family: ");
        String familyName = scanner.nextLine();
        Family newFamily = new Family(families.size() + 1);
        newFamily.setFamilyName(familyName);
        try {
            newFamily.addMember(victim);
            families.add(newFamily);
            System.out.println("New family created and victim added.");
        } catch (MemberNotFoundException e) {
            System.err.println("Error adding member: " + e.getMessage());
        }
    }

    public static void addMedicalRecordToVictim(DisasterVictim victim, List<Location> locations, Scanner scanner) {
        System.out.println("Adding a new medical record for: " + victim.getFirstName() + " " + victim.getLastName());

        // Display available locations
        System.out.println("---- List of Available Locations ----");
        for (int i = 0; i < locations.size(); i++) {
            System.out.println((i + 1) + ": " + locations.get(i).getName() + " - " + locations.get(i).getAddress());
        }

        System.out.println();

        System.out.println("Choose a location by number");
        int locationIndex = boundaryChecker(scanner, 0, locations.size()) - 1;

        System.out.println();

        if (locationIndex < 0 || locationIndex >= locations.size()) {
            System.out.println("Invalid location selection.");
            return;
        }

        Location selectedLocation = locations.get(locationIndex);

        System.out.print("Enter treatment details: ");
        String treatmentDetails = scanner.nextLine();

        String dateOfTreatment;

        while (true) {
            System.out.print("Enter date of treatment (YYYY-MM-DD): ");
            dateOfTreatment = scanner.nextLine();
            if (isValidDateFormat(dateOfTreatment)) {
                break;
            }
            else {
                System.out.println("Invalid date format");
            }
        }

        try {
            MedicalRecord newRecord = new MedicalRecord(selectedLocation, treatmentDetails, dateOfTreatment);
            victim.addMedicalRecord(newRecord);
            System.out.println("Medical record added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding medical record: " + e.getMessage());
            System.out.println("Please try again.");
            addMedicalRecordToVictim(victim, locations, scanner); // Optionally retry
        }
    }


}
