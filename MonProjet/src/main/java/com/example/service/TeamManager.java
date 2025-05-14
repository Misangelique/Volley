package main.java.com.example.service;

import main.java.com.example.model.Player;
import main.java.com.example.model.Team;
import main.java.com.example.exception.RegistrationException;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamManager {
    private static final int MAX_TEAMS = 8;
    private static final String TEAMS_FILE = "teams.csv";
    private List<Team> teams;

    public TeamManager() {
        teams = new ArrayList<>();
        loadTeams();
    }

    public void registerPlayer(String firstName, String lastName, boolean isTeamLeader,
                               String teamName) throws RegistrationException {
        // Vérifier la période d'inscription
        LocalDate today = LocalDate.now();
        LocalDate startDate = LocalDate.of(2025, 5, 9);
        LocalDate endDate = LocalDate.of(2025, 5, 16);

        if (today.isBefore(startDate)) {
            throw new RegistrationException("Les inscriptions ne sont pas encore ouvertes. Elles débuteront le 9 mai 2025.");
        }

        if (today.isAfter(endDate)) {
            throw new RegistrationException("Les inscriptions sont terminées depuis le 16 mai 2025.");
        }

        // Vérifier le format du nom et prénom
        if (firstName.matches(".*\\d.*") || lastName.matches(".*\\d.*")) {
            throw new RegistrationException("Le nom et le prénom ne peuvent pas contenir de chiffres.");
        }

        if (firstName.isEmpty() || lastName.isEmpty()) {
            throw new RegistrationException("Le nom et le prénom ne peuvent pas être vides.");
        }

        Player player = new Player(firstName, lastName, isTeamLeader, teamName);

        if (isTeamLeader) {
            // Vérifier si le nombre maximum d'équipes est atteint
            if (teams.size() >= MAX_TEAMS) {
                throw new RegistrationException("Le nombre maximum d'équipes (" + MAX_TEAMS + ") est atteint.");
            }

            // Vérifier si le nom d'équipe existe déjà
            if (teams.stream().anyMatch(t -> t.getName().equalsIgnoreCase(teamName))) {
                throw new RegistrationException("Une équipe avec le nom '" + teamName + "' existe déjà.");
            }

            Team team = new Team(teamName, player);
            teams.add(team);
        } else {
            // Trouver l'équipe par son nom
            Optional<Team> teamOpt = teams.stream()
                    .filter(t -> t.getName().equalsIgnoreCase(teamName))
                    .findFirst();

            if (!teamOpt.isPresent()) {
                throw new RegistrationException("L'équipe '" + teamName + "' n'existe pas.");
            }

            Team team = teamOpt.get();

            // Vérifier si l'équipe est pleine
            if (team.isFull()) {
                throw new RegistrationException("L'équipe '" + teamName + "' est complète (6 membres maximum).");
            }

            team.addMember(player);
        }

        saveTeams();
    }

    public List<Team> getTeams() {
        return teams;
    }

    public boolean isNameDuplicateInTeam(String firstName, String lastName, String teamName) {
        Optional<Team> teamOpt = teams.stream()
                .filter(t -> t.getName().equalsIgnoreCase(teamName))
                .findFirst();

        return teamOpt.isPresent() && teamOpt.get().containsPlayer(firstName, lastName);
    }

    public boolean isNameDuplicateInOtherTeams(String firstName, String lastName, String teamName) {
        return teams.stream()
                .filter(t -> !t.getName().equalsIgnoreCase(teamName))
                .anyMatch(t -> t.containsPlayer(firstName, lastName));
    }

    private void saveTeams() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TEAMS_FILE))) {
            writer.println("TeamName,MemberFirstName,MemberLastName,IsTeamLeader");

            for (Team team : teams) {
                for (Player player : team.getMembers()) {
                    writer.println(String.format("%s,%s,%s,%b",
                            team.getName(),
                            player.getFirstName(),
                            player.getLastName(),
                            player.isTeamLeader()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTeams() {
        File file = new File(TEAMS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Ignorer l'en-tête
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String teamName = parts[0];
                    String firstName = parts[1];
                    String lastName = parts[2];
                    boolean isTeamLeader = Boolean.parseBoolean(parts[3]);

                    Player player = new Player(firstName, lastName, isTeamLeader, teamName);

                    // Vérifier si l'équipe existe déjà
                    Optional<Team> existingTeam = teams.stream()
                            .filter(t -> t.getName().equals(teamName))
                            .findFirst();

                    if (existingTeam.isPresent()) {
                        if (!isTeamLeader) {
                            existingTeam.get().addMember(player);
                        }
                    } else {
                        if (isTeamLeader) {
                            Team team = new Team(teamName, player);
                            teams.add(team);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
