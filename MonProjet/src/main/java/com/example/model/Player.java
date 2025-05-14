package main.java.com.example.model;

public class Player {
    private String firstName;
    private String lastName;
    private boolean isTeamLeader;
    private String teamName;

    public Player(String firstName, String lastName, boolean isTeamLeader, String teamName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isTeamLeader = isTeamLeader;
        this.teamName = teamName;
    }

    // Getters et setters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isTeamLeader() {
        return isTeamLeader;
    }

    public String getTeamName() {
        return teamName;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + (isTeamLeader ? " (Chef)" : "");
    }
}
