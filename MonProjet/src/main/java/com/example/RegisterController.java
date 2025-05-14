package main.java.com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;

public class RegisterController {
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private static final String CSV_FILE = "users.csv";
    private static final String CSV_HEADER = "Nom,Prenom,MotDePasse";

    @FXML
    public void initialize() {
        // Afficher le répertoire de travail pour le débogage
        System.out.println("Répertoire de travail : " + System.getProperty("user.dir"));

        // Vérifier si le fichier CSV existe, sinon le créer avec l'en-tête
        try {
            File csvFile = new File(CSV_FILE);
            if (!csvFile.exists()) {
                System.out.println("Création du fichier CSV : " + csvFile.getAbsolutePath());
                try (FileWriter writer = new FileWriter(csvFile)) {
                    writer.append(CSV_HEADER);
                    writer.append("\n");
                }
                System.out.println("Fichier CSV créé avec succès");
            } else {
                System.out.println("Fichier CSV existant trouvé : " + csvFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'initialisation",
                    "Impossible de créer ou d'accéder au fichier CSV: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String password = passwordField.getText();

        // Validation : champs vides
        if (nom.isEmpty() || prenom.isEmpty() || password.isEmpty()) {
            showErrorAlert("Champs manquants", "Veuillez remplir tous les champs");
            return;
        }

        // Validation : nom et prénom ne doivent pas contenir de chiffres
        if (nom.matches(".*\\d.*") || prenom.matches(".*\\d.*")) {
            showErrorAlert("Format incorrect", "Le nom et le prénom ne peuvent pas contenir de chiffres");
            return;
        }

        // Validation : mot de passe doit avoir au moins 6 caractères
        if (password.length() < 6) {
            showErrorAlert("Mot de passe trop court",
                    "Votre mot de passe ne fait que " + password.length() +
                            " caractères. Il doit contenir au moins 6 caractères.");
            return;
        }

        // Si toutes les validations sont passées, enregistrer l'utilisateur
        try {
            // Ajouter le nouvel utilisateur
            try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
                writer.append(nom);
                writer.append(",");
                writer.append(prenom);
                writer.append(",");
                writer.append(password);
                writer.append("\n");

                // Afficher l'alerte de succès
                showSuccessAlert("Inscription réussie",
                        "L'utilisateur " + prenom + " " + nom + " a été enregistré avec succès !");

                // Réinitialiser les champs
                nomField.clear();
                prenomField.clear();
                passwordField.clear();
                messageLabel.setText("");
            }
        } catch (IOException e) {
            showErrorAlert("Erreur d'enregistrement", "Une erreur est survenue lors de l'enregistrement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        // Effacer tous les champs de texte
        nomField.clear();
        prenomField.clear();
        passwordField.clear();

        // Effacer aussi le message
        messageLabel.setText("");
    }

    @FXML
    private void showUsers() {
        try {
            // Vérifier d'abord si le fichier CSV existe
            File csvFile = new File(CSV_FILE);
            if (!csvFile.exists() || csvFile.length() <= CSV_HEADER.length() + 2) { // En-tête + saut de ligne
                showErrorAlert("Aucune donnée", "Aucun utilisateur n'a été enregistré pour le moment.");
                return;
            }

            // Charger la vue des utilisateurs
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des Utilisateurs");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur de navigation",
                    "Impossible d'ouvrir la liste des utilisateurs: " + e.getMessage());
        }
    }

    // Méthode utilitaire pour afficher une alerte d'erreur
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthode utilitaire pour afficher une alerte de succès
    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
