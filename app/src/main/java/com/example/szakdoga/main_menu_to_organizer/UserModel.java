package com.example.szakdoga.main_menu_to_organizer;

public class UserModel {
    private String ID;
    private String FirstName;
    private String LastName;
    private String email;
    private boolean organizer;

    public UserModel(String ID, String firstName, String lastName, String email, boolean organizer) {
        this.ID = ID;
        FirstName = firstName;
        LastName = lastName;
        this.email = email;
        this.organizer = organizer;
    }

    public UserModel(String ID, String email) {
        this.ID = ID;
        this.email = email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOrganizer() {
        return organizer;
    }

    public void setOrganizer(boolean organizer) {
        this.organizer = organizer;
    }
}
