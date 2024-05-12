package edu.ucalgary.oop;

public class FamilyRelation {
    private DisasterVictim personOne;
    private String relationshipTo; // Describes the relationship to personTwo
    private DisasterVictim personTwo;

    // Constructor
    public FamilyRelation(DisasterVictim personOne, String relationshipTo, DisasterVictim personTwo) {
        this.personOne = personOne;
        this.relationshipTo = relationshipTo;
        this.personTwo = personTwo;
    }

    // Getters
    public DisasterVictim getPersonOne() {
        return personOne;
    }

    public String getRelationshipTo() {
        return relationshipTo;
    }

    public DisasterVictim getPersonTwo() {
        return personTwo;
    }

    // Setters (optional, consider if you want to make relationships mutable)
    public void setPersonOne(DisasterVictim personOne) {
        this.personOne = personOne;
    }

    public void setRelationshipTo(String relationshipTo) {
        this.relationshipTo = relationshipTo;
    }

    public void setPersonTwo(DisasterVictim personTwo) {
        this.personTwo = personTwo;
    }

    public DisasterVictim getOtherPerson(DisasterVictim person) {
        if (personOne.equals(person)) {
            return personTwo;
        } else if (personTwo.equals(person)) {
            return personOne;
        } else {
            throw new IllegalArgumentException("Person is not part of this relationship");
        }
    }
}
