package edu.ucalgary.oop;

import java.util.ArrayList;

public class Location {
    private String name;
    private String address;
    private ArrayList<DisasterVictim> occupants = new ArrayList<>();
    private ArrayList<Supply> supplies = new ArrayList<>();

    // Constructor
    public Location(String name, String address) {
        this.name = name;
        this.address = address;
    }// Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<DisasterVictim> getOccupants() {
        return new ArrayList<>(occupants); // Return a copy
    }

    public void setOccupants(ArrayList<DisasterVictim> occupants) {
        this.occupants = new ArrayList<>(occupants); // Maintain encapsulation
    }

    public ArrayList<Supply> getSupplies() {
        return new ArrayList<>(supplies);  // Return a copy
    }

    public void setSupplies(ArrayList<Supply> supplies) {
        this.supplies = new ArrayList<>(supplies); // Maintain encapsulation
    }

    // Methods for managing occupants
    public void addOccupant(DisasterVictim occupant) {
        occupants.add(occupant);
    }

    public void removeOccupant(DisasterVictim occupant) {
        occupants.remove(occupant);
    }

    // Methods for managing supplies

    public void addSupply(String type, int quantity) {
        supplies.add(new Supply(type, quantity));
    }

    public void addSupply(Supply supply) {
        supplies.add(supply);
    }

    public void removeSupply(String type, int quantity) {
        for (Supply supply : supplies) {
            if (supply.getType().equals(type) && supply.getQuantity() >= quantity) {
                supply.setQuantity(supply.getQuantity() - quantity);
                if (supply.getQuantity() == 0) {
                    supplies.remove(supply);
                }
                return; // Supply removed
            }
        }
    }

    public boolean hasSupply(String type, int quantity) {
        for (Supply supply : supplies) {
            if (supply.getType().equals(type) && supply.getQuantity() >= quantity) {
                return true;
            }
        }
        return false;
    }
}
