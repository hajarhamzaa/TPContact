<?php
// Paramètres de connexion à la base de données
$servername = "localhost";  // Serveur MySQL
$username = "root";         // Utilisateur de MySQL
$password = "";             // Mot de passe (vide sur XAMPP)
$dbname = "contacts";        // Le nom de la base de données

// Création de la connexion
$conn = new mysqli($servername, $username, $password, $dbname);

// Vérification de la connexion
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Lire les données JSON envoyées dans le corps de la requête
$inputData = file_get_contents('php://input');

// Afficher les données brutes reçues pour déboguer
error_log($inputData);  // Ceci va écrire les données dans les logs PHP

// Convertir les données JSON en tableau PHP
$data = json_decode($inputData, true);

// Vérifier si les données sont bien reçues
if (isset($data['name']) && isset($data['phone_number'])) {
    $name = $data['name'];
    $phone_number = $data['phone_number'];

    // Requête SQL pour insérer le contact dans la base de données
    $sql = "INSERT INTO contact (name, phone_number) VALUES ('$name', '$phone_number')";

    if ($conn->query($sql) === TRUE) {
        echo json_encode(["message" => "Contact ajouté avec succès"]);
    } else {
        echo json_encode(["error" => "Erreur: " . $sql . "<br>" . $conn->error]);
    }
} else {
    echo json_encode(["error" => "Données manquantes"]);
}

// Fermer la connexion
$conn->close();
?>
