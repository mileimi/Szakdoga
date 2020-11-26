package com.example.szakdoga.main_menu_to_organizer;

/**
 * A felhasználók adatainak tárolására szolgál a modell:
 * tárolja az ID-t, teljes nevet, email-t, és hogy szervezői státuszba van-e
 */
public class UserModel {
    private final String ID;
    private final String FirstName;
    private final String LastName;
    private final String email;
    private final boolean organizer;

    public UserModel(String ID, String firstName, String lastName, String email, boolean organizer) {
        this.ID = ID;
        FirstName = firstName;
        LastName = lastName;
        this.email = email;
        this.organizer = organizer;
    }

    public UserModel(String ID,String firstName,String lastName, String email) {
        this.ID = ID;
        this.email = email;
        this.FirstName=firstName;
        this.LastName=lastName;
        this.organizer=false;
    }

    //Getterek, setterek
    public String getID() {
        return ID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getEmail() {
        return email;
    }

}
