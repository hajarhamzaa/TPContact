<?php
header("Content-Type: application/json");
include 'db.php';

$data = json_decode(file_get_contents("php://input"));

$id = $data->id;

$sql = "DELETE FROM contacts WHERE id=$id";

if ($conn->query($sql) === TRUE) {
    echo json_encode(["message" => "Contact supprimé"]);
} else {
    echo json_encode(["error" => "Erreur lors de la suppression : " . $conn->error]);
}
?>

