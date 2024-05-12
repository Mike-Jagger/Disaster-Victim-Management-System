package edu.ucalgary.oop;

import java.sql.Date;
import java.time.LocalDateTime;

public class InteractionLog {
    private final int id;
    private final Inquirer inquirer;
    private final Date timestamp;
    private final Date callDate;
    private final String notes;

    // Constructor with all attributes
    public InteractionLog(int id, Inquirer inquirer, Date callDate, String notes) {
        this.id = id;
        this.inquirer = inquirer;
        this.timestamp = callDate;
        this.callDate = callDate;
        this.notes = notes;
    }

    // Convenience constructor
    public InteractionLog(int id, Inquirer inquirer, LocalDateTime timestamp, String notes) {
        this(id, inquirer, Date.valueOf(timestamp.toLocalDate()), notes);
    }


    // Getters for all attributes
    public int getId() {
        return id;
    }

    public Inquirer getInquirer() {
        return inquirer;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Date getCallDate() {
        return callDate;
    }

    public String getNotes() {
        return notes;
    }
}
