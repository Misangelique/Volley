package main.java.com.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.java.com.example.model.Player;
import main.java.com.example.model.Team;
import main.java.com.example.service.TeamManager;

public class TeamViewController {

    @FXML private TableView<Team> teamsTable;
    @FXML private TableColumn<Team, String> teamNameColumn;
    @FXML private TableColumn<Team, String> leaderNameColumn;
    @FXML private TableColumn<Team, Integer> memberCountColumn;

    @FXML private TableView<Player> playersTable;
    @FXML private TableColumn<Player, String> playerFirstNameColumn;
    @FXML private TableColumn<Player, String> playerLastNameColumn;
    @FXML private TableColumn<Player, String> playerRoleColumn;

    private TeamManager teamManager;

    @FXML
    public void initialize() {
        // Configuration des colonnes de la table des équipes
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        leaderNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTeamLeader().getFirstName() + " " +
                        cellData.getValue().getTeamLeader().getLastName()));
        memberCountColumn.setCellValueFactory(new PropertyValueFactory<>("memberCount"));

        // Configuration des colonnes de la table des joueurs
        playerFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        playerLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        playerRoleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isTeamLeader() ? "Chef d'équipe" : "Membre"));

        // Ajouter un listener pour afficher les membres quand une équipe est sélectionnée
        teamsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        playersTable.setItems(FXCollections.observableArrayList(newSelection.getMembers()));
                    }
                }
        );
    }

    public void setTeamManager(TeamManager teamManager) {
        this.teamManager = teamManager;
        teamsTable.setItems(FXCollections.observableArrayList(teamManager.getTeams()));

        if (!teamsTable.getItems().isEmpty()) {
            teamsTable.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) teamsTable.getScene().getWindow();
        stage.close();
    }
}
