package main.java.com.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.java.com.example.model.Player;
import main.java.com.example.model.Team;
import main.java.com.example.service.TeamManager;

import java.util.Optional;

public class TeamViewController {

    @FXML private TableView<Team> teamsTable;
    @FXML private TableColumn<Team, String> teamNameColumn;
    @FXML private TableColumn<Team, String> leaderNameColumn;
    @FXML private TableColumn<Team, Integer> memberCountColumn;

    @FXML private TableView<Player> playersTable;
    @FXML private TableColumn<Player, String> playerFirstNameColumn;
    @FXML private TableColumn<Player, String> playerLastNameColumn;
    @FXML private TableColumn<Player, String> playerRoleColumn;

    @FXML private Button deleteTeamButton;
    @FXML private Button deletePlayerButton;
    @FXML private Button refreshButton;

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

        // Désactiver les boutons de suppression par défaut
        deleteTeamButton.setDisable(true);
        deletePlayerButton.setDisable(true);

        // Ajouter un listener pour activer/désactiver le bouton de suppression d'équipe
        teamsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    deleteTeamButton.setDisable(newSelection == null);
                    if (newSelection != null) {
                        playersTable.setItems(FXCollections.observableArrayList(newSelection.getMembers()));
                    } else {
                        playersTable.getItems().clear();
                    }
                }
        );

        // Ajouter un listener pour activer/désactiver le bouton de suppression de joueur
        playersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> deletePlayerButton.setDisable(newSelection == null)
        );
    }

    public void setTeamManager(TeamManager teamManager) {
        this.teamManager = teamManager;
        refreshTeamsTable();
    }

    @FXML
    private void handleDeleteTeam() {
        Team selectedTeam = teamsTable.getSelectionModel().getSelectedItem();
        if (selectedTeam != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation de suppression");
            confirmDialog.setHeaderText("Supprimer l'équipe");
            confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer l'équipe " +
                    selectedTeam.getName() + " et tous ses membres ?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                teamManager.removeTeam(selectedTeam.getName());
                refreshTeamsTable();
                playersTable.getItems().clear();
            }
        }
    }

    @FXML
    private void handleDeletePlayer() {
        Player selectedPlayer = playersTable.getSelectionModel().getSelectedItem();
        Team selectedTeam = teamsTable.getSelectionModel().getSelectedItem();

        if (selectedPlayer != null && selectedTeam != null) {
            // Vérifier si c'est le chef d'équipe
            if (selectedPlayer.isTeamLeader()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de suppression");
                alert.setHeaderText(null);
                alert.setContentText("Impossible de supprimer le chef d'équipe. Vous devez supprimer l'équipe entière.");
                alert.showAndWait();
                return;
            }

            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation de suppression");
            confirmDialog.setHeaderText("Supprimer le joueur");
            confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer " +
                    selectedPlayer.getFirstName() + " " + selectedPlayer.getLastName() +
                    " de l'équipe " + selectedTeam.getName() + " ?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                teamManager.removePlayerFromTeam(selectedTeam.getName(), selectedPlayer);
                refreshPlayersTable(selectedTeam);
                refreshTeamsTable();
            }
        }
    }

    @FXML
    private void handleRefresh() {
        refreshTeamsTable();
        Team selectedTeam = teamsTable.getSelectionModel().getSelectedItem();
        if (selectedTeam != null) {
            refreshPlayersTable(selectedTeam);
        } else {
            playersTable.getItems().clear();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actualisation");
        alert.setHeaderText(null);
        alert.setContentText("Les tableaux ont été actualisés.");
        alert.showAndWait();
    }

    private void refreshTeamsTable() {
        teamsTable.setItems(FXCollections.observableArrayList(teamManager.getTeams()));
        if (!teamsTable.getItems().isEmpty()) {
            teamsTable.getSelectionModel().selectFirst();
        }
    }

    private void refreshPlayersTable(Team team) {
        playersTable.setItems(FXCollections.observableArrayList(team.getMembers()));
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) teamsTable.getScene().getWindow();
        stage.close();
    }
}
