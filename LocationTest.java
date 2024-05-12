package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class LocationTest {
    private Location location;
    private DisasterVictim victim;
    private Supply supply;

    @Before
    public void setUp() {
        location = new Location("Shelter A", "1234 Shelter Ave");
        victim = new DisasterVictim("John Doe", "2024-01-01");
        supply = new Supply("Water Bottle", 10);
    }

    @Test
    public void testConstructor() {
        assertNotNull("Constructor should create a non-null Location object", location);
        assertEquals("Constructor should set the name correctly", "Shelter A", location.getName());
        assertEquals("Constructor should set the address correctly", "1234 Shelter Ave", location.getAddress());
    }

    @Test
    public void testSetName() {
        String newName = "Shelter B";
        location.setName(newName);
        assertEquals("setName should update the name of the location", newName, location.getName());
    }

    @Test
    public void testSetAddress() {
        String newAddress = "4321 Shelter Blvd";
        location.setAddress(newAddress);
        assertEquals("setAddress should update the address of the location", newAddress, location.getAddress());
    }

    @Test
    public void testAddOccupant() {
        location.addOccupant(victim);
        assertTrue("addOccupant should add a disaster victim to the occupants list", location.getOccupants().contains(victim));
    }

    @Test
    public void testRemoveOccupant() {
        location.addOccupant(victim); // First, add the victim
        location.removeOccupant(victim);
        assertFalse("removeOccupant should remove the disaster victim from the occupants list", location.getOccupants().contains(victim));
    }

    @Test
    public void testSetAndGetSupplies() {
        ArrayList<Supply> newSupplies = new ArrayList<>();
        newSupplies.add(supply);
        newSupplies.add(new Supply("Blanket", 5));

        location.setSupplies(newSupplies);
        assertTrue("setSupplies should replace the supplies list", location.getSupplies().containsAll(newSupplies));
    }

    @Test
    public void testAddSupplyByTypeAndQuantity() {
        location.addSupply(supply.getType(), supply.getQuantity());
        assertTrue("addSupply should add a supply to the supplies list", location.hasSupply(supply.getType(), supply.getQuantity()));
    }

    @Test
    public void testRemoveSupplyByTypeAndQuantity() {
        location.addSupply(supply.getType(), supply.getQuantity());
        location.removeSupply(supply.getType(), 1);
        assertTrue("Supplies should be correctly removed", location.hasSupply(supply.getType(), 5));
    }

    @Test
    public void testHasSupply() {
        location.addSupply(supply.getType(), supply.getQuantity());
        assertTrue("hasSupply should return true if the supply exists", location.hasSupply(supply.getType(), supply.getQuantity()));
        assertFalse("hasSupply should return false if the supply doesn't exist", location.hasSupply("Sleeping Bag", 2));
    }
}
