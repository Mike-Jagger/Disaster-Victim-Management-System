package edu.ucalgary.oop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Family {
    private int familyId; // Unique identifier
    private String familyName;
    private List<DisasterVictim> members;
    private HashMap<String, FamilyRelation> familyConnections = new HashMap<>();

    // Constructor
    public Family(int familyId) {
        this.familyId = familyId;
        this.members = new ArrayList<>();
    }

    // Getters and Setters
    public int getFamilyId() {
        return familyId;
    }
    public void setFamilyId(int familyId) { this.familyId = familyId; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }


    public List<DisasterVictim> getMembers() {
        return members;
    }

    // Methods for member management
    public void addMember(DisasterVictim member) throws MemberNotFoundException {
        members.add(member);
        member.setFamily(this); // Update the DisasterVictim's family reference
    }

    public void removeMember(DisasterVictim member) throws MemberNotFoundException {
        if (!members.remove(member)) {
            throw new MemberNotFoundException("Member not found in family");
        }
        member.setFamily(null); // Clear the family reference in the DisasterVictim
    }

    // Methods for family relations management
    public void addFamilyConnection(String key, FamilyRelation record) {
        familyConnections.put(key, record);
    }

    public void removeFamilyConnection(String exRelation) throws RelationNotFoundException{
        if(familyConnections.remove(exRelation) == null) {
            throw new RelationNotFoundException("Relationship not found for family member: " +
                    exRelation);
        };
    }

    public Map<String, FamilyRelation> getFamilyConnections() {
        return new HashMap<>(familyConnections);
    }
}
