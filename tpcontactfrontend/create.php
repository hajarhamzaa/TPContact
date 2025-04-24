<?php
include 'db.php';

$data = json_decode(file_get_contents("php://input"));

$nom = $data->nom;
$telephone = $data->telephone;
$email = $data->email;

$sql = "INSERT INTO contacts (nom, telephone, email) VALUES ('$nom', '$telephone', '$email')";

if ($conn->query($sql) === TRUE) {
    echo json_encode(["message" => "Contact ajouté"]);
} else {
    echo json_encode(["error" => "Erreur : " . $conn->error]);
}
?>
