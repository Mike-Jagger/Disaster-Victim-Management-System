package edu.ucalgary.oop;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.sql.Date;

public class InteractionLogTest {

    @Test
    public void testConstructorAndGetters() {
        LocalDateTime timestamp = LocalDateTime.now();
        Date callDate = Date.valueOf(timestamp.toLocalDate()); // Convert to java.sql.Date
        String notes = "Initial interaction with the inquirer";
        Inquirer inquirer = generateInquirer();

        InteractionLog logEntry = new InteractionLog(1, inquirer, callDate, notes); // Assume you have a helper method to generate an Inquirer object

        assertEquals("Constructor should set id correctly", 1, logEntry.getId());
        assertEquals("Constructor should set inquirer correctly", inquirer, logEntry.getInquirer());
        assertEquals("Constructor should set callDate correctly", callDate, logEntry.getCallDate());
        assertEquals("Constructor should set notes correctly", notes, logEntry.getNotes());
    }

    // Helper method (if needed)
    private Inquirer generateInquirer() {
        return new Inquirer("Test", "User", "123-456-7890", "Sample Info");
    }
}
