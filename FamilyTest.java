package edu.ucalgary.oop;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

public class FamilyTest {

    // Helper method for creating test families
    private Family createFamily(int familyId) {
        return new Family(familyId);
    }

    // Helper method for creating test victims
    private DisasterVictim createVictim(String name, String entryDate) {
        return new DisasterVictim(name, entryDate);
    }

    @Test
    public void testFamilyId() {
        Family family = new Family(2001);
        assertEquals("Family ID should be set correctly", 2001, family.getFamilyId());

        family.setFamilyId(2002);
        assertEquals("Family ID should be updated correctly", 2002, family.getFamilyId());
    }

    @Test
    public void testFamilyName() {
        Family family = new Family(2003);
        assertNull("Initially, family name should be null", family.getFamilyName());

        family.setFamilyName("Smith Family");
        assertEquals("Family name should be set correctly", "Smith Family", family.getFamilyName());
    }

    @Test
    public void testAddMember() throws MemberNotFoundException {
        Family family = createFamily(1001);
        DisasterVictim member1 = createVictim("Alice", "2024-03-18");
        family.addMember(member1);

        assertTrue("Member should be added to the family", family.getMembers().contains(member1));
        assertEquals("Member's family reference should be updated", family, member1.getFamily());
    }

    @Test
    public void testAddMultipleMembers() throws MemberNotFoundException {
        Family family = createFamily(1002);
        DisasterVictim member1 = createVictim("Bob", "2024-01-01");
        DisasterVictim member2 = createVictim("Charlie", "2024-05-12");

        family.addMember(member1);
        family.addMember(member2);

        List<DisasterVictim> expectedMembers = Arrays.asList(member1, member2);
        assertEquals("All members should be added correctly", expectedMembers, family.getMembers());
    }

    @Test
    public void testRemoveMember() throws MemberNotFoundException {
        Family family = createFamily(1003);
        DisasterVictim member = createVictim("David", "2024-12-25");

        family.addMember(member);
        family.removeMember(member);

        assertFalse("Member should be removed from the family", family.getMembers().contains(member));
        assertNull("Member's family reference should be cleared", member.getFamily());
    }

    @Test(expected = MemberNotFoundException.class)
    public void testRemoveMemberNotFound() throws MemberNotFoundException {
        Family family = createFamily(1004);
        DisasterVictim nonMember = createVictim("Eve", "2024-09-20");
        family.removeMember(nonMember);
    }

    @Test
    public void testAddFamilyConnection() {
        Family family = new Family(1005);
        FamilyRelation relation = new FamilyRelation(createVictim("Alice", "2024-01-01"), "Sibling", createVictim("Bob", "2024-02-01"));

        family.addFamilyConnection("Alice-Bob", relation);
        assertTrue("Family connection should be added", family.getFamilyConnections().containsKey("Alice-Bob"));
        assertEquals("Family connection should retrieve the correct relation", relation, family.getFamilyConnections().get("Alice-Bob"));
    }

    @Test
    public void testRemoveFamilyConnection() throws RelationNotFoundException {
        Family family = new Family(1006);
        FamilyRelation relation = new FamilyRelation(createVictim("Charlie", "2024-03-01"), "Cousin", createVictim("Dave", "2024-04-01"));

        family.addFamilyConnection("Charlie-Dave", relation);
        family.removeFamilyConnection("Charlie-Dave");

        assertFalse("Family connection should be removed", family.getFamilyConnections().containsKey("Charlie-Dave"));
    }

    @Test(expected = RelationNotFoundException.class)
    public void testRemoveFamilyConnectionNotFound() throws RelationNotFoundException {
        Family family = new Family(1007);
        family.removeFamilyConnection("Nonexistent Connection");
    }


}
