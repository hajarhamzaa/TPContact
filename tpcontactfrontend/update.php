<?php
header("Content-Type: application/json");
include 'db.php';

$data = json_decode(file_get_contents("php://input"));

$id = $data->id;
$nom = $data->nom;
$telephone = $data->telephone;
$email = $data->email;

$sql = "UPDATE contacts SET nom='$nom', telephone='$telephone', email='$email' WHERE id=$id";

if ($conn->query($sql) === TRUE) {
    echo json_encode(["message" => "Contact mis à jour"]);
} else {
    echo json_encode(["error" => "Erreur lors de la mise à jour : " . $conn->error]);
}
?>
