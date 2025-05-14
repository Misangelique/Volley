package main.java.com.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.java.com.example.exception.RegistrationException;
import main.java.com.example.model.Team;
import main.java.com.example.service.TeamManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class TeamRegistrationController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField teamNameField;
    @FXML private RadioButton leaderRadio;
    @FXML private RadioButton memberRadio;
    @FXML private ComboBox<Team> teamComboBox;
    @FXML private Label messageLabel;
    @FXML private Button registerButton;
    @FXML private Button viewTeamsButton;

    private TeamManager teamManager;
    private ToggleGroup roleGroup;

    @FXML
    public void initialize() {
        teamManager = new TeamManager();

        // Configuration des boutons radio
        roleGroup = new ToggleGroup();
        leaderRadio.setToggleGroup(roleGroup);
        memberRadio.setToggleGroup(roleGroup);
        leaderRadio.setSelected(true);

        // Mise à jour de l'interface selon le rôle sélectionné
        leaderRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                teamNameField.setDisable(false);
                teamComboBox.setDisable(true);
            }
        });

        memberRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                teamNameField.setDisable(true);
                teamComboBox.setDisable(false);
                updateTeamComboBox();
            }
        });

        // Initialiser la liste des équipes
        updateTeamComboBox();

        // Vérifier si l'inscription est ouverte
        checkRegistrationPeriod();
    }

    private void checkRegistrationPeriod() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = LocalDate.of(2025, 5, 9);
        LocalDate endDate = LocalDate.of(2025, 5, 16);

        if (today.isBefore(startDate) || today.isAfter(endDate)) {
            registerButton.setDisable(true);

            if (today.isBefore(startDate)) {
                messageLabel.setText("Les inscriptions ouvriront le 9 mai 2025");
                messageLabel.getStyleClass().add("error-message");
            } else {
                messageLabel.setText("Les inscriptions sont terminées depuis le 16 mai 2025");
                messageLabel.getStyleClass().add("error-message");
            }
        }
    }

    private void updateTeamComboBox() {
        ObservableList<Team> teams = FXCollections.observableArrayList(teamManager.getTeams());
        teamComboBox.setItems(teams);
        if (!teams.isEmpty()) {
            teamComboBox.setValue(teams.get(0));
        }
    }

    @FXML
    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        boolean isTeamLeader = leaderRadio.isSelected();
        String teamName;

        // Validation des champs
        if (firstName.isEmpty() || lastName.isEmpty()) {
            showErrorAlert("Champs manquants", "Veuillez remplir tous les champs");
            return;
        }

        // Validation du format du nom/prénom
        if (firstName.matches(".*\\d.*") || lastName.matches(".*\\d.*")) {
            showErrorAlert("Format incorrect", "Le nom et le prénom ne peuvent pas contenir de chiffres");
            return;
        }

        if (isTeamLeader) {
            teamName = teamNameField.getText().trim();
            if (teamName.isEmpty()) {
                showErrorAlert("Nom d'équipe manquant", "Veuillez entrer un nom d'équipe");
                return;
            }
        } else {
            if (teamComboBox.getValue() == null) {
                showErrorAlert("Équipe non sélectionnée", "Veuillez sélectionner une équipe");
                return;
            }
            teamName = teamComboBox.getValue().getName();
        }

        // Vérifier les doublons dans la même équipe
        if (teamManager.isNameDuplicateInTeam(firstName, lastName, teamName)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Doublon détecté");
            alert.setHeaderText("Ce nom existe déjà dans l'équipe");
            alert.setContentText("Une personne avec le même nom et prénom existe déjà dans cette équipe. Êtes-vous sûr de vouloir continuer?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK) {
                return;
            }
        }

        // Vérifier les doublons dans d'autres équipes
        if (teamManager.isNameDuplicateInOtherTeams(firstName, lastName, teamName)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Doublon détecté");
            alert.setHeaderText("Ce nom existe déjà dans une autre équipe");
            alert.setContentText("Une personne avec le même nom et prénom existe déjà dans une autre équipe. S'agit-il bien d'une personne différente?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK) {
                return;
            }
        }

        try {
            teamManager.registerPlayer(firstName, lastName, isTeamLeader, teamName);

            showSuccessAlert("Inscription réussie",
                    isTeamLeader ?
                            "Vous avez créé l'équipe " + teamName + " avec succès!" :
                            "Vous avez rejoint l'équipe " + teamName + " avec succès!");

            clearFields();
            updateTeamComboBox();
        } catch (RegistrationException e) {
            showErrorAlert("Erreur d'inscription", e.getMessage());
        }
    }

    @FXML
    private void handleViewTeams() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/team_view.fxml"));
            Parent root = loader.load();

            TeamViewController controller = loader.getController();
            controller.setTeamManager(teamManager);

            Stage stage = new Stage();
            stage.setTitle("Liste des Équipes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible d'afficher la liste des équipes: " + e.getMessage());
        }
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        teamNameField.clear();
        messageLabel.setText("");
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
