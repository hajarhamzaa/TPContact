<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>TP6 - Gestionnaire de Contacts</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 0;
                padding: 20px;
                background-color: #f5f5f5;
            }
            .container {
                max-width: 800px;
                margin: 0 auto;
                background-color: white;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
            h1 {
                color: #333;
                text-align: center;
                margin-bottom: 30px;
            }
            .menu {
                display: flex;
                justify-content: center;
                margin-bottom: 30px;
            }
            .menu a {
                display: inline-block;
                margin: 0 10px;
                padding: 10px 20px;
                background-color: #4CAF50;
                color: white;
                text-decoration: none;
                border-radius: 4px;
                transition: background-color 0.3s;
            }
            .menu a:hover {
                background-color: #45a049;
            }
            .info {
                background-color: #e7f3fe;
                border-left: 4px solid #2196F3;
                padding: 15px;
                margin-bottom: 20px;
            }
            .status {
                margin-top: 20px;
                padding: 15px;
                background-color: #f9f9f9;
                border-radius: 4px;
            }
            .status-item {
                margin-bottom: 8px;
            }
            .status-ok {
                color: green;
            }
            .status-error {
                color: red;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>TP6 - Gestionnaire de Contacts</h1>
            
            <div class="menu">
                <a href="test-form.php">Tester l'API</a>
                <a href="view-contacts.php">Voir les Contacts</a>
                <a href="setup/create_database.php">Initialiser la BDD</a>
            </div>
            
            <div class="info">
                <p>Ce système permet de stocker des contacts depuis une application mobile dans une base de données MySQL.</p>
                <p>L'API est accessible à l'adresse <code>/api/contacts.php</code> et accepte les requêtes POST.</p>
            </div>
            
            <div class="status">
                <h3>État du système:</h3>
                <?php
                // Vérifier si les fichiers nécessaires existent
                $api_exists = file_exists('api/contacts.php');
                $db_config_exists = file_exists('config/database.php');
                $setup_exists = file_exists('setup/create_database.php');
                
                // Vérifier la connexion à la base de données
                $db_connected = false;
                if ($db_config_exists) {
                    include_once 'config/database.php';
                    $database = new Database();
                    $conn = $database->getConnection();
                    $db_connected = $conn ? true : false;
                }
                ?>
                
                <div class="status-item">
                    API (contacts.php): 
                    <span class="<?php echo $api_exists ? 'status-ok' : 'status-error'; ?>">
                        <?php echo $api_exists ? 'OK' : 'Non trouvé'; ?>
                    </span>
                </div>
                
                <div class="status-item">
                    Configuration BDD: 
                    <span class="<?php echo $db_config_exists ? 'status-ok' : 'status-error'; ?>">
                        <?php echo $db_config_exists ? 'OK' : 'Non trouvé'; ?>
                    </span>
                </div>
                
                <div class="status-item">
                    Script d'initialisation: 
                    <span class="<?php echo $setup_exists ? 'status-ok' : 'status-error'; ?>">
                        <?php echo $setup_exists ? 'OK' : 'Non trouvé'; ?>
                    </span>
                </div>
                
                <div class="status-item">
                    Connexion à la BDD: 
                    <span class="<?php echo $db_connected ? 'status-ok' : 'status-error'; ?>">
                        <?php echo $db_connected ? 'OK' : 'Échec'; ?>
                    </span>
                </div>
            </div>
        </div>
    </body>
</html>
