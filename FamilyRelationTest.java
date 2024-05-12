package edu.ucalgary.oop;

import org.junit.Test;
import static org.junit.Assert.*;

public class FamilyRelationTest {

    private DisasterVictim personOne = new DisasterVictim("John Dalan", "2024-01-19");
    private DisasterVictim personTwo = new DisasterVictim("Jane Dalan", "2024-02-20");
    private String relationshipTo = "sibling";

    @Test
    public void testObjectCreation() {
        FamilyRelation relation = new FamilyRelation(personOne, relationshipTo, personTwo);
        assertNotNull(relation);
    }

    @Test
    public void testGetPersonOne() {
        FamilyRelation relation = new FamilyRelation(personOne, relationshipTo, personTwo);
        assertEquals("getPersonOne should return the correct person", personOne, relation.getPersonOne());
    }

    @Test
    public void testGetPersonTwo() {
        FamilyRelation relation = new FamilyRelation(personOne, relationshipTo, personTwo);
        assertEquals("getPersonTwo should return the correct person", personTwo, relation.getPersonTwo());
    }

    @Test
    public void testGetRelationshipTo() {
        FamilyRelation relation = new FamilyRelation(personOne, relationshipTo, personTwo);
        assertEquals("getRelationshipTo should return the correct relationship", relationshipTo, relation.getRelationshipTo());
    }


    @Test
    public void testGetOtherPerson() {
        FamilyRelation relation = new FamilyRelation(personOne, relationshipTo, personTwo);

        assertEquals("getOtherPerson should return the correct other person (from personOne's perspective)", personTwo, relation.getOtherPerson(personOne));
        assertEquals("getOtherPerson should return the correct other person (from personTwo's perspective)", personOne, relation.getOtherPerson(personTwo));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOtherPersonWithInvalidPerson() {
        FamilyRelation relation = new FamilyRelation(personOne, relationshipTo, personTwo);
        DisasterVictim unrelatedPerson = new DisasterVictim("Unrelated Person", "2024-02-25");
        relation.getOtherPerson(unrelatedPerson);
    }

}
