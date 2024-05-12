package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DisasterVictimTest {
    private DisasterVictim victim;
    private String EXPECTED_ENTRY_DATE = "2024-01-18";
    private String expectedComments = "Needs medical attention and speaks 2 languages";

    ///correct this
    @Before
    public void setUp() {
        String expectedFirstName = "Freda";
        victim = new DisasterVictim(expectedFirstName, EXPECTED_ENTRY_DATE);
    }

    @Test
    public void testConstructorWithValidEntryDate() {
        String validEntryDate = "2024-01-18";
        DisasterVictim victim = new DisasterVictim("Freda", validEntryDate);
        assertNotNull("Constructor should successfully create an instance with a valid entry date", victim);
        assertEquals("Constructor should set the entry date correctly", validEntryDate, victim.getENTRY_DATE());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithInvalidEntryDateFormat() {
        String invalidEntryDate = "18/01/2024"; // Incorrect format
        new DisasterVictim("Freda", invalidEntryDate);
    }

    @Test
    public void testSetDateOfBirth() {
        String newDateOfBirth = "1987-05-21";
        victim.setDateOfBirth(newDateOfBirth);
        assertEquals("setDateOfBirth should correctly update the date of birth", newDateOfBirth, victim.getDateOfBirth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDateOfBirthWithInvalidFormat() {
        String invalidDate = "15/13/2024";
        victim.setDateOfBirth(invalidDate);
    }

    @Test
    public void testSetAndGetFirstName() {
        String newFirstName = "Alice";
        victim.setFirstName(newFirstName);
        assertEquals("setFirstName should update and getFirstName should return the new first name", newFirstName, victim.getFirstName());
    }

    @Test
    public void testSetAndGetLastName() {
        String newLastName = "Smith";
        victim.setLastName(newLastName);
        assertEquals("setLastName should update and getLastName should return the new last name", newLastName, victim.getLastName());
    }

    @Test
    public void testGetComments() {
        victim.setComments(expectedComments);
        assertEquals("getComments should return the initial correct comments", expectedComments, victim.getComments());
    }

    @Test
    public void testSetComments() {
        victim.setComments(expectedComments);
        String newComments = "Has a minor injury on the left arm";
        victim.setComments(newComments);
        assertEquals("setComments should update the comments correctly", newComments, victim.getComments());
    }

    @Test
    public void testGetEntryDate() {
        assertEquals("getEntryDate should return the expected entry date", EXPECTED_ENTRY_DATE, victim.getENTRY_DATE());
    }

    @Test(expected = InvalidGenderException.class)
    public void testSetInvalidGender() throws InvalidGenderException {
        String invalidGender = "Unspecified"; // Should not be present
        victim.setGender(invalidGender);
    }

    @Test
    public void testGetGender() throws InvalidGenderException {
        String gender = DisasterVictim.validGenderOptions.getFirst(); // Should not be present
        victim.setGender(gender);
        assertEquals("getGender should return the first valid gender option", DisasterVictim.validGenderOptions.getFirst(),
                victim.getGender());
    }

    @Test
    public void testGetAssignedSocialID() {
        // The next victim should have an ID one higher than the previous victim
        DisasterVictim newVictim = new DisasterVictim("Kash", "2024-01-21");
        int expectedSocialId = newVictim.getAssignedSocialID() + 1;
        DisasterVictim actualVictim = new DisasterVictim("Adeleke", "2024-01-22");

        assertEquals("getAssignedSocialID should return the expected social ID", expectedSocialId, actualVictim.getAssignedSocialID());
    }

    @Test
    public void testSetAssignedSocialID() {
        // The next victim should have an ID one higher than the previous victim
        DisasterVictim newVictim = new DisasterVictim("Kash", "2024-01-21");
        newVictim.setASSIGNED_SOCIAL_ID(2);
        int expectedSocialId = 2;

        assertEquals("getAssignedSocialID should return the expected social ID", expectedSocialId, newVictim.getAssignedSocialID());
    }

    @Test
    public void testSetAndGetApproximateAge() {
        victim.setApproximateAge(30);
        assertEquals("setApproximateAge should update and getApproximateAge should return the approximate age", 30, victim.getApproximateAge().intValue());
        assertNull("Setting approximate age should clear any existing dateOfBirth", victim.getDateOfBirth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetApproximateAgeWithNegativeValue() {
        victim.setApproximateAge(-10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetApproximateAgeWithOutOfLimit() {
        victim.setApproximateAge(201);
    }

    @Test
    public void testAddRemoveDietaryRestriction() {
        victim.addDietaryRestriction(DietaryRestriction.VGML);
        victim.addDietaryRestriction(DietaryRestriction.DBML);

        assertTrue("Dietary restrictions should be added correctly", victim.getDietaryRestrictions().containsAll(Arrays.asList(DietaryRestriction.VGML, DietaryRestriction.DBML)));

        victim.removeDietaryRestriction(DietaryRestriction.VGML);
        assertFalse("Dietary restrictions should be removed correctly", victim.getDietaryRestrictions().contains(DietaryRestriction.VGML));
    }

    @Test
    public void testAddFamilyMember() throws MemberNotFoundException {
        // Create a family
        Family family = new Family(1);

        // Create disaster victims
        DisasterVictim victim1 = new DisasterVictim("John", "2024-03-12");
        DisasterVictim victim2 = new DisasterVictim("Sarah", "2024-05-01");

        // Add members to the family
        family.addMember(victim1);
        family.addMember(victim2);

        // Assertions
        assertTrue("Victim 1 should be in the family", family.getMembers().contains(victim1));
        assertEquals("Victim 1's family should be set correctly", family, victim1.getFamily());
        assertTrue("Victim 2 should be in the family", family.getMembers().contains(victim2));
        assertEquals("Victim 2's family should be set correctly", family, victim2.getFamily());
    }

    @Test
    public void testRemoveFamilyMember() throws MemberNotFoundException {
        Family family = new Family(2);
        DisasterVictim victim = new DisasterVictim("Alice", "2024-09-30");

        family.addMember(victim);
        family.removeMember(victim);

        assertFalse("Victim should not be in the family after removal", family.getMembers().contains(victim));
        assertNull("Victim's family reference should be cleared", victim.getFamily());
    }

    @Test
    public void testSetFamily() throws MemberNotFoundException {
        DisasterVictim victim = new DisasterVictim("Bob", "2024-11-23");
        Family oldFamily = new Family(3);
        Family newFamily = new Family(4);

        // Add to old family initially
        oldFamily.addMember(victim);
        oldFamily.removeMember(victim);


        // Set the new family
        newFamily.addMember(victim);
        victim.setFamily(newFamily);


        assertFalse("Victim should no longer be in the old family", oldFamily.getMembers().contains(victim));
        assertTrue("Victim should be in the new family", newFamily.getMembers().contains(victim));
        assertEquals("Victim's family reference should be updated", newFamily, victim.getFamily());
    }

    @Test
    public void testAddPersonalBelongingUpdatesLocationSupplies() {
        Location shelter = new Location("Central Shelter", "123 Main Street");
        Supply sleepingBag = new Supply("Sleeping Bag", 1);
        shelter.addSupply(sleepingBag); // Initial stock

        DisasterVictim victim = new DisasterVictim("Alice", "2024-09-21");
        victim.setLocation(shelter);

        victim.addPersonalBelonging(sleepingBag);

        // Assert supplies at the location are decreased
        int expectedQuantity = 4; // Or calculate this dynamically
        for (Supply supply : shelter.getSupplies()) {
            if (supply.getType().equals("Sleeping Bag")) {
                assertEquals("Shelter should have one less sleeping bag", expectedQuantity, supply.getQuantity());
                break; // Exit the loop once you've found the supply
            }
        }
    }

    @Test
    public void testAddMedicalRecord() {
        Location location = new Location("Local Hospital", "101 Health Blvd");
        MedicalRecord record = new MedicalRecord(location, "Treated for minor injuries", "2024-03-19");
        victim.addMedicalRecord(record);
        assertTrue("Medical records should include the added record", victim.getMedicalRecords().contains(record));
    }

    @Test
    public void testRemoveMedicalRecord() {
        Location location = new Location("Local Clinic", "102 Health Blvd");
        MedicalRecord record = new MedicalRecord(location, "Routine check-up", "2024-03-20");
        victim.addMedicalRecord(record);
        victim.removeMedicalRecord(record);
        assertFalse("Medical records should not include the removed record", victim.getMedicalRecords().contains(record));
    }

    @Test
    public void testGetMedicalRecords() {
        Location location1 = new Location("Emergency Room", "103 Health Blvd");
        Location location2 = new Location("Specialist", "104 Health Blvd");
        MedicalRecord record1 = new MedicalRecord(location1, "Emergency surgery", "2024-03-21");
        MedicalRecord record2 = new MedicalRecord(location2, "Consultation", "2024-03-22");
        victim.addMedicalRecord(record1);
        victim.addMedicalRecord(record2);
        assertTrue("Medical records should contain all added records", victim.getMedicalRecords().containsAll(Arrays.asList(record1, record2)));
    }

    @Test
    public void testAddPersonalBelonging() {
        Supply supply = new Supply("Tent", 1);
        victim.addPersonalBelonging(supply);
        assertTrue("Personal belongings should include the added supply", victim.getPersonalBelongings().contains(supply));
    }

    @Test
    public void testRemovePersonalBelonging() {
        Supply supply = new Supply("Backpack", 1);
        victim.addPersonalBelonging(supply);
        victim.removePersonalBelonging(supply);
        assertFalse("Personal belongings should not include the removed supply", victim.getPersonalBelongings().contains(supply));
    }

    @Test
    public void testGetPersonalBelongings() {
        Supply supply1 = new Supply("Water Bottle", 2);
        Supply supply2 = new Supply("First Aid Kit", 1);
        victim.addPersonalBelonging(supply1);
        victim.addPersonalBelonging(supply2);
        assertTrue("Personal belongings should contain all added supplies", victim.getPersonalBelongings().containsAll(Arrays.asList(supply1, supply2)));
    }
}

