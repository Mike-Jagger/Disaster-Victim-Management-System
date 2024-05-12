package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class CommandLineDisasterVictimInquiry implements EnterInquiry {
    private Connection conn;
    private Scanner scanner;

    public CommandLineDisasterVictimInquiry() {
        scanner = new Scanner(System.in);
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/ensf380project", "oop", "ucalgary");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initiateInquiry() {
        System.out.print("Enter part of the name to search for victims: ");
        String namePart = scanner.nextLine();
        System.out.println();
        searchVictimsByName(namePart);
    }

    @Override
    public void searchVictimsByName(String namePart) {
        String query = "SELECT * FROM DISASTER_VICTIM WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + namePart.toLowerCase() + "%");
            pstmt.setString(2, "%" + namePart.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();
            int counter = 0;
            while (rs.next()) {
                counter++;
                System.out.println("--------- " + "Person " + counter + " ---------");
                System.out.println("ID: " + rs.getInt("victim_id") +
                                    "\nName: " + rs.getString("first_name") + ", " + rs.getString("last_name") +
                                    "\nEntry Date: " + rs.getDate("entry_date")  +
                                    "\nComment: " + rs.getString("comments") +
                                    "\nGender: " + rs.getString("gender")
                );
                // More fields can be printed as needed
            }

            System.out.println();

            if (!rs.isBeforeFirst()) {
                System.out.println("No additional results found.");
            }
        } catch (SQLException e) {
            System.out.println("Error searching victims: " + e.getMessage());
        }
    }

    @Override
    public void logInquiry(String phone, String details) {
        // SQL query to find the maximum ID currently in INQUIRY_LOG and add 1 to it
        String getMaxIdQuery = "SELECT MAX(id) + 1 AS next_id FROM INQUIRY_LOG";
        // SQL query to insert a new log entry
        String logInquiryQuery = "INSERT INTO INQUIRY_LOG (id, inquirer, callDate, details) VALUES (?, ?, ?, ?)";

        try {
            int nextId = 1; // Default to 1 if table is empty
            int inquirerId = fetchInquirerId(phone);
            if (inquirerId == -1) {
                System.out.println("Inquirer not found in the database.");
                return;
            }

            // Execute the getMaxIdQuery to find the next ID value
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(getMaxIdQuery)) {
                if (rs.next()) {
                    nextId = rs.getInt("next_id");
                    if (rs.wasNull()) {  // Check if the MAX(id) was NULL, which occurs if the table is empty
                        nextId = 1;
                    }
                }
            }

            // Now perform the insert with the calculated next ID
            try (PreparedStatement pstmt = conn.prepareStatement(logInquiryQuery)) {
                pstmt.setInt(1, nextId);
                pstmt.setInt(2, inquirerId);
                pstmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                pstmt.setString(4, details);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Inquiry logged with ID: " + nextId);
                } else {
                    System.out.println("Failed to log inquiry.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error logging inquiry: " + e.getMessage());
        }

        System.out.println();
    }

    private int fetchInquirerId(String phoneNumber) throws SQLException {
        String query = "SELECT id FROM INQUIRER WHERE phoneNumber = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;  // Return -1 if inquirer is not found
    }

    public static void runInquirySystem(Scanner scanner) {
        CommandLineDisasterVictimInquiry inquirySystem = new CommandLineDisasterVictimInquiry();
        inquirySystem.inquiryLoop(scanner);
    }

    private void inquiryLoop(Scanner scanner) {
        CommandLineDisasterVictimInquiry inquirySystem = new CommandLineDisasterVictimInquiry();
        boolean systemOn = true;
        while (systemOn) {
            System.out.println("Welcome to the Disaster Victim Inquiry System");
            System.out.println("1. Start a new inquiry");
            System.out.println("2. Exit");
            System.out.println();
            System.out.print("Please choose an option: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }
            System.out.println();

            switch (choice) {
                case 1:
                    inquirySystem.initiateInquiry();
                    handleInquiryResults(inquirySystem, scanner);
                    break;
                case 2:
                    System.out.println("Exiting the system. Thank you for using the Disaster Victim Inquiry System.");
                    systemOn = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1 or 2.");
            }
        }
    }

    private static void handleInquiryResults(CommandLineDisasterVictimInquiry inquirySystem, Scanner scanner) {
        System.out.println("Do you want to log this inquiry? (yes/no)");
        String logChoice = scanner.nextLine();
        if (logChoice.equalsIgnoreCase("yes")) {
            System.out.print("To confirm you are an inquirer, please enter the phone number you registered with (xxx-xxx-xxxx): ");
            String inquirerId = scanner.nextLine().trim();
            System.out.print("Enter inquiry details: ");
            String details = scanner.nextLine();

            // Mock objects for example purposes; you'd normally fetch these or create more detailed implementations
            Inquirer inquirer = new Inquirer("John", "Doe", "123-456-7890", "Seeking information");

            inquirySystem.logInquiry(inquirerId, details);
        }
    }
}

