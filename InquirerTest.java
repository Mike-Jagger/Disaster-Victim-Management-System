package edu.ucalgary.oop;

import org.junit.*;
import static org.junit.Assert.*;
import java.time.LocalDateTime; // Assuming you might want timestamps for InteractionLog
import java.util.List;

public class InquirerTest {

    private String expectedFirstName = "Joseph";
    private String expectedLastName = "Bouillon";
    private String expectedPhoneNumber = "+1-123-456-7890";
    private String expectedMessage = "looking for my family members";
    private Inquirer inquirer;

    @Before
    public void setUp() {
        inquirer = new Inquirer(expectedFirstName, expectedLastName, expectedPhoneNumber, expectedMessage);
    }

    @Test
    public void testObjectCreation() {
        assertNotNull(inquirer);
    }

    @Test
    public void testGetFirstName() {
        assertEquals("getFirstName() should return inquirer's first name", expectedFirstName, inquirer.getFirstName());
    }

    @Test
    public void testGetLastName() {
        assertEquals("getLastName() should return inquirer's last name", expectedLastName, inquirer.getLastName());
    }

    @Test
    public void testGetPhoneNumber() { // Updated method name
        assertEquals("getPhoneNumber() should return the correct Phone Number", expectedPhoneNumber, inquirer.getPhoneNumber());
    }

    @Test
    public void testGetInfo() {
        assertEquals("getInfo() should return the inquirer message", expectedMessage, inquirer.getInfo());
    }

    // ... (Tests for interaction logs would likely be in a separate InteractionLogTest class)

}
