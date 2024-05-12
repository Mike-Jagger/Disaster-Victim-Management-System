package edu.ucalgary.oop;

public interface EnterInquiry {
    void initiateInquiry();
    void searchVictimsByName(String namePart);
    void logInquiry(String phone, String details);
}