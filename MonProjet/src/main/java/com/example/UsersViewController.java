package main.java.com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class UsersViewController {
    @FXML private TableView<String[]> usersTable;
    @FXML private TableColumn<String[], String> nomColumn;
    @FXML private TableColumn<String[], String> prenomColumn;
    @FXML private TableColumn<String[], String> passwordColumn;

    private static final String CSV_FILE = "users.csv";

    @FXML
    public void initialize() {
        // Afficher le répertoire de travail pour le débogage
        System.out.println("Répertoire de travail (UsersView): " + System.getProperty("user.dir"));

        setupTableColumns();
        loadDataFromCsv();
    }

    private void setupTableColumns() {
        // Configurez chaque colonne pour utiliser l'index approprié du tableau
        nomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue()[0]));

        prenomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue()[1]));

        passwordColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue()[2]));
    }

    private void loadDataFromCsv() {
        try {
            // Vérifier si le fichier existe
            File csvFile = new File(CSV_FILE);
            if (!csvFile.exists()) {
                showErrorAlert("Fichier non trouvé", "Le fichier CSV n'existe pas.");
                return;
            }

            List<String> lines = Files.readAllLines(Paths.get(CSV_FILE));
            ObservableList<String[]> data = FXCollections.observableArrayList();

            // Vérifier si le fichier contient des données
            if (lines.size() <= 1) {
                System.out.println("Le fichier CSV ne contient que l'en-tête ou est vide.");
                usersTable.setItems(data); // Définir une liste vide
                return;
            }

            // Sauter la première ligne (en-tête)
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue; // Ignorer les lignes vides

                String[] row = line.split(",");
                if (row.length >= 3) {
                    data.add(row);
                } else {
                    System.err.println("Ligne mal formatée (index " + i + "): " + line);
                    System.err.println("Nombre d'éléments: " + row.length);
                }
            }

            usersTable.setItems(data);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur de chargement",
                    "Impossible de charger les données: " + e.getMessage());
        }
    }

    @FXML
    public void refreshTable() {
        // Effacer les données actuelles
        usersTable.getItems().clear();

        // Recharger les données depuis le fichier CSV
        loadDataFromCsv();

        // Afficher un message de confirmation
        showInfoAlert("Actualisation", "La liste des utilisateurs a été actualisée");
    }

    @FXML
    public void goBack() {
        Stage stage = (Stage) usersTable.getScene().getWindow();
        stage.close();
    }

    // Méthodes utilitaires pour les alertes
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
