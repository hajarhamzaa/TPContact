<?php
header("Content-Type: application/json");
include 'db.php';

$sql = "SELECT * FROM contacts";
$result = $conn->query($sql);

$contacts = [];

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $contacts[] = $row;
    }
}

echo json_encode($contacts);
?>