package main.java.com.example.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private Player teamLeader;
    private List<Player> members;
    private static final int MAX_MEMBERS = 6;

    public Team(String name, Player teamLeader) {
        this.name = name;
        this.teamLeader = teamLeader;
        this.members = new ArrayList<>();
        this.members.add(teamLeader); // Le chef d'équipe est aussi un membre
    }

    public boolean addMember(Player player) {
        if (members.size() < MAX_MEMBERS) {
            members.add(player);
            return true;
        }
        return false;
    }

    public boolean isFull() {
        return members.size() >= MAX_MEMBERS;
    }

    public boolean containsPlayer(String firstName, String lastName) {
        return members.stream()
                .anyMatch(p -> p.getFirstName().equalsIgnoreCase(firstName)
                        && p.getLastName().equalsIgnoreCase(lastName));
    }

    // Getters et setters
    public String getName() {
        return name;
    }

    public Player getTeamLeader() {
        return teamLeader;
    }

    public List<Player> getMembers() {
        return members;
    }

    public int getMemberCount() {
        return members.size();
    }

    @Override
    public String toString() {
        return name + " (" + members.size() + "/" + MAX_MEMBERS + " membres)";
    }
}
